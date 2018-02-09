<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 16:43
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Round;
use QuizzBundle\Entity\User;
use SensioLabs\Security\Exception\HttpException;

class ResponseManager
{

    private $em;
    private $userManager;
    private $roundManager;

    public function __construct(EntityManagerInterface $entityManager, UserManager $userManager, RoundManager $roundManager)
    {
        $this->em = $entityManager;
        $this->userManager = $userManager;
        $this->roundManager = $roundManager;
    }

    public function saveAnswers($idUser, $idGame, $answers)
    {
        $userRepo = $this->em->getRepository(User::class);
        $gameRepo = $this->em->getRepository(Game::class);
        $roundRepo = $this->em->getRepository(Round::class);
        $myPoints = 0;

        $user = $userRepo->find($idUser);
        if (!$user)
            throw new HttpException("Pas d'utilisateur", 404);
        $game = $gameRepo->find($idGame);
        if (!$game)
            throw new HttpException("Pas de partie liée", 404);
        if (2 == $game->getState())
            throw new HttpException("Partie terminée", 200);

        $myRole = $this->userManager->whoIAm($user, $game);

        foreach ($answers as $answer) {
            $response = $this->createResponse($user, $answer["answer"]);
            $question = $this->roundManager->getQuestionById($answer["roundId"]);
            $round = $roundRepo->find($answer["roundId"]);
            if ($question->getAnswer() == $response->getResponse())
                $myPoints++;
            $this->roundManager->addResponse($round, $response);
        }


        if ("A" == $myRole) {
            $game->setPointsA($myPoints);
            $game->setAdv($game->getUserB()->getUsername());
        } else {
            $game->setPointsB($myPoints);
            $game->setAdv($game->getUserA()->getUsername());
        }

        if ($game->getPointsA() != null && $game->getPointsB() != null) {
            switch ($this->getWinner($game->getPointsB(), $game->getPointsA())) {
                case "A":
                    $game->setWinner($game->getUserB());
                    break;
                case "B":
                    $game->setWinner($game->getUserA());
                    break;
                default:
                    break;
            }
            $game->setState(2);
        }
        $this->em->merge($game);
        $this->em->flush();


        return $game;
    }

    /**
     * Return if it's A or B the winner or if it's a draw.
     * @param $pointsA
     * @param $pointsB
     * @return string
     */
    public function getWinner($pointsA, $pointsB)
    {
        if ($pointsB > $pointsA) {
            return "A";
        } elseif ($pointsB < $pointsA) {
            return "B";
        } else {
            return "NUL";
        }
    }

    public function createResponse(User $user, $answer)
    {

        $response = new \QuizzBundle\Entity\Response();
        $response->setResponse($answer);
        $response->setState(1);
        $response->setUser($user);
        $this->em->persist($response);
        $this->em->flush();
        return $response;
    }

}