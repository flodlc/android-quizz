<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Round
 *
 * @ORM\Table(name="round")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\RoundRepository")
 */
class Round
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
     * @ORM\Column(name="numRound", type="integer")
     */
    private $numRound;

    /**
     * @var bool
     *
     * @ORM\Column(name="state", type="boolean")
     */
    private $state;

    /**
     *
     *
     * @ORM\ManyToOne(targetEntity="Question", cascade={"persist"})
     */
    private $question;

    /**
     *
     * @ORM\ManyToOne(targetEntity="Response", cascade={"persist"})
     */
    private $responseUA;

    /**
     *
     * @ORM\ManyToOne(targetEntity="Response", cascade={"persist"})
     */
    private $responseUB;

    /**
     *
     * @ORM\ManyToOne(targetEntity="Game", cascade={"persist"})
     */
    private $game;


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
     * Set numRound
     *
     * @param integer $numRound
     *
     * @return Round
     */
    public function setNumRound($numRound)
    {
        $this->numRound = $numRound;

        return $this;
    }

    /**
     * Get numRound
     *
     * @return int
     */
    public function getNumRound()
    {
        return $this->numRound;
    }

    /**
     * Set state
     *
     * @param boolean $state
     *
     * @return Round
     */
    public function setState($state)
    {
        $this->state = $state;

        return $this;
    }

    /**
     * Get state
     *
     * @return bool
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * @return mixed
     */
    public function getQuestion()
    {
        return $this->question;
    }

    /**
     * @param mixed $question
     */
    public function setQuestion($question)
    {
        $this->question = $question;
    }

    /**
     * @return mixed
     */
    public function getResponseUA()
    {
        return $this->responseUA;
    }

    /**
     * @param mixed $responseUA
     */
    public function setResponseUA($responseUA)
    {
        $this->responseUA = $responseUA;
    }

    /**
     * @return mixed
     */
    public function getResponseUB()
    {
        return $this->responseUB;
    }

    /**
     * @param mixed $responseUB
     */
    public function setResponseUB($responseUB)
    {
        $this->responseUB = $responseUB;
    }

    /**
     * @return mixed
     */
    public function getGame()
    {
        return $this->game;
    }

    /**
     * @param mixed $game
     */
    public function setGame($game)
    {
        $this->game = $game;
    }
}

