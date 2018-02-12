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
use QuizzBundle\Entity\Question;
use QuizzBundle\Entity\Response;
use QuizzBundle\Entity\Round;
use SensioLabs\Security\Exception\HttpException;

class RoundManager
{
    private $em;
    private $nbRounds;
    private $questionManager;
    private $userManager;

    public function __construct(EntityManagerInterface $entityManager, $nbRounds, QuestionManager $questionManager,
                                UserManager $userManager)
    {
        $this->em = $entityManager;
        $this->nbRounds = $nbRounds;
        $this->questionManager = $questionManager;
        $this->userManager = $userManager;
    }

    /**
     * Return all rounds of the game in parameter.
     * @param Game $game
     * @return array
     */
    public function getRoundsOfGame(Game $game)
    {
        $roundRepo = $this->em->getRepository(Round::class);
        $rounds = $roundRepo->findBy(["game" => $game]);
        return $rounds;
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

    /**
     * Add the response in the round at the correct user column. Return true if the response is correct.
     * @param Round $round
     * @param Response $response
     * @return bool
     */
    public function addResponse(Round $round, Response $response)
    {
        if ($round->getState() > 1)
            throw new HttpException("Ce round (" . $round->getId() . ") est déjà plein.", 404);
        $user = $response->getUser();
        $game = $round->getGame();
        $myRole = $this->userManager->whoIAm($user, $game);
        if ($myRole == "A")
            $round->setResponseUA($response);
        else
            $round->setResponseUB($response);
        $round->setState($round->getState() + 1);
        $this->em->merge($round);
        $this->em->flush();
        return ($response->getResponse() == $round->getQuestion()->getAnswer());
    }

    /**
     * Return the object Question include in the round.
     * @param $idRound
     * @return Question
     */
    public function getQuestionById($idRound)
    {
        $roundRepo = $this->em->getRepository(Round::class);
        $round = $roundRepo->find($idRound);
        if (!$round)
            throw new HttpException("Pas de round correspondant", 404);
        return $round->getQuestion();
    }

}