<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 09/02/2018
 * Time: 17:42
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

class ResponseController extends Controller
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
     * Send all response of a user
     * @Route("/", name="answer")
     * @Method({"POST"})
     */
    public function postAnswerAction(Request $request)
    {
        $responseManager = $this->container->get("quizz.response");
        $roundManager = $this->container->get("quizz.round");

        $data = json_decode($request->getContent(), true);
        $game = $responseManager->saveAnswers($data["user"], $data["game"], $data["answers"]);
        $rounds = $roundManager->getRoundsOfGame($game);

        return new Response(json_encode(["game" => $this->serializer->normalize($game, null, ["groups" => ["game"]]),
            "rounds" => $this->serializer->normalize($rounds, null, ["groups" => ["game"]])]));
    }
}