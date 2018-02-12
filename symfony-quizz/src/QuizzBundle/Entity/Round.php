<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

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
     * @Groups({"game"})
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var int
     * @Groups({"game"})
     *
     * @ORM\Column(name="numRound", type="integer")
     */
    private $numRound;

    /**
     * @var int
     * @Groups({"game"})
     *
     * @ORM\Column(name="state", type="integer")
     */
    private $state;

    /**
     *
     * @Groups({"game"})
     *
     * @ORM\ManyToOne(targetEntity="Question", cascade={"persist"})
     */
    private $question;

    /**
     * @var Response
     * @Groups({"game"})
     *
     * @ORM\ManyToOne(targetEntity="Response", cascade={"persist"})
     */
    private $responseUA;

    /**
     * @var Response
     * @Groups({"game"})
     *
     * @ORM\ManyToOne(targetEntity="Response", cascade={"persist"})
     *
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
     * @return int
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * @param int $state
     */
    public function setState($state)
    {
        $this->state = $state;
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
     * @return Response
     */
    public function getResponseUA()
    {
        return $this->responseUA;
    }

    /**
     * @param Response $responseUA
     */
    public function setResponseUA(Response $responseUA)
    {
        $this->responseUA = $responseUA;
    }

    /**
     * @return Response
     */
    public function getResponseUB()
    {
        return $this->responseUB;
    }

    /**
     * @param Response $responseUB
     */
    public function setResponseUB(Response $responseUB)
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

