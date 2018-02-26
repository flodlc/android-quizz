<?php
/**
 * Created by PhpStorm.
 * User: Florian
 * Date: 25/02/2018
 * Time: 19:18
 */

namespace QuizzBundle\Controller;


use Doctrine\Common\Annotations\AnnotationReader;
use QuizzBundle\Entity\OfflineGame;
use QuizzBundle\Entity\User;
use QuizzBundle\Service\OfflineGameManager;
use QuizzBundle\Service\UserManager;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Mapping\Factory\ClassMetadataFactory;
use Symfony\Component\Serializer\Mapping\Loader\AnnotationLoader;
use Symfony\Component\Serializer\Normalizer\DateTimeNormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;


class OfflineGameController extends Controller
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
        $this->serializer = new Serializer([new DateTimeNormalizer("d/m/Y"),
            new ObjectNormalizer($classMetadataFactory)], [new JsonEncoder()]);
    }

    /**
     * @Route("/create", name="createOffGame")
     * @Method({"POST"})
     *
     * @param Request $request
     * @return Response
     */
    public function postOfflineGameAction(Request $request)
    {
        /** @var OfflineGame $offlineGame */
        $offlineGame = $this->serializer->deserialize($request->getContent(), OfflineGame::class, "json");

        /** @var OfflineGameManager $offlineGameManager */
        $offlineGameManager = $this->container->get("quizz.off_game");

        /** @var User $user */
        $user = $this->getUser();

        $offlineGame->setUser($user);

        if ($offlineGame->getScore() > $user->getRecord()) {
            $user->setRecord($offlineGame->getScore());
        }

        $offlineGameManager->saveOfflineGame($offlineGame);

        return new Response("", 200);
    }
}