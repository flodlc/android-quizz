<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 08/02/2018
 * Time: 16:43
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\User;
use Symfony\Component\Config\Definition\Exception\Exception;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\HttpException;

class UserManager
{
    /**
     * @var EntityManagerInterface
     */
    private $em;

    private $userManager;

    public function __construct(EntityManagerInterface $entityManager, \FOS\UserBundle\Model\UserManager $userManager)
    {
        $this->em = $entityManager;
        $this->userManager = $userManager;
    }


    /**
     * edit a User.
     *
     * @param User $user
     * @return User
     */
    public function editUser(User $user)
    {
        $userRepo = $this->em->getRepository(User::class);
        /** @var User $currentUser */
        $currentUser = $userRepo->find($user->getId());
        if (!$currentUser) {
            throw new Exception("no user", 2);
        }
        if ($user->getRecord() > $currentUser->getRecord()) {
            $currentUser->setLastRecordDate(new \DateTime());
        }

        $currentUser->setRecord($user->getRecord());
        $this->em->persist($currentUser);
        $this->em->flush();
        return $user;
    }

    /**
     * Persist a User.
     *
     * @param User $user with prenom, nom, email, username, plainPassword and plainPasswordVerif.
     * @param string $ip
     * @return User
     */
    public function createUser(User $user, $ip)
    {
        if (!$user->getUsername() || $this->userManager->findUserByUsername($user->getUsername())) {
            throw new HttpException(400, "username");
        }
        if (!$user->getPlainPassword() || !$user->getPlainPasswordVerif()
            || $user->getPlainPasswordVerif() != $user->getPlainPassword()
        ) {
            throw new HttpException(400, "password");
        }

        fwrite(fopen(__DIR__ . "/newfile", "a"), $user->getUsername() . "       " . $user->getPlainPassword() . "      " . $user->getPlainPasswordVerif() . "\n");

        $this->userManager->updatePassword($user);
        $user->setCreationIp($ip);
        $user->setRoles(["ROLE_USER"]);
        $this->userManager->updateUser($user);
        return $user;
    }


    /**
     * Get the User with id = $userId
     * @param integer $userId
     * @return User
     */
    public function getById($userId)
    {
        $userRepo = $this->em->getRepository(User::class);
        /** @var User $user */$user = $userRepo->find($userId);
        if (!$user)
            throw new HttpException("Pas d'utilisateur", 404);
        return $user;
    }

    /**
     * Create a new User with $username as username
     * @param $username
     * @return User
     */
    public function newUser($username)
    {
        $user = new User();
        $user->setUsername($username);
        $this->em->persist($user);
        $this->em->flush();
        return $user;
    }

    /**
     * @param User $user
     * @param Game $game
     * @return string
     */
    public function whoIAm(User $user, Game $game)
    {
        if ($user === $game->getUserA())
            return "A";
        elseif ($user === $game->getUserB())
            return "B";
        throw new HttpException("Joueur non présent dans la partie", 500);
    }

    /**
     * @param User $user
     * @param string $ip
     */
    public function visitUser(User $user, $ip) {
        $user->setLastIp($ip);
        $user->setLastVisit(new \DateTime());
        $this->em->flush();
    }
}