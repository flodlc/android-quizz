<?php
/**
 * Created by PhpStorm.
 * User: lucas
 * Date: 21/02/2018
 * Time: 16:34
 */

namespace QuizzBundle\Controller;

use QuizzBundle\Service\InvitationManager;
use SensioLabs\Security\Exception\HttpException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Doctrine\Common\Annotations\AnnotationReader;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Mapping\Loader\AnnotationLoader;
use Symfony\Component\Serializer\Normalizer\DateTimeNormalizer;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Mapping\Factory\ClassMetadataFactory;

class InvitationController extends Controller
{
    /**
     * @var Serializer
     */
    private $serializer;

    /**
     * ActeurController constructor.
     */
    public function __construct()
    {
        $classMetadataFactory = new ClassMetadataFactory(new AnnotationLoader(new AnnotationReader()));
        $this->serializer = new Serializer([new DateTimeNormalizer("d/m/Y"), new ObjectNormalizer($classMetadataFactory)], [new JsonEncoder()]);
    }

    /**
     * Get the list of invitations
     * @Route("/all", name="invitation_get")
     * @Method({"GET"})
     */
    public function getInvitationsAction()
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");


        $invitation = $invitationManager->getInvitationsOfUser($this->getUser());

        return new Response($this->serializer->serialize($invitation, "json", ["groups" => ["invit"]]));
    }

    /**
     * Get the number of invitation received by you
     * @Route("/me", name="invitation_get_to_me")
     * @Method({"GET"})
     */
    public function getNumberInvitationsToMeAction()
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");


        $nb_invitations = $invitationManager->getNbInvitationsToUser($this->getUser());

        return new Response(json_encode($nb_invitations));
    }

    /**
     * Send a invitation request to a user, in order to play against him
     * @Route("/", name="invitation_request")
     * @Method({"POST"})
     */
    public function postRequestAction(Request $request)
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");

        $data = json_decode($request->getContent(), true);
        $invitation = $invitationManager->sendInvitation($this->getUser(), $data["to"]);

        return new Response($this->serializer->serialize($invitation, "json", ["groups" => ["invit"]]));
    }

    /**
     * Route for accept an invitation. Return Game and rounds
     * @Route("/accept", name="invitation_accept")
     * @Method({"GET"})
     */
    public function getAcceptAction(Request $request)
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");

        $invitationId = $request->get("invitation");
        $invitation = $invitationManager->acceptInvitation($this->getUser(), $invitationId);

        $gameSeria = $this->serializer->normalize($invitation["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($invitation["rounds"], "json", ["groups" => ["game"]]);
        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));
    }

    /**
     * Route for accept an invitation. Return Game and rounds
     * @Route("/refuse", name="invitation_refuse")
     * @Method({"GET"})
     */
    public function getRefuseAction(Request $request)
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");

        $invitationId = $request->get("invitation");

        $invitationManager->removeInvitation($invitationId);

        return new Response(json_encode(true));
    }

    /**
     * Route to know the status of a game. Return Game and rounds or 0.
     * @Route("/status", name="invitation_status")
     * @Method({"POST"})
     */
    public function getStateAction(Request $request)
    {
        /**
         * @var InvitationManager $invitationManager
         */
        $invitationManager = $this->container->get("quizz.invitation");

        $data = json_decode($request->getContent(), true);
        $gameRounds = $invitationManager->getStateInvitation($this->getUser(), $data["invit"]);
        if ($gameRounds == null)
            return new Response(json_encode(0));

        $gameSeria = $this->serializer->normalize($gameRounds["game"], "json", ["groups" => ["game"]]);
        $roundsSeria = $this->serializer->normalize($gameRounds["rounds"], "json", ["groups" => ["game"]]);
        return new Response(json_encode(["game" => $gameSeria, "rounds" => $roundsSeria]));
    }

}