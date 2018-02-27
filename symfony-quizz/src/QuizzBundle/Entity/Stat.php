<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Game
 *
 * @ORM\Table(name="user_stat")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\UserStatRepository")
 */
class Stat
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var int
     *
     * @ORM\Column(name="offline_sum_score", type="integer", nullable=true)
     */
    private $offlineSumScore;

    /**
     * @var int
     *
     * @ORM\Column(name="nb_offline_games", type="integer", nullable=true)
     */
    private $nbOfflineGames;

    /**
     * @var int
     *
     * @ORM\Column(name="offline_time_sum", type="integer", nullable=true)
     */
    private $offlineTime;

    /**
     * @var \DateTime
     * @ORM\Column(name="update_date", type="datetime", nullable=true)
     */
    private $updateDate;

    /**
     * Stat constructor.
     */
    public function __construct()
    {
        $this->updateDate = new \DateTime();
        $this->nbOfflineGames = 0;
        $this->offlineSumScore= 0;
        $this->offlineTime= 0;
    }


    /**
     * Get id
     *
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return int
     */
    public function getOfflineSumScore()
    {
        return $this->offlineSumScore;
    }

    /**
     * @param int $offlineSumScore
     */
    public function setOfflineSumScore($offlineSumScore)
    {
        $this->offlineSumScore = $offlineSumScore;
    }

    /**
     * @return int
     */
    public function getNbOfflineGames()
    {
        return $this->nbOfflineGames;
    }

    /**
     * @param int $nbOfflineGames
     */
    public function setNbOfflineGames($nbOfflineGames)
    {
        $this->nbOfflineGames = $nbOfflineGames;
    }

    /**
     * @return \DateTime
     */
    public function getUpdateDate()
    {
        return $this->updateDate;
    }

    /**
     * @param \DateTime $updateDate
     */
    public function setUpdateDate($updateDate)
    {
        $this->updateDate = $updateDate;
    }

    /**
     * @return int
     */
    public function getOfflineTime()
    {
        return $this->offlineTime;
    }

    /**
     * @param int $offlineTime
     */
    public function setOfflineTime($offlineTime)
    {
        $this->offlineTime = $offlineTime;
    }
}

