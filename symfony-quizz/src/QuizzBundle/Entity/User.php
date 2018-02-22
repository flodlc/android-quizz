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
     * @Groups({"user"})
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
     * @Groups({"game", "user", "mygames"})
     */
    protected $username;

    /**
     * @Groups({"user"})
     *
     * @ORM\Column(name="record", type="integer", nullable=true)
     */
    private $record;

    /**
     *
     * @ORM\Column(name="last_record_date", type="datetime", nullable=true)
     */
    private $lastRecordDate;


    public function __construct()
    {
        parent::__construct();
        $this->setRecord(0);
        $this->setEmail("");
        $this->setEmailCanonical("");
        $this->setEnabled(true);
        $this->setLastRecordDate(new \DateTime());
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
}