<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 09/02/2018
 * Time: 17:41
 */

namespace QuizzBundle\Controller;

use QuizzBundle\Entity\Game;
use SensioLabs\Security\Exception\HttpException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Doctrine\Common\Annotations\AnnotationReader;
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

class GameController extends Controller
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
     * @Route("/all", name="my_games")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getGamesAction(Request $request)
    {
        $userManager = $this->container->get("quizz.user");
        $gameManager = $this->container->get("quizz.game");
        $userId = $this->getUser()->getId();

        $user = $userManager->getById($userId);
        $games_tmp = $gameManager->getMyGames($user);

        return new Response($this->serializer->serialize($games_tmp, "json", ["groups" => ["mygames"]]));
    }

    /**
     * @Route("/current", name="games")
     * @Method({"GET"})
     *
     * @param Request $request
     * @return Response
     */
    public function getCurrentGameAction(Request $request)
    {
        $userManager = $this->container->get("quizz.user");
        $gameManager = $this->container->get("quizz.game");
        $userId = $this->getUser()->getId();

        $user = $userManager->getById($userId);
        $gameCurrent = $gameManager->getMyGameCurrent($user);

        return new Response(json_encode(["game" => $gameCurrent]));
    }

    /**
     * @Route("/", name="search")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getSearchAction(Request $request)
    {
        $userManager = $this->container->get("quizz.user");
        $gameManager = $this->container->get("quizz.game");

        $userId = $this->getUser()->getId();
        $me = $userManager->getById($userId);

        $game_data = $gameManager->getAGameAvailable($me);

        $gameSeria = $this->serializer->normalize($game_data["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($game_data["rounds"], "json", ["groups" => ["game"]]);

        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));
    }

    /**
     * @Route("/status", name="game_is_ready")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getStatusAction(Request $request)
    {
        $gameManager = $this->container->get("quizz.game");
        $gameRepo = $this->getDoctrine()->getManager()->getRepository(Game::class);
        $gameId = $request->get("game");
        $game = $gameRepo->find($gameId);
        $user = $this->getUser();

        $data_game = $gameManager->getGameAndRounds($game, $user);

        $gameSeria = $this->serializer->normalize($data_game["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($data_game["rounds"], "json", ["groups" => ["game"]]);
        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));
    }

    /**
     * @Route("/{id}", name="game_delete")
     * @Method({"DELETE"})
     *
     * @return Response
     */
    public function deleteGameAction(Request $request)
    {
        $gameManager = $this->container->get("quizz.game");
        $gameId = $request->get("id");
        if (!$gameId)
            throw new HttpException("L'identifiant de la partie est manquant.");
        /** @var Game $game */
        $game = $this->getDoctrine()->getManager()->getRepository(Game::class)->find($gameId);
        if ($game->getUserA()->getId() != $this->getUser()->getId() && $game->getUserB()->getId() != $this->getUser()->getId()) {
            return new Response("", 403);
        }
        $removeState = $gameManager->deleteGame($game);
        return new Response(json_encode($removeState));
    }
}