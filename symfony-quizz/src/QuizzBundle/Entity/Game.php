<?php

namespace QuizzBundle\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Game
 *
 * @ORM\Table(name="game")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\GameRepository")
 */
class Game
{
    /**
     * @var int
     * @Groups({"game", "mygames"})
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var int
     * @Groups({"game", "mygames"})
     *
     * @ORM\Column(name="state", type="integer")
     */
    private $state;

    /**
     *
     * @Groups({"game"})
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     */
    private $userA;

    /**
     * @Groups({"game"})
     *
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     */
    private $userB;

    /**
     * @var User
     * @Groups({"game", "mygames"})
     *
     *
     */
    private $adv;

    /**
     * @var User
     * @Groups({"game", "mygames"})
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     *
     *
     */
    private $winner;

    /**
     * @var integer
     * @Groups({"game", "mygames"})
     * @ORM\Column(name="pointsA", type="integer", nullable=true)
     *
     *
     */
    private $pointsA;

    /**
     * @var integer
     * @Groups({"game", "mygames"})
     * @ORM\Column(name="pointsB", type="integer", nullable=true)
     *
     *
     */
    private $pointsB;


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
     * Set state
     *
     * @param integer $state
     *
     * @return Game
     */
    public function setState($state)
    {
        $this->state = $state;

        return $this;
    }

    /**
     * Get state
     *
     * @return int
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * @return User
     */
    public function getUserA()
    {
        return $this->userA;
    }

    /**
     * @param User $userA
     */
    public function setUserA(User $userA)
    {
        $this->userA = $userA;
    }

    /**
     * @return User
     */
    public function getUserB()
    {
        return $this->userB;
    }

    /**
     * @param User $userB
     */
    public function setUserB(User $userB)
    {
        $this->userB = $userB;
    }

    /**
     * @return User
     */
    public function getAdv()
    {
        return $this->adv;
    }

    /**
     * @param User $adv
     */
    public function setAdv(User $adv)
    {
        $this->adv = $adv;
    }

    /**
     * @return User
     */
    public function getWinner()
    {
        return $this->winner;
    }

    /**
     * @param User $winner
     */
    public function setWinner(User $winner)
    {
        $this->winner = $winner;
    }

    /**
     * @return int
     */
    public function getPointsA()
    {
        return $this->pointsA;
    }

    /**
     * @param int $pointsA
     */
    public function setPointsA($pointsA)
    {
        $this->pointsA = $pointsA;
    }

    /**
     * @return int
     */
    public function getPointsB()
    {
        return $this->pointsB;
    }

    /**
     * @param int $pointsB
     */
    public function setPointsB($pointsB)
    {
        $this->pointsB = $pointsB;
    }
}

