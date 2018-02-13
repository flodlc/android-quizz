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
use Symfony\Component\HttpKernel\Exception\HttpException;

class UserManager
{
    /**
     * @var EntityManagerInterface
     */
    private $em;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->em = $entityManager;
    }

    /**
     * Get the User with username = $username
     * @param string $username
     * @return User
     */
    public function getByUsername($username)
    {
        $userRepo = $this->em->getRepository(User::class);
        $user = $userRepo->findBy(["username" => $username]);
        if (count($user) > 0)
            throw new HttpException("Utilisateur déjà existant", 500);
        return $this->newUser($username);
    }

    /**
     * Get the User with id = $userId
     * @param integer $userId
     * @return User
     */
    public function getById($userId)
    {
        $userRepo = $this->em->getRepository(User::class);
        $user = $userRepo->find($userId);
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
}