<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 09/02/2018
 * Time: 17:41
 */

namespace QuizzBundle\Controller;

use FOS\UserBundle\Form\Factory\FormFactory;
use FOS\UserBundle\FOSUserBundle;
use QuizzBundle\Entity\User;
use QuizzBundle\Service\OfflineGameManager;
use QuizzBundle\Service\UserManager;
use SensioLabs\Security\Exception\HttpException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Doctrine\Common\Annotations\AnnotationReader;
use Symfony\Component\Config\Definition\Exception\Exception;
use Symfony\Component\HttpFoundation\JsonResponse;
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
use Symfony\Component\PropertyInfo\Extractor\ReflectionExtractor;
use Symfony\Component\Serializer\Normalizer\ArrayDenormalizer;

class UserController extends Controller
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
     * @Route("/createUser", name="createUser")
     * @Method({"POST"})
     * @param Request $request
     * @return Response
     */
    public function createUserAction(Request $request)
    {
        $user = $this->serializer->deserialize($request->getContent(), User::class, "json");

        /**@var $userManager UserManager */
        $userManager = $this->container->get("quizz.user");
        $userManager->createUser($user, $request->getClientIp());
        return new Response("true");
    }

    /**
     * @Route("/editUser", name="editUser")
     * @Method({"POST"})
     * @param Request $request
     * @return Response
     */
    public function editUserAction(Request $request)
    {
        $user = $this->serializer->deserialize($request->getContent(), User::class, "json");

        /**@var $userManager UserManager */
        $userManager = $this->container->get("quizz.user");
        $userManager->editUser($user);
        return new Response($this->serializer->serialize($this->getUser(), "json", ["groups" => ["user"]]));
    }


    /**
     * @Route("/success", name="success")
     * @Method({"GET"})
     */
    public function successAction(Request $request)
    {
        return new Response($this->serializer->serialize($this->getUser(), "json", ["groups" => ["user"]]));
    }


    /**
     * @Route("/fail", name="fail")
     */
    public function testFailureAction()
    {
        return new Response("false", 403);
    }

    /**
     * @Route("/user", name="user")
     * @Method({"GET"})
     *
     * @param Request $request
     * @return Response
     */
    public function getMeAction(Request $request)
    {
        /** @var User $user */
        $user = $this->getUser();
        $userManager = $this->container->get("quizz.user");
        $userManager->visitUser($user, $request->getClientIp());
        return new Response($this->serializer->serialize($user, "json", ["groups" => ["user"]]));
    }
}