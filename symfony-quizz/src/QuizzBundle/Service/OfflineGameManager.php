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


class OfflineGameManager
{
    private $em;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->em = $entityManager;
    }

    public function saveOfflineGame(OfflineGame $offlineGame) {
        $this->em->persist($offlineGame);
        $this->em->flush();
    }
}