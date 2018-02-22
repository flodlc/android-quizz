<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Invitation
 *
 * @ORM\Table(name="invitation")
 * @ORM\Entity(repositoryClass="QuizzBundle\Repository\InvitationRepository")
 */
class Invitation
{
    /**
     * @var int
     * @Groups({"invit"})
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @Groups({"invit"})
     * @var User $userFrom
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     */
    private $userFrom;

    /**
     * @Groups({"invit"})
     * @var User $userTo
     * @ORM\ManyToOne(targetEntity="User", cascade={"persist"})
     */
    private $userTo;

    /**
     * @Groups({"invit"})
     * @var bool
     *
     * @ORM\Column(name="played", type="boolean", options={"default": false})
     */
    private $played;

    /**
     * @Groups({"invit"})
     * @var User
     *
     */
    private $adv;


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
    public function getUserFrom()
    {
        return $this->userFrom;
    }

    /**
     * @param User $userFrom
     */
    public function setUserFrom(User $userFrom)
    {
        $this->userFrom = $userFrom;
    }

    /**
     * @return User
     */
    public function getUserTo()
    {
        return $this->userTo;
    }

    /**
     * @param User $userTo
     */
    public function setUserTo(User $userTo)
    {
        $this->userTo = $userTo;
    }

    /**
     * @return bool
     */
    public function isPlayed()
    {
        return $this->played;
    }

    /**
     * @param bool $played
     */
    public function setPlayed($played)
    {
        $this->played = $played;
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
}
