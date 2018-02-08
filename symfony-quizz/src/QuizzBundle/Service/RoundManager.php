<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 17:51
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Round;

class RoundManager
{
    private $em;
    private $nbRounds;
    private $questionManager;

    public function __construct(EntityManagerInterface $entityManager, $nbRounds, QuestionManager $questionManager)
    {
        $this->em = $entityManager;
        $this->nbRounds = $nbRounds;
        $this->questionManager = $questionManager;
    }

    /**
     * Generate rounds for a game, with question. The number of rounds depends on the parameter nb_round.
     * @param Game $game
     * @return array
     */
    public function generateRounds(Game $game)
    {
        $rounds = [];
        $questions = $this->questionManager->generateQuestions();
        for ($i = 0; $i < $this->nbRounds; $i++) {
            $round = new Round();
            $round->setState(0);
            $round->setNumRound($i + 1);
            $round->setGame($game);
            $round->setQuestion($questions[$i]);
            $this->em->persist($round);
            $this->em->flush();
            $rounds[] = $round;
        }

        return $rounds;

    }

}