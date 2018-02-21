<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 21/02/2018
 * Time: 12:30
 */

namespace QuizzBundle\Service;

use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Stats;
use QuizzBundle\Entity\User;
use SensioLabs\Security\Exception\HttpException;


class StatsManager
{
    private $em;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->em = $entityManager;
    }

    public function getMyStats(User $me)
    {
        $stats = $this->em->getRepository(Stats::class)->findBy(["user" => $me]);
        if (count($stats) == 0) {
            throw new HttpException(500, "This user have currently no stats.");
        }
        return $stats;
    }

}