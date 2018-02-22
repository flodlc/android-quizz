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
                    WHERE g.user_a_id = " . $user->getId() . " OR g.user_b_id = " . $user->getId() . " and g.state > 0
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
     * Get a game with $user as a player (UserA or UserB in the game) and ready to play
     * @param User $user
     * @return array
     */
    public function getMyGameCurrent(User $user)
    {
        $gameRepo = $this->em->getRepository(Game::class);

        $gamesA = $gameRepo->findBy(["userA" => $user, "state" => 1, "pointsA" => null]);
        if (count($gamesA) > 0)
            return ["id" => $gamesA[0]->getId(), "state" => $gamesA[0]->getState()];

        $gamesB = $gameRepo->findBy(["userB" => $user, "state" => 1, "pointsB" => null]);
        if (count($gamesB) > 0)
            return ["id" => $gamesB[0]->getId(), "state" => $gamesB[0]->getState()];

        return null;
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
            $this->startOnlineGame($me, $game, false);
            $rounds = $this->roundManager->generateRounds($game);
            $game->setAdv($game->getUserA());
            $this->em->flush();
            return ["game" => $game, "rounds" => $rounds];
        }

        /**
         * Create a new party
         */
        return ["game" => $this->createGame($me), "rounds" => []];
    }

    /**
     * Get the game corresponding to the id and its rounds.
     * @param $idGame
     * @return array
     */
    public function getGameAndRounds($idGame, $idUser)
    {
        $gameRepo = $this->em->getRepository(Game::class);
        $userRepo = $this->em->getRepository(User::class);
        $game = $gameRepo->find($idGame);
        $user = $userRepo->find($idUser);

        if (!$game)
            throw new HttpException("Partie inexistante", 404);
        if (!$user)
            throw new HttpException("Utilisateur inexistant", 404);

        if ($game->getState() > 0) {
            $whoMe = $this->userManager->whoIAm($user, $game);
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
     * Create a new Game with $me as a UserA
     * @param User $me
     * @param bool $flush
     * @return Game
     */
    public function createGame(User $me, $flush = true)
    {
        $game = new Game();
        $game->setState(0);
        $game->setUserA($me);
        $this->em->persist($game);
        $this->em->flush();
        if ($flush) {
            $this->em->flush();
        }
        return $game;
    }

    /**
     * Set a party, which is waiting a second player, in order to be playable.
     * @param User $user
     * @param Game $game
     * @param bool $flush
     */
    public function startOnlineGame(User $user, Game $game, $flush = true)
    {
        $game->setUserB($user);
        $game->setState(1);
        $this->em->persist($game);
        if ($flush) {
            $this->em->flush();
        }
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