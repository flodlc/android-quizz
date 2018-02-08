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
        $userManager = $this->container->get("quizz.user");
        $me = $userManager->getByUsername($username);

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
        $userManager = $this->container->get("quizz.user");
        $gameManager = $this->container->get("quizz.game");
        $userId = $request->get("user");

        $user = $userManager->getById($userId);
        $games_tmp = $gameManager->getMyGames($user);

        return new Response($this->serializer->serialize($games_tmp, "json", ["groups" => ["mygames"]]));
    }

    /**
     * @Route("/game", name="search")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getSearchAction(Request $request)
    {
        $entityManager = $this->getDoctrine()->getManager();
        $userManager = $this->container->get("quizz.user");
        $gameManager = $this->container->get("quizz.game");

        $userId = $request->get("user");
        $me = $userManager->getById($userId);

        $game_data = $gameManager->getAGameAvailable($me);

        $gameSeria = $this->serializer->normalize($game_data["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($game_data["rounds"], "json", ["groups" => ["game"]]);

        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));

    }

    /**
     * @Route("/statusgame", name="game_ready")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getStatusAction(Request $request)
    {
        $entityManager = $this->getDoctrine()->getManager();
        $gameRepo = $entityManager->getRepository(Game::class);
        $roundRepo = $entityManager->getRepository(Round::class);
        $gameId = $request->get("game");
        if (!$gameId) {
            throw new HttpException("Pas de partie", 404);
        }

        $game = $gameRepo->find($gameId);

        $userManager = $this->container->get("quizz.user");
        $userId = $request->get("user");
        $me = $userManager->getById($userId);


        if (!$game) {
            throw new HttpException("Pas de partie", 404);
        }
        if ($game->getState() == 0) {
            throw new HttpException("Pas d'adversaire", 404);
        }

        $rounds = $roundRepo->findBy(["game" => $game]);
        $gameSeria = $this->serializer->normalize($game, "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($rounds, "json", ["groups" => ["game"]]);
        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));

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
        $gameRepo = $entityManager->getRepository(Game::class);
        $roundRepo = $entityManager->getRepository(Round::class);
        $userManager = $this->container->get("quizz.user");

        $data = json_decode($request->getContent(), true);
        $userId = $data["user"];
        $gameId = $data["game"];

        $user = $userManager->getById($userId);

        $game = $gameRepo->find($gameId);
        if ($game->getState() == 2) {
            throw new HttpException("Partie terminée", 404);
        }
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
                    $game->setWinner($game->getUserB());
                } elseif ($game->getPointsB() < $game->getPointsA()) {
                    $game->setWinner($game->getUserA());
                } else {
                    $game->setWinner(null);
                }
                $game->setState(2);
            }
        } else {
            $game->setPointsB($myPoints);
            if ($game->getPointsA() != null) {
                if ($game->getPointsB() > $game->getPointsA()) {
                    $game->setWinner($game->getUserB());
                } elseif ($game->getPointsB() < $game->getPointsA()) {
                    $game->setWinner($game->getUserA());
                } else {
                    $game->setWinner(null);
                }
                $game->setState(2);
            }
        }
        $entityManager->merge($game);
        $entityManager->flush();

        return new Response("", 200);
    }
}
