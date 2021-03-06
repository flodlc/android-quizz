<?php

namespace QuizzBundle\Repository;

/**
 * UserRepository
 *
 * This class was generated by the Doctrine ORM. Add your own custom
 * repository methods below.
 */
class UserStatRepository extends \Doctrine\ORM\EntityRepository
{
    public function getGlobalOfflineSumScore()
    {
        $qb = $this->getEntityManager()->createQueryBuilder();
        $qb->select('SUM(stat.offlineSumScore)')
            ->from('QuizzBundle\Entity\Stat', 'stat');
        return $qb->getQuery()->getSingleScalarResult();
    }


    public function getGlobalNbOfGames()
    {
        $qb = $this->getEntityManager()->createQueryBuilder();
        $qb->select('SUM(stat.nbOfflineGames)')
            ->from('QuizzBundle\Entity\Stat', 'stat');
        return $qb->getQuery()->getSingleScalarResult();
    }
}
