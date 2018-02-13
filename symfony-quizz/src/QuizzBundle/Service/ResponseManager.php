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

    /**
     * @param $idUser
     * @param $idGame
     * @param $answers
     * @return object
     */
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
        $myRole = $this->userManager->whoIAm($user, $game);

        /**
         * Check if game is ended
         */
        if (2 == $game->getState())
            throw new HttpException("Partie terminée", 200);
        /**
         * Check if game is ended
         */
        if (0 == $game->getState())
            throw new HttpException("Partie en attente d'un adversaire", 200);

        if ("A" == $myRole && $game->getPointsA() !== null
            || "B" == $myRole && $game->getPointsB() !== null) {
            throw new HttpException("Tu as déjà répondu pour cette partie!", 500);
        }


        $rounds = $this->roundManager->getRoundsOfGame($game);
        foreach ($answers as $answer) {
            $round = $roundRepo->find($answer["roundId"]);
            if (!in_array($round, $rounds))
                throw new HttpException("Ce round (" . $round->getId() . ") n'appartient pas à la partie en cours (" . $game->getId() . ").", 200);
        }

        foreach ($answers as $answer) {
            $round = $roundRepo->find($answer["roundId"]);
            $response = $this->createResponse($user, $answer["answer"]);

            if ($this->roundManager->addResponse($round, $response))
                $myPoints++;
            $index = array_search($round, $rounds);
            unset($rounds[$index]);
        }

        /**
         * Check if some rounds had not be answered by the player. If true, we set the round at "played by the player"
         */
        if (count($rounds) > 0) {
            foreach ($rounds as $round) {
                $round->setState($round->getState() + 1);
            }
        }


        if ("A" == $myRole) {
            $game->setPointsA($myPoints);
            $game->setAdv($game->getUserB());
        } else {
            $game->setPointsB($myPoints);
            $game->setAdv($game->getUserA());
        }

        if ($game->getPointsA() !== null && $game->getPointsB() !== null) {
            switch ($this->getWinner($game->getPointsA(), $game->getPointsB())) {
                case "A":
                    $game->setWinner($game->getUserA());
                    break;
                case "B":
                    $game->setWinner($game->getUserB());
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
    public
    function getWinner($pointsA, $pointsB)
    {
        if ($pointsB > $pointsA) {
            return "B";
        } elseif ($pointsB < $pointsA) {
            return "A";
        } else {
            return "NUL";
        }
    }

    public
    function createResponse(User $user, $answer)
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