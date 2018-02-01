<?php

namespace QuizzBundle\Controller;

use QuizzBundle\Entity\Game;
use QuizzBundle\Entity\User;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Doctrine\Common\Annotations\AnnotationReader;
use Doctrine\ORM\EntityNotFoundException;
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

class DefaultController extends Controller
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
     * @Route("/games", name="games")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getGamesAction(Request $request)
    {
        $username = $request->get("username");

        $entityManager = $this->getDoctrine()->getManager();
        $gameRepo = $entityManager->getRepository(Game::class);
        $userRepo = $entityManager->getRepository(User::class);

        $user = $userRepo->findBy(["username" => $username]);
        $gamesA = $gameRepo->findBy(["userA" => $user]);
        $gamesB = $gameRepo->findBy(["userB" => $user]);
        $games_tmp = array_merge($gamesA, $gamesB);
        $games = [];
        foreach ($games_tmp as $game) {
            if ($game->getState() != 2)
                $games[] = $game;
        }

        return new Response($this->serializer->serialize($games, "json", ["groups" => ["game"]]));
    }

    /**
     * @Route("/game", name="search")
     * @Method({"GET"})
     *
     * @return Response
     */
    public function getSearchAction(Request $request)
    {
        $username = $request->get("username");

        $entityManager = $this->getDoctrine()->getManager();
        $userRepo = $entityManager->getRepository(User::class);

        $userA = $userRepo->findOneBy(["username" => $username]);
        $users = $userRepo->findBy(["stateSearch" => 1]);

        if (count($users) > 0 && $users[0]->getUsername() != $username) {
            do {
                $rd = rand(0, sizeof($users) - 1);
            } while ($users[$rd]->getUsername() == $username);

        } else {
            $userA->setStateSearch(1);
            $entityManager->merge($userA);
            $entityManager->flush();
            return new Response("En attente de joueur..", 404);
        }

        $userB = $users[$rd];
        $game = new Game();
        $game->setState(1);
        $game->setUserA($userA);
        $game->setUserB($userB);
        $userA->setStateSearch(0);
        $userB->setStateSearch(0);

        $entityManager->merge($userA);
        $entityManager->merge($userB);
        $entityManager->persist($game);
        $entityManager->flush();
        return new Response($this->serializer->serialize($game, "json", ["groups" => ["game"]]));
    }
}
