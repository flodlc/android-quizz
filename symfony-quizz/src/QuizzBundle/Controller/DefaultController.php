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
        $gameManager = $this->container->get("quizz.game");
        $gameId = $request->get("game");
        if (!$gameId)
            throw new HttpException("L'identifiant de la partie est manquant.");

        $data_game = $gameManager->getGameAndRounds($gameId);

        $gameSeria = $this->serializer->normalize($data_game["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($data_game["rounds"], "json", ["groups" => ["game"]]);
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
        $responseManager = $this->container->get("quizz.response");

        $data = json_decode($request->getContent(), true);
        $game = $responseManager->saveAnswers($data["user"], $data["game"], $data["answers"]);
        return new Response($this->serializer->serialize($game, "json", ["groups" => ["game"]]));

    }
}
