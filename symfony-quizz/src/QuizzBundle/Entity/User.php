<?php

namespace QuizzBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;
use FOS\UserBundle\Model\User as BaseUser;


/**
 * @ORM\Entity
 * @ORM\Table(name="user")
 * @ORM\AttributeOverrides({
 *      @ORM\AttributeOverride(name="email", column=@ORM\Column(type="string", name="email", length=255, nullable=true)),
 *      @ORM\AttributeOverride(name="emailCanonical", column=@ORM\Column(type="string", name="email_canonical", length=255, nullable=true))
 * })
 */
class User extends BaseUser
{
    /**
     * @Groups({"user", "stats", "invit"})
     *
     * @ORM\Id
     * @ORM\Column(type="integer")
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * @var String
     *
     * Use to check the if identique with $plainPassword during the registration
     */
    private $plainPasswordVerif;

    /**
     * @Groups({"game", "user", "mygames", "stats", "invit"})
     */
    protected $username;

    /**
     * @Groups({"user", "stats"})
     *
     * @ORM\Column(name="record", type="integer", nullable=true)
     */
    private $record;

    /**
     *
     * @ORM\Column(name="last_record_date", type="datetime", nullable=true)
     */
    private $lastRecordDate;


    /**
     *
     * @ORM\Column(name="last_visit", type="datetime", nullable=true)
     */
    private $lastVisit;

    /**
     *
     * @ORM\Column(name="create_ip", type="string", nullable=true)
     */
    private $creationIp;


    /**
     *
     * @ORM\Column(name="last_ip", type="string", nullable=true)
     */
    private $lastIp;

    /**
     * @var \DateTime
     * @ORM\Column(name="creation_date", type="datetime", nullable=true)
     */
    private $creationDate;



    public function __construct()
    {
        parent::__construct();
        $this->setRecord(0);
        $this->setEmail("");
        $this->setEmailCanonical("");
        $this->setEnabled(true);
        $this->setLastRecordDate(new \DateTime());
        $this->creationDate = new \Datetime();
    }

    /**
     * @return String
     */
    public function getPlainPasswordVerif()
    {
        return $this->plainPasswordVerif;
    }

    /**
     * @param String $plainPasswordVerif
     */
    public function setPlainPasswordVerif($plainPasswordVerif)
    {
        $this->plainPasswordVerif = $plainPasswordVerif;
    }

    /**
     * @return mixed
     */
    public function getRecord()
    {
        return $this->record;
    }

    /**
     * @param mixed $record
     */
    public function setRecord($record)
    {
        $this->record = $record;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @return
     */
    public function getLastRecordDate()
    {
        return $this->lastRecordDate;
    }

    /**
     * @param $lastRecordDate
     */
    public function setLastRecordDate($lastRecordDate)
    {
        $this->lastRecordDate = $lastRecordDate;
    }

    /**
     * @return mixed
     */
    public function getLastVisit()
    {
        return $this->lastVisit;
    }

    /**
     * @param mixed $lastVisit
     */
    public function setLastVisit($lastVisit)
    {
        $this->lastVisit = $lastVisit;
    }

    /**
     * @return mixed
     */
    public function getCreationIp()
    {
        return $this->creationIp;
    }

    /**
     * @param mixed $creationIp
     */
    public function setCreationIp($creationIp)
    {
        $this->creationIp = $creationIp;
    }

    /**
     * @return mixed
     */
    public function getLastIp()
    {
        return $this->lastIp;
    }

    /**
     * @param mixed $lastIp
     */
    public function setLastIp($lastIp)
    {
        $this->lastIp = $lastIp;
    }

    /**
     * @return \DateTime
     */
    public function getCreationDate()
    {
        return $this->creationDate;
    }

    /**
     * @param \DateTime $creationDate
     */
    public function setCreationDate($creationDate)
    {
        $this->creationDate = $creationDate;
    }


}