<?php

namespace QuizzBundle\Controller;

use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Question;
use QuizzBundle\Entity\Round;
use QuizzBundle\Entity\User;
use SensioLabs\Security\Exception\HttpException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Doctrine\Common\Annotations\AnnotationReader;
use Doctrine\ORM\EntityNotFoundException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Mapping\Loader\AnnotationLoader;
use Symfony\Component\Serializer\Normalizer\DateTimeNormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Mapping\Factory\ClassMetadataFactory;

class DefaultController extends Controller
{
    /**
     * @var Serializer
     */
    private $serializer;

    /**
     * ActeurController constructor.
     */
    public function __construct()
    {
        $classMetadataFactory = new ClassMetadataFactory(new AnnotationLoader(new AnnotationReader()));
        $this->serializer = new Serializer([new DateTimeNormalizer("d/m/Y"), new ObjectNormalizer($classMetadataFactory)], [new JsonEncoder()]);
    }

    /**
     * @Route("/user", name="user")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getMeAction(Request $request)
    {
        $username = $request->get("user");

        $entityManager = $this->getDoctrine()->getManager();
        $userRepo = $entityManager->getRepository(User::class);

        $me = $userRepo->findBy(["username" => $username]);
        if (!$me) {
            $me = new User();
            $me->setUsername($username);
            $entityManager->persist($me);
            $entityManager->flush();
        }

        return new Response($this->serializer->serialize($me, "json", ["groups" => ["user"]]));
    }

    /**
     * @Route("/games", name="games")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getGamesAction(Request $request)
    {
        $userId = $request->get("user");

        $entityManager = $this->getDoctrine()->getManager();
        $gameRepo = $entityManager->getRepository(Game::class);
        $userRepo = $entityManager->getRepository(User::class);

        $user = $userRepo->find($userId);
        $gamesA = $gameRepo->findBy(["userA" => $user]);
        $gamesB = $gameRepo->findBy(["userB" => $user]);
        $games_tmp = array_merge($gamesA, $gamesB);

        return new Response($this->serializer->serialize($games_tmp, "json", ["groups" => ["game"]]));
    }

    /**
     * @Route("/game", name="search")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getSearchAction(Request $request)
    {
        $userId = $request->get("user");
        if (!$userId) {
            throw new HttpException("Pas d'utilisateur", 404);
        }
        $entityManager = $this->getDoctrine()->getManager();
        $userRepo = $entityManager->getRepository(User::class);
        $gameRepo = $entityManager->getRepository(Game::class);
        $me = $userRepo->find($userId);
        $games = $gameRepo->findBy(["state" => 0]);
        if (count($games) > 0 && $games[0]->getUserA() != $me) {
            $questRepo = $entityManager->getRepository(Question::class);

            do {
                $rd = rand(0, sizeof($games) - 1);
            } while ($games[$rd]->getUserA() == $me);

            $game = $games[$rd];
            $game->setUserB($me);
            $game->setState(1);
            $entityManager->persist($game);
            $entityManager->flush();


            $questions = $questRepo->findAll();

            $rounds = [];
            for ($i = 1; $i <= 3; $i++) {
                $keyQuestions = array_keys($questions);
                $rd = rand(0, sizeof($keyQuestions) - 1);
                $round = new Round();
                $round->setState(0);
                $round->setGame($game);
                $round->setNumRound($i);
                $round->setQuestion($questions[$rd]);
                $entityManager->persist($round);
                $entityManager->flush();
                unset($questions[$rd]);

                $rounds[] = $round;
            }

            $game->setAdv($game->getUserA()->getUsername());

            $gameSeria = $this->serializer->normalize($game, "json", ["groups" => ["game"]]);
            $roundsSeria = $this->serializer->normalize($rounds, "json", ["groups" => ["game"]]);

            return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));

        } else {
            $game = new Game();
            $game->setState(0);
            $game->setUserA($me);
            $entityManager->persist($game);
            $entityManager->flush();
            return new Response($this->serializer->serialize($game, "json", ["groups" => ["game"]]));
        }

    }

    /**
     * @Route("/game", name="answer")
     * @Method({"POST"})
     *
     *
     */
    public function postAnswerAction(Request $request)
    {
        $entityManager = $this->getDoctrine()->getManager();
        $userRepo = $entityManager->getRepository(User::class);
        $gameRepo = $entityManager->getRepository(Game::class);
        $roundRepo = $entityManager->getRepository(Round::class);

        $data = json_decode($request->getContent(), true);
        $userId = $data["user"];
        $gameId = $data["game"];
        $user = $userRepo->find($userId);
        $game = $gameRepo->find($gameId);
        if (!$user)
            throw new HttpException("Pas d'utilisateur", 404);

        if ($game->getUserA() == $user) {
            $whichUser = "A";
        } elseif ($game->getUserB() == $user) {
            $whichUser = "B";
        } else {
            throw new HttpException("Cet utilisateur n'est pas sur ce jeu.", 404);
        }

        $answers = $data["answers"];

        $myPoints = 0;
        foreach ($answers as $answer) {
            $roundId = $answer["roundId"];
            $answer = $answer["answer"];

            $response = new \QuizzBundle\Entity\Response();
            $response->setResponse($answer);
            $response->setState(1);
            $response->setUser($user);
            $entityManager->persist($response);
            $entityManager->flush();

            $round = $roundRepo->find($roundId);
            $question = $round->getQuestion();
            if ($question->getAnswer() == $answer) {
                $myPoints++;
            }

            if ($whichUser == "A") {
                $round->setResponseUA($response);
            } else {
                $round->setResponseUB($response);
            }
            $entityManager->persist($round);
            $entityManager->flush();
        }

        if ($whichUser == "A") {
            $game->setPointsA($myPoints);
            if ($game->getPointsB() != null) {
                if ($game->getPointsB() > $game->getPointsA()) {
                    $game->setWinner($game->getUserB()->getUsername());
                }elseif ($game->getPointsB() < $game->getPointsA()) {
                    $game->setWinner($game->getUserA()->getUsername());
                } else {
                    $game->setWinner("Nul");
                }
                $game->setState(2);
            }
        } else {
            $game->setPointsB($myPoints);
            if ($game->getPointsA() != null) {
                if ($game->getPointsB() > $game->getPointsA()) {
                    $game->setWinner($game->getUserB()->getUsername());
                }elseif ($game->getPointsB() < $game->getPointsA()) {
                    $game->setWinner($game->getUserA()->getUsername());
                } else {
                    $game->setWinner("Nul");
                }
                $game->setState(2);
            }
        }
        $entityManager->merge($game);
        $entityManager->flush();

        return new Response("", 200);
    }
}
