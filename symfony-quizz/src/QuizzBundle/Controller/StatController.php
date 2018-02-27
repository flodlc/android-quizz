<?php
/**
 * Created by PhpStorm.
 * User: Florian
 * Date: 26/02/2018
 * Time: 00:59
 */

namespace QuizzBundle\Controller;


use Doctrine\Common\Annotations\AnnotationReader;
use QuizzBundle\Entity\User;
use QuizzBundle\Service\StatManager;
use QuizzBundle\Service\UserManager;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PropertyInfo\Extractor\ReflectionExtractor;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Mapping\Factory\ClassMetadataFactory;
use Symfony\Component\Serializer\Mapping\Loader\AnnotationLoader;
use Symfony\Component\Serializer\Normalizer\ArrayDenormalizer;
use Symfony\Component\Serializer\Normalizer\DateTimeNormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;

class StatController extends Controller
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
        $this->serializer = new Serializer([new DateTimeNormalizer("d/m/Y"), new ObjectNormalizer($classMetadataFactory,
            null, null, new ReflectionExtractor()), new ArrayDenormalizer()], [new JsonEncoder()]);
    }

    /**
     * @Route("/", name="stats")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getMyStatsAction()
    {
        /** @var UserManager $userManager */
        $userManager = $this->container->get("quizz.user");

        /** @var StatManager $statManager */
        $statManager = $this->container->get("quizz.stat");

        $responseArray = [
            "myStats" => $statManager->getGoodResponseRate($this->getUser()),
            "globalStats" => $statManager->getGlobalGoodResponseRate(),
            "bestUsers" => $this->serializer->normalize($userManager->getBestUsers(5), null, ["groups" => ["stats"]])
        ];
        return new Response(json_encode($responseArray));
    }

    /**
     * @Route("/up", name="upStats")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function upStat()
    {
        /** @var StatManager $statManager */
        $statManager = $this->container->get("quizz.stat");

        $users = $this->getDoctrine()->getManager()->getRepository(User::class)->findAll();

        foreach ($users as $user) {
            if (!$user->getStat()) {
                $user->setStat($statManager->makeStatFromOfflineTable($user));
                $this->getDoctrine()->getManager()->flush();
            }
        }

        return new Response("ok");
    }
}