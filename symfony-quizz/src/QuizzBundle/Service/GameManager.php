<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 16:43
 */

namespace QuizzBundle\Service;


use ClassesWithParents\G;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\Query\ResultSetMappingBuilder;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Round;
use QuizzBundle\Entity\User;
use QuizzBundle\Repository\GameRepository;
use SensioLabs\Security\Exception\HttpException;

class GameManager
{
    private $em;
    private $roundManager;
    private $userManager;

    public function __construct(EntityManagerInterface $entityManager, RoundManager $roundManager, UserManager $userManager)
    {
        $this->em = $entityManager;
        $this->roundManager = $roundManager;
        $this->userManager = $userManager;
    }

    /**
     * Get all games with $user as a player (UserA or UserB in the game)
     * @param User $user
     * @return array
     */
    public function getMyGames(User $user)
    {

        $sqlQuery = "SELECT * 
                    FROM `game` g
                    WHERE g.user_a_id = " . $user->getId() . " OR g.user_b_id = " . $user->getId() . " AND g.state > 0
                    ORDER BY id DESC LIMIT 30;";
        $rsm = new ResultSetMappingBuilder($this->em);
        $rsm->addRootEntityFromClassMetadata(Game::class, 'game');
        $q = $this->em->createNativeQuery($sqlQuery, $rsm);
        $games = $q->getResult();
        foreach ($games as $ind => $game) {
            if ($user == $game->getUserA()) {
                $game->setAdv($game->getUserB());
                if (null === $game->getPointsA())
                    array_splice($games, $ind, 1);
            } else {
                $game->setAdv($game->getUserA());
                if (null === $game->getPointsB())
                    array_splice($games, $ind, 1);
            }
        }
        return $games;
    }

    /**
     * Get a game with $user as a player (UserA or UserB in the game) and he have to play
     * @param User $user
     * @return Game
     */
    public function getMyGameCurrent(User $user)
    {
        /** @var GameRepository $gameRepo */
        $gameRepo = $this->em->getRepository(Game::class);
        $game = $gameRepo->getAvailableGames($user, 1);

        if (count($game) > 0) {
            return $gameRepo->getAvailableGames($user, 1)[0];
        }

        return null;
    }


    /**
     * @param User $user
     * @return int
     */
    public function countMyGameCurrent(User $user)
    {
        /** @var GameRepository $gameRepo */
        $gameRepo = $this->em->getRepository(Game::class);
        return $gameRepo->countAvailablesGame($user);
    }

    /**
     * Get a game available, if it's possible. Otherwise, a new Game will be returned.
     * @param User $me
     * @return array
     */
    public function getAGameAvailable(User $me)
    {
        $gameRepo = $this->em->getRepository(Game::class);

        /**
         * He's already looking for an adversaire
         */
        $game = $gameRepo->findOneBy(["userA" => $me, "state" => 0]);
        if ($game)
            return ["game" => $game, "rounds" => []];


        /** @var Game $gameA */
        $gameA = $gameRepo->findOneBy(["userA" => $me, "state" => 1, "pointsA" => null]);
        if ($gameA) {
            $gameA->setAdv($gameA->getUserB());
            return ["game" => $gameA, "rounds" => $this->roundManager->getRoundsOfGame($gameA)];
        }

        /** @var Game $gameB */
        $gameB = $gameRepo->findOneBy(["userB" => $me, "state" => 1, "pointsB" => null]);
        if ($gameB) {
            $gameB->setAdv($gameB->getUserA());
            return ["game" => $gameB, "rounds" => $this->roundManager->getRoundsOfGame($gameB)];
        }


        $games = $gameRepo->findBy(["state" => 0]);
        /**
         * There is a party which is waiting for someone
         */
        if (count($games) > 0) {
            /** @var Game $game */
            $game = $games[array_rand($games)];
            $this->setUserB($me, $game, false);
            $rounds = $this->roundManager->generateRounds($game);
            $game->setAdv($game->getUserA());
            $this->em->flush();
            return ["game" => $game, "rounds" => $rounds];
        }

        /**
         * Create a new party
         */
        return ["game" => $this->createGameUserA($me), "rounds" => []];
    }

    /**
     * Get the game corresponding to the id and its rounds.
     * @param Game $game
     * @param User $me
     * @return array
     */
    public function getGameAndRounds(Game $game, User $me)
    {
        if (!$game)
            throw new HttpException("Partie inexistante", 404);
        if (!$me)
            throw new HttpException("Utilisateur inexistant", 404);

        if ($game->getState() > 0) {
            $whoMe = $this->userManager->whoIAm($me, $game);
            if ("A" == $whoMe) {
                $game->setAdv($game->getUserB());
            } else {
                $game->setAdv($game->getUserA());
            }
        }

        $rounds = $this->roundManager->getRoundsOfGame($game);

        return ["game" => $game, "rounds" => $rounds];
    }

    /**
     * Create an online game with two players, return the Game and every Rounds
     * @param User $userA
     * @param User $userB
     * @return array
     */
    public function createOnlineGame(User $userA, User $userB)
    {
        $game = $this->createGameUserA($userA);
        $game = $this->setUserB($userB, $game);
        $rounds = $this->roundManager->generateRounds($game);
        return ["game" => $game, "rounds" => $rounds];
    }

    /**
     * Create a new Game with $me as a UserA
     * @param User $me
     * @return Game
     */
    public function createGameUserA(User $me)
    {
        $game = new Game();
        $game->setState(0);
        $game->setUserA($me);
        $game->setCreationDate(new \DateTime());
        $this->em->persist($game);
        $this->em->flush();
        return $game;
    }

    /**
     * Set a party, which is waiting a second player, in order to be playable.
     * @param User $userB
     * @param Game $game
     * @param bool $flush
     * @return Game
     */
    public function setUserB(User $userB, Game $game, $flush = true)
    {
        $game->setBeginDate(new \DateTime());
        $game->setUserB($userB);
        $game->setState(1);
        $this->em->persist($game);
        if ($flush) {
            $this->em->flush();
        }
        return $game;
    }

    /**
     * Remove a game when its state is at 0. Return true if the game is remove, otherwise throw error 500.
     * @param Game $game
     * @return bool
     */
    public function deleteGame(Game $game)
    {
        if ($game->getState() != 0)
            throw new HttpException("Partie déjà lancée", 500);
        $this->em->remove($game);
        $this->em->flush();
        return true;
    }
}