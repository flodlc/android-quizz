<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Stats
 *
 * @ORM\Table(name="stats")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\StatsRepository")
 */
class Stats
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
     *
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     *
     */
    private $user;

    /**
     * @var int
     * @Groups({"stats"})
     *
     * @ORM\Column(name="game_tot", type="integer", options={"default":0})
     */
    private $gameTot;

    /**
     * @var int
     * @Groups({"stats"})
     *
     * @ORM\Column(name="game_win", type="integer", options={"default":0})
     */
    private $gameWin;


    /**
     * Get id.
     *
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return User
     */
    public function getUser()
    {
        return $this->user;
    }

    /**
     * @param User $user
     */
    public function setUser(User $user)
    {
        $this->user = $user;
    }

    /**
     * Set gameTot.
     *
     * @param int $gameTot
     *
     * @return Stats
     */
    public function setGameTot($gameTot)
    {
        $this->gameTot = $gameTot;

        return $this;
    }

    /**
     * Get gameTot.
     *
     * @return int
     */
    public function getGameTot()
    {
        return $this->gameTot;
    }

    /**
     * Set gameWin.
     *
     * @param int $gameWin
     *
     * @return Stats
     */
    public function setGameWin($gameWin)
    {
        $this->gameWin = $gameWin;

        return $this;
    }

    /**
     * Get gameWin.
     *
     * @return int
     */
    public function getGameWin()
    {
        return $this->gameWin;
    }
}
