<?php
/**
 * Created by PhpStorm.
 * User: Florian
 * Date: 25/02/2018
 * Time: 19:21
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\OfflineGame;
use QuizzBundle\Entity\Stat;
use QuizzBundle\Repository\OfflineGameRepository;


class OfflineGameManager
{
    private $em;

    private $statManager;

    public function __construct(EntityManagerInterface $entityManager, StatManager $statManager)
    {
        $this->em = $entityManager;
        $this->statManager = $statManager;
    }

    public function saveOfflineGame(OfflineGame $offlineGame) {
        $userStat = $offlineGame->getUser()->getStat();

        $userStat->setNbOfflineGames($userStat->getNbOfflineGames() + 1);
        $userStat->setOfflineSumScore($userStat->getOfflineSumScore() + $offlineGame->getScore());
        $userStat->setOfflineTime($userStat->getOfflineTime() + $offlineGame->getTime());
        $userStat->setUpdateDate(new \DateTime());

        $this->em->persist($offlineGame);
        $this->em->flush();
    }
}