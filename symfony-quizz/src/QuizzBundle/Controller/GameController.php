<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 09/02/2018
 * Time: 17:41
 */

namespace QuizzBundle\Controller;

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
     * @Route("/all", name="games")
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
     * @Route("/", name="search")
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
     * @Route("/status", name="game_is_ready")
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
}