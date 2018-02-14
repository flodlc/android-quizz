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
WHERE g.user_a_id = " . $user->getId() . " OR g.user_b_id = " . $user->getId() . " 
ORDER BY id DESC;";
        $rsm = new ResultSetMappingBuilder($this->em);
        $rsm->addRootEntityFromClassMetadata(Game::class, 'game');
        $q = $this->em->createNativeQuery($sqlQuery, $rsm);
        $games = $q->getResult();
        foreach ($games as $game) {
            if ($user == $game->getUserA())
                $game->setAdv($game->getUserB());
            else
                $game->setAdv($game->getUserA());
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

        $games = $gameRepo->findBy(["userA" => $me, "state" => 0]);
        /**
         * He's already looking for an adversaire
         */
        if (count($games) > 0)
            return ["game" => $games[0], "rounds" => []];

        $gamesA = $gameRepo->findBy(["userA" => $me, "state" => 1, "pointsA" => null]);
        $gamesB = $gameRepo->findBy(["userB" => $me, "state" => 1, "pointsB" => null]);
        /**
         * He has already a party which he has no replied
         */
        if (count($gamesA) > 0) {
            $gamesA[0]->setAdv($gamesA[0]->getUserB());
            return ["game" => $gamesA[0], "rounds" => $this->roundManager->getRoundsOfGame($gamesA[0])];
        }
        if (count($gamesB) > 0) {
            $gamesB[0]->setAdv($gamesB[0]->getUserA());
            return ["game" => $gamesB[0], "rounds" => $this->roundManager->getRoundsOfGame($gamesB[0])];
        }


        $games = $gameRepo->findBy(["state" => 0]);
        /**
         * There is a party which is waiting for someone
         */
        if (count($games) > 0) {

            $game = $games[array_rand($games)];

            $this->startOnlineGame($me, $game);
            $rounds = $this->roundManager->generateRounds($game);
            $game->setAdv($game->getUserA());
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