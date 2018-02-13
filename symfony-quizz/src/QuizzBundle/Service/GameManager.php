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
        $gameRepo = $this->em->getRepository(Game::class);
        $gamesA = $gameRepo->findBy(["userA" => $user]);
        foreach ($gamesA as $game) {
            if ($game->getState() > 0)
                $game->setAdv($game->getUserB());
        }
        $gamesB = $gameRepo->findBy(["userB" => $user]);
        foreach ($gamesB as $game) {
            if ($game->getState() > 0)
                $game->setAdv($game->getUserA());
        }
        return array_merge($gamesA, $gamesB);
    }

    /**
     * Get a game with $user as a player (UserA or UserB in the game) and ready to play
     * @param User $user
     * @return array
     */
    public function getMyGameCurrent(User $user)
    {
        $gameRepo = $this->em->getRepository(Game::class);
        $gamesA = $gameRepo->findBy(["userA" => $user, "state" => 1]);
        foreach ($gamesA as $game) {
            if ($game->getPointsA() === null)
                return ["id" => $game->getId(), "state" => $game->getState()];
        }
        $gamesB = $gameRepo->findBy(["userB" => $user, "state" => 1]);
        foreach ($gamesB as $game) {
            if ($game->getPointsB() === null)
                return ["id" => $game->getId(), "state" => $game->getState()];
        }
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
        $mySearch = $gameRepo->findBy(["userA" => $me, "state" => 0]);
        if (count($mySearch) > 0) {
            $mySearch = $mySearch[0];
            if (0 == $mySearch->getState())
                return ["game" => $mySearch, "rounds" => []];
        }

        $games = $gameRepo->findBy(["state" => 0]);

        if (count($games) > 0 && $games[0]->getUserA() != $me) {

            $index_games = array_keys($games);
            do {
                $rd = array_rand($index_games);
                unset($index_games[$rd]);
                $game = $games[$rd];
            } while ($game->getUserA() == $me);

            $this->startOnlineGame($me, $game);
            $rounds = $this->roundManager->generateRounds($game);

            return ["game" => $game, "rounds" => $rounds];
        }

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
     * @return Game
     */
    public function createGame(User $me)
    {
        $game = new Game();
        $game->setState(0);
        $game->setUserA($me);
        $this->em->persist($game);
        $this->em->flush();
        return $game;
    }

    /**
     * Set a party, which is waiting a second player, in order to be playable.
     * @param User $user
     * @param Game $game
     */
    public function startOnlineGame(User $user, Game $game)
    {
        $game->setUserB($user);
        $game->setState(1);
        $this->em->persist($game);
        $this->em->flush();
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