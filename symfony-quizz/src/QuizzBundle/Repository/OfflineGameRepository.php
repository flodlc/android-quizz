<?php
/**
 * Created by PhpStorm.
 * User: Florian
 * Date: 25/02/2018
 * Time: 19:09
 */

namespace QuizzBundle\Repository;


use Doctrine\ORM\EntityRepository;
use QuizzBundle\Entity\User;

class OfflineGameRepository extends EntityRepository
{
    public function getMyOfflineSumScore(User $user)
    {
        $dql = "SELECT SUM(og.score)
                FROM QuizzBundle\Entity\OfflineGame og 
                WHERE og.user = ?1";
        $sumScore = $this->getEntityManager()->createQuery($dql)
            ->setParameter(1, $user)
            ->getSingleScalarResult();
        return $sumScore;
    }

    public function getMyNbOfGames(User $user)
    {
        $dql = "SELECT COUNT(og.id)
                FROM QuizzBundle\Entity\OfflineGame og
                WHERE og.user = ?1";
        $nbGame = $this->getEntityManager()->createQuery($dql)
            ->setParameter(1, $user)
            ->getSingleScalarResult();
        return $nbGame;
    }

    public function getMyOfflineTime(User $user)
    {
        $dql = "SELECT SUM(og.time)
                FROM QuizzBundle\Entity\OfflineGame og
                WHERE og.user = ?1";
        $nbGame = $this->getEntityManager()->createQuery($dql)
            ->setParameter(1, $user)
            ->getSingleScalarResult();
        return $nbGame;
    }

    public function getGlobalOfflineSumScore()
    {
        $dql = "SELECT SUM(og.score) AS balance 
                FROM QuizzBundle\Entity\OfflineGame og";
        $sumScore = $this->getEntityManager()->createQuery($dql)
            ->getSingleScalarResult();
        return $sumScore;
    }

    public function getGlobalNbOfGames()
    {
        $dql = "SELECT COUNT(og.id)
                FROM QuizzBundle\Entity\OfflineGame og";
        $nbGame = $this->getEntityManager()->createQuery($dql)
            ->getSingleScalarResult();
        return $nbGame;
    }
}