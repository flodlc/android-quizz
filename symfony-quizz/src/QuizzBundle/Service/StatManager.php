<?php
/**
 * Created by PhpStorm.
 * User: Florian
 * Date: 26/02/2018
 * Time: 01:01
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\OfflineGame;
use QuizzBundle\Entity\Stat;
use QuizzBundle\Entity\User;
use QuizzBundle\Repository\OfflineGameRepository;
use QuizzBundle\Repository\UserStatRepository;

class StatManager
{
    private $em;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->em = $entityManager;
    }

    public function getGoodResponseRate(User $user)
    {
        $sum = $user->getStat()->getOfflineSumScore();
        $nbGame = $user->getStat()->getNbOfflineGames();
        if ($nbGame == 0) {
            return null;
        }
        return ($sum) / ($sum + $nbGame);
    }

    public function getGlobalGoodResponseRate()
    {
        /** @var UserStatRepository $statRepo */
        $statRepo = $this->em->getRepository(Stat::class);

        $sum = $statRepo->getGlobalOfflineSumScore();
        $nbGame = $statRepo->getGlobalNbOfGames();
        if ($nbGame == 0) {
            return null;
        }
        return ($sum) / ($sum + $nbGame);
    }

    /**
     * @param User $user
     * @return Stat
     */
    public function makeStatFromOfflineTable(User $user)
    {
        $userStat = new Stat();

        /**
         * @var OfflineGameRepository $offlineGameRepo
         */
        $offlineGameRepo = $this->em->getRepository(OfflineGame::class);

        $userStat->setOfflineSumScore($offlineGameRepo->getMyOfflineSumScore($user));
        $userStat->setNbOfflineGames($offlineGameRepo->getMyNbOfGames($user));
        $userStat->setOfflineTime($offlineGameRepo->getMyOfflineTime($user));

        return $userStat;
    }
}