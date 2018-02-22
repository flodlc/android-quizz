<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 21/02/2018
 * Time: 16:37
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Invitation;
use QuizzBundle\Entity\User;
use SensioLabs\Security\Exception\HttpException;

class InvitationManager
{
    private $em;
    private $gameManager;
    private $userManager;

    public function __construct(EntityManagerInterface $entityManager, GameManager $gameManager, UserManager $userManager)
    {
        $this->em = $entityManager;
        $this->gameManager = $gameManager;
        $this->userManager = $userManager;
    }

    public function getInvitationsOfUser(User $user)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);
        $invitsFrom = $invitRepo->findBy(["userFrom" => $user]);
        foreach ($invitsFrom as $invit) {
            $invit->setAdv($invit->getUserTo());
        }
        $invitsTo = $invitRepo->findBy(["userTo" => $user]);
        foreach ($invitsTo as $invit) {
            $invit->setAdv($invit->getUserFrom());
        }
        $invitations = array_merge($invitsFrom, $invitsTo);
        return $invitations;
    }

    /**
     * Send an invitation from user A ($idFrom) to user B ($usernameTo).
     * @param User $me
     * @param $usernameTo
     * @return Invitation
     */
    public function sendInvitation(User $me, $usernameTo)
    {
        $userRepo = $this->em->getRepository(User::class);
        $invitRepo = $this->em->getRepository(Invitation::class);

        $userTo = $userRepo->findBy(["username" => $usernameTo]);
        if (count($userTo) < 1)
            throw new HttpException("This user (" . $usernameTo . ") is unknown.", 404);
        /**
         * @var User $userTo
         */
        $userTo = $userTo[0];

        if ($me === $userTo)
            throw new HttpException("You can't play against yourself.", 500);

        $invitation_old = $invitRepo->findBy(["userFrom" => $me, "userTo" => $userTo]);
        if (count($invitation_old) > 0) {
            foreach ($invitation_old as $inv) {
                /**
                 * @var Invitation $inv
                 */
                if (!$inv->isPlayed())
                    throw new HttpException("Invitation already send.", 404);
            }
        }

        $invitation = new Invitation();
        $invitation->setUserFrom($me);
        $invitation->setUserTo($userTo);
        $this->em->persist($invitation);
        $this->em->flush();
        return $invitation;
    }

    /**
     * Refuse an invitation from another player
     * @param $idInvit
     */
    public function refuseInvitation($idInvit)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);
        /**
         * @var Invitation $invitation
         */
        $invitation = $invitRepo->find($idInvit);
        if ($invitation->isPlayed())
            throw new HttpException("A game has been already created for this invitation.", 404);
        $this->em->remove($invitation);
        $this->em->flush();
    }

    /**
     * Accept an invitation. Return a Game with rounds. The invitation will be removed at this moment.
     * @param User $me
     * @param $idInvit
     * @return array
     */
    public function acceptInvitation(User $me, $idInvit)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);

        /**
         * @var Invitation $invitation
         */
        $invitation = $invitRepo->find($idInvit);
        if (!$invitation)
            throw new HttpException("Pas d'invitation", 500);

        $userA = $invitation->getUserFrom();
        $userB = $invitation->getUserTo();

        $gameRounds = $this->gameManager->createOnlineGame($userA, $userB);

        $role = $this->userManager->whoIAm($me, $gameRounds["game"]);
        switch ($role) {
            case "A":
                $gameRounds["game"]->setAdv($gameRounds["game"]->getUserB());
                break;
            case "B":
                $gameRounds["game"]->setAdv($gameRounds["game"]->getUserA());
                break;
        }
        $invitation->setPlayed(true);
        $this->em->merge($invitation);
        $this->em->flush();
        return $gameRounds;
    }

    public function getStateInvitation(User $me, $idInvit)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);
        $gameRepo = $this->em->getRepository(Game::class);
        /**
         * @var Invitation $invitation
         */
        $invitation = $invitRepo->find($idInvit);
        if (!$invitation)
            throw new HttpException("Invitation remove", 500);
        if (!$invitation->isPlayed())
            return null;
        $game = $gameRepo->findBy(["invitation" => $invitation]);
        $game = $game[0];
        $gameRounds = $this->gameManager->getGameAndRounds($game, $me);
        return $gameRounds;
    }

}