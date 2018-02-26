<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 21/02/2018
 * Time: 16:37
 */

namespace QuizzBundle\Service;


use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\Query\ResultSetMappingBuilder;
use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\Invitation;
use QuizzBundle\Entity\Response;
use QuizzBundle\Entity\User;
use QuizzBundle\Repository\InvitationRepository;
use Symfony\Component\HttpKernel\Exception\HttpException;

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

        $sqlQuery = "SELECT * 
                    FROM `invitation` i
                    WHERE (i.user_from_id = " . $user->getId() . " and i.played = false) 
                    OR (i.user_to_id = " . $user->getId() . " and i.played = false) 
                    ORDER BY id DESC LIMIT 30;";
        $rsm = new ResultSetMappingBuilder($this->em);
        $rsm->addRootEntityFromClassMetadata(Invitation::class, 'invitation');
        $q = $this->em->createNativeQuery($sqlQuery, $rsm);
        $invitations = $q->getResult();
        foreach ($invitations as $ind => $invitation) {
            /**
             * @var Invitation $invitation
             */
            if ($user === $invitation->getUserTo()) {
                $invitation->setAdv($invitation->getUserFrom());
            } else {
                $invitation->setAdv($invitation->getUserTo());
            }
        }
        return $invitations;
    }

    public function getNbInvitationsToUser(User $user)
    {
        /**
         * @var InvitationRepository $invitRepo
         */
        $invitRepo = $this->em->getRepository(Invitation::class);

        return $invitRepo->getNbInvitationsToMe($user);
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
            throw new HttpException(404, "This user (" . $usernameTo . ") is unknown.");
        /**
         * @var User $userTo
         */
        $userTo = $userTo[0];

        if ($me === $userTo)
            throw new HttpException(500, "You can't play against yourself.");

        $invitation_old = $invitRepo->findBy(["userFrom" => $me, "userTo" => $userTo]);
        if (count($invitation_old) > 0) {
            foreach ($invitation_old as $inv) {
                /**
                 * @var Invitation $inv
                 */
                if (!$inv->isPlayed())
                    throw new HttpException(409, "Invitation already send.");
            }
        }

        $invitation = new Invitation();
        $invitation->setUserFrom($me);
        $invitation->setUserTo($userTo);
        $invitation->setPlayed(false);
        $this->em->persist($invitation);
        $this->em->flush();
        return $invitation;
    }

    /**
     * Refuse an invitation from another player
     * @param $idInvit
     */
    public function removeInvitation($idInvit)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);
        /**
         * @var Invitation $invitation
         */
        $invitation = $invitRepo->find($idInvit);
        if ($invitation->isPlayed())
            throw new HttpException(404, "A game has been already created for this invitation.");
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
            throw new HttpException(404, "Pas d'invitation");

        $userA = $invitation->getUserFrom();
        $userB = $invitation->getUserTo();

        $gameRounds = $this->gameManager->createOnlineGame($userA, $userB);
        $idGame = $gameRounds["game"]->getId();
        $invitation->setPlayed(true);
        $invitation->setGame($idGame);
        $this->em->merge($invitation);
        $this->em->flush();

        $role = $this->userManager->whoIAm($me, $gameRounds["game"]);

        switch ($role) {
            case "A":
                $gameRounds["game"]->setAdv($gameRounds["game"]->getUserB());
                break;
            case "B":
                $gameRounds["game"]->setAdv($gameRounds["game"]->getUserA());
                break;
        }
        return $gameRounds;
    }

    /**
     * @param User $me
     * @param $idInvit
     * @return array|null
     */
    public function getStateInvitation(User $me, $idInvit)
    {
        $invitRepo = $this->em->getRepository(Invitation::class);
        $gameRepo = $this->em->getRepository(Game::class);
        /**
         * @var Invitation $invitation
         */
        $invitation = $invitRepo->find($idInvit);
        if (!$invitation)
            throw new HttpException(500, "Invitation remove");
        if (!$invitation->isPlayed())
            return null;
        $game = $gameRepo->findBy(["invitation" => $invitation]);
        $game = $game[0];
        $gameRounds = $this->gameManager->getGameAndRounds($game, $me);
        return $gameRounds;
    }

}