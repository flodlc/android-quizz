<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Question
 *
 * @ORM\Table(name="question")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\QuestionRepository")
 */
class Question
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
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="question", type="string", length=255)
     */
    private $question;

    /**
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="responseA", type="string", length=255)
     */
    private $responseA;

    /**
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="responseB", type="string", length=255)
     */
    private $responseB;

    /**
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="responseC", type="string", length=255)
     */
    private $responseC;

    /**
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="responseD", type="string", length=255)
     */
    private $responseD;

    /**
     * @var string
     * @Groups({"game"})
     *
     * @ORM\Column(name="answer", type="string", length=255)
     */
    private $answer;


    /**
     * Get id
     *
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    public function setId($id)
    {
        $this->id = $id;

        return $this;
    }

    /**
     * Set question
     *
     * @param string $question
     *
     * @return Question
     */
    public function setQuestion($question)
    {
        $this->question = $question;

        return $this;
    }

    /**
     * Get question
     *
     * @return string
     */
    public function getQuestion()
    {
        return $this->question;
    }

    /**
     * Set responseA
     *
     * @param string $responseA
     *
     * @return Question
     */
    public function setResponseA($responseA)
    {
        $this->responseA = $responseA;

        return $this;
    }

    /**
     * Get responseA
     *
     * @return string
     */
    public function getResponseA()
    {
        return $this->responseA;
    }

    /**
     * Set responseB
     *
     * @param string $responseB
     *
     * @return Question
     */
    public function setResponseB($responseB)
    {
        $this->responseB = $responseB;

        return $this;
    }

    /**
     * Get responseB
     *
     * @return string
     */
    public function getResponseB()
    {
        return $this->responseB;
    }

    /**
     * Set responseC
     *
     * @param string $responseC
     *
     * @return Question
     */
    public function setResponseC($responseC)
    {
        $this->responseC = $responseC;

        return $this;
    }

    /**
     * Get responseC
     *
     * @return string
     */
    public function getResponseC()
    {
        return $this->responseC;
    }

    /**
     * Set responseD
     *
     * @param string $responseD
     *
     * @return Question
     */
    public function setResponseD($responseD)
    {
        $this->responseD = $responseD;

        return $this;
    }

    /**
     * Get responseD
     *
     * @return string
     */
    public function getResponseD()
    {
        return $this->responseD;
    }

    /**
     * Set answer
     *
     * @param string $answer
     *
     * @return Question
     */
    public function setAnswer($answer)
    {
        $this->answer = $answer;

        return $this;
    }

    /**
     * Get answer
     *
     * @return string
     */
    public function getAnswer()
    {
        return $this->answer;
    }
}

