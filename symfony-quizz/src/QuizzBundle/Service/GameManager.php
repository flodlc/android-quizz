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
use QuizzBundle\Entity\User;

class GameManager
{
    private $em;
    private $roundManager;

    public function __construct(EntityManagerInterface $entityManager, RoundManager $roundManager)
    {
        $this->em = $entityManager;
        $this->roundManager = $roundManager;
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
                $game->setAdv($game->getUserB()->getUsername());
        }
        $gamesB = $gameRepo->findBy(["userB" => $user]);
        foreach ($gamesB as $game) {
            if ($game->getState() > 0)
                $game->setAdv($game->getUserA()->getUsername());
        }
        return array_merge($gamesA, $gamesB);
    }

    /**
     * Get a game available, if it's possible. Otherwise, a new Game will be returned.
     * @param User $me
     * @return array
     */
    public function getAGameAvailable(User $me)
    {
        $gameRepo = $this->em->getRepository(Game::class);
        $games = $gameRepo->findBy(["state" => 0]);

        if (count($games) > 0 && $games[0]->getUserA() != $me) {

            $index_games = array_keys($games);
            do {
                $rd = array_rand($index_games);
                unset($index_games[$rd]);
            } while ($games[$rd]->getUserA() == $me);
            $game = $games[$rd];
            $game->setUserB($me);
            $game->setState(1);
            $this->em->persist($game);
            $this->em->flush();

            $rounds = $this->roundManager->generateRounds($game);
            return ["game" => $game, "rounds" => $rounds];
        }

        return ["game" => $this->createGame($me), "rounds" => []];
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

    public function addUserInGame(User $user, Game $game)
    {

        $game->setUserB($user);
        $game->setState(1);
        $this->em->persist($game);
        $this->em->flush();
    }

}