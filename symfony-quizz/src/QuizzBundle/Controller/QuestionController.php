<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 13/02/2018
 * Time: 16:40
 */

namespace QuizzBundle\Controller;

use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Question;
use QuizzBundle\Entity\User;
use QuizzBundle\Service\QuestionManager;
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
use Symfony\Component\PropertyInfo\Extractor\ReflectionExtractor;
use Symfony\Component\Serializer\Normalizer\ArrayDenormalizer;

class QuestionController extends Controller
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
     * @Route("/all", name="get_all_questions")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getQuestionsAction(Request $request)
    {
        $lastId = $request->get("lastId");
        if ($lastId) {
            $offset = $lastId;
        } else {
            $offset = 0;
        }
        $questionsRepo = $this->getDoctrine()->getManager()->getRepository(Question::class);
        $questions = $questionsRepo->findBy([], ["id" => "ASC"], 500, $offset);
        return new Response($this->serializer->serialize($questions, "json", ["groups" => ["questions"]]));
    }

    /**
     * @Route("create", name="postQuestions")
     * @Method({"POST"})
     *
     * @return Response
     */
    public function postQuestionsAction(Request $request)
    {
        /** @var array $questions */
        $questions = $this->serializer->deserialize($request->getContent(), Question::class . '[]', "json");

        /** @var QuestionManager $questionManager */
        $questionManager = $this->get("quizz.question");

        return new Response(json_encode($questionManager->saveQuestions($questions, $this->getUser())));
    }

    /**
     * @Route("edit", name="editQuestions")
     * @Method({"POST"})
     *
     * @return Response
     */
    public function editQuestionsAction(Request $request)
    {
        /** @var array $questions */
        $questions = $this->serializer->deserialize($request->getContent(), Question::class . '[]', "json");

        /** @var QuestionManager $questionManager */
        $questionManager = $this->get("quizz.question");

        return new Response($this->serializer->serialize($questionManager->editQuestions($questions), "json"));
    }
}