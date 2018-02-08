<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 17:39
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Question;
use SensioLabs\Security\Exception\HttpException;

class QuestionManager
{
    private $em;
    private $nbRounds;

    public function __construct(EntityManagerInterface $entityManager, $nbRounds)
    {
        $this->em = $entityManager;
        $this->nbRounds = $nbRounds;
    }

    /**
     * Generate an array with random questions. The number of questions depends on the parameter nb_rounds.
     * @return array
     */
    public function generateQuestions()
    {
        $questionRepo = $this->em->getRepository(Question::class);
        $questions = [];

        $questions_all = $questionRepo->findAll();
        if (count($questions_all) < $this->nbRounds)
            throw new HttpException("Il n'y a pas assez de question en stock !", 404);

        $index_questions = array_rand($questions_all, $this->nbRounds);
        foreach ($index_questions as $index) {
            $questions[] = $questions_all[$index];
        }

        return $questions;

    }

}