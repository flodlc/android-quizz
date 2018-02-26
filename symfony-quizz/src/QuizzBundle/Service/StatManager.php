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
use QuizzBundle\Repository\OfflineGameRepository;

class StatManager
{
    private $em;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->em = $entityManager;
    }

    public function getGoodResponseRate($user)
    {

        /** @var OfflineGameRepository $offlineGameRepo */
        $offlineGameRepo = $this->em->getRepository(OfflineGame::class);

        $sum = $offlineGameRepo->getMyOfflineSumScore($user);
        $nbGame = $offlineGameRepo->getMyNbOfGames($user);
        if ($nbGame == 0) {
            return null;
        }
        return ($sum) / ($sum + $nbGame);
    }

    public function getGlobalGoodResponseRate()
    {
        /** @var OfflineGameRepository $offlineGameRepo */
        $offlineGameRepo = $this->em->getRepository(OfflineGame::class);

        $sum = $offlineGameRepo->getGlobalOfflineSumScore();
        $nbGame = $offlineGameRepo->getGlobalNbOfGames();
        if ($nbGame == 0) {
            return null;
        }
        return ($sum) / ($sum + $nbGame);
    }
}