<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 17:39
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\Query\ResultSetMappingBuilder;
use QuizzBundle\Entity\Question;
use QuizzBundle\Repository\QuestionRepository;
use Symfony\Component\HttpKernel\Exception\HttpException;
use Symfony\Component\Security\Core\Authorization\AuthorizationChecker;


class QuestionManager
{
    private $em;
    private $nbRounds;

    /** @var AuthorizationChecker */
    private $authorizationChecker;

    public function __construct(EntityManagerInterface $entityManager, AuthorizationChecker $authorizationChecker, $nbRounds)
    {
        $this->em = $entityManager;
        $this->nbRounds = $nbRounds;
        $this->authorizationChecker = $authorizationChecker;
    }

    /**
     * Generate an array with random questions. The number of questions depends on the parameter nb_rounds.
     * @return array
     */
    public function generateQuestions()
    {
        $sqlQuery = "SELECT * 
                    FROM `question` q
                    WHERE q.enabled = 1 ORDER BY RAND() LIMIT " . $this->nbRounds . ";";
        $rsm = new ResultSetMappingBuilder($this->em);
        $rsm->addRootEntityFromClassMetadata(Question::class, 'question');
        $q = $this->em->createNativeQuery($sqlQuery, $rsm);
        $questions = $q->getResult();
        if (array_key_exists($this->nbRounds - 1, $questions)) {
            return $questions;
        } else {
            throw new HttpException(500, "Not enough questions");
        }
    }


    /**
     * @param array $questions
     * @return boolean
     */
    public function saveQuestions(array $questions, $user)
    {
        foreach ($questions as $question) {
            $question->setUser($user);
            $this->saveQuestion($question, false);
        }
        $this->em->flush();
        return true;
    }

    /**
     * @param array $questions
     * @return boolean
     */
    public function editQuestions(array $questions)
    {
        foreach ($questions as $question) {
            $this->editQuestion($question, false);
        }
        $this->em->flush();
        return true;
    }

    /**
     * @param Question $question
     * @param bool $flush
     * @return bool
     */
    public function saveQuestion(Question $question, $flush = true)
    {
        if ($question->getQuestion() && $question->getResponseA() && $question->getResponseB()
            && $question->getResponseC() && $question->getResponseD() && $question->getAnswer()
        ) {
            if ($this->authorizationChecker->isGranted('ROLE_ADMIN')) {
                $question->setEnabled(true);
            } else {
                $question->setEnabled(false);
            }

            $this->em->persist($question);
            if ($flush) {
                $this->em->flush();
            }
            return true;
        } else {
            throw new HttpException(400, "missing date for question : " . $question->getQuestion());
        }
    }

    public function editQuestion(Question $question, $flush = true)
    {
        if ($id = $question->getId()) {
            /** @var QuestionRepository $questionRepo */
            $questionRepo = $this->em->getRepository(Question::class);

            /** @var Question $currentQuestion */
            $currentQuestion = $questionRepo->find($id);
            if (!$currentQuestion) {
                throw new HttpException(400, "id not found : " . $question->getId());
            }
            $currentQuestion->setAnswer($question->getAnswer());
            $currentQuestion->setResponseA($question->getResponseA());
            $currentQuestion->setResponseB($question->getResponseB());
            $currentQuestion->setResponseC($question->getResponseC());
            $currentQuestion->setResponseD($question->getResponseD());
            $currentQuestion->setQuestion($question->getQuestion());

            if ($this->authorizationChecker->isGranted('ROLE_ADMIN')) {
                $currentQuestion->setEnabled($question->isEnabled());
            }

            if ($flush) {
                $this->em->flush();
            }
            return true;
        }
        throw new HttpException(400, "missing id for : " . $question->getQuestion());
    }
}