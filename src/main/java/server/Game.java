package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loster on 26.05.2015.
 */
public class Game {

    private CardStack card_stack;
    private Card[] board;

    private UserList players;
    private User small_blind;
    private User big_blind;

    private User current;
    private User first;

    private List<Integer> pod;
    private int blind_index = 0;

    private boolean last_turn = false;

    /**
     * creates a new instance of Game initialized with the given Players
     *
     * @param players a UserList of the Players who are playing in the Game
     */
    public Game(UserList players) {
        this.players = players;
        startRound();
    }

    /**
     * lets a User join the game. He is added to the list of playing Users in this Game
     *
     * @param player the joining User
     */
    public void joinGame(User player) {
        players.add(player);
    }

    /**
     * removes the given User from the list of playing Users
     *
     * @param player the User to be removed
     */
    public void leaveGame(User player) {
        players.removeUser(player);
    }

    /**
     * lets the Game start. Only possible if there are two or more Players
     */
    public void startRound() {
        //TODO sollte das vielleicht boolean sein um zu wissen ob es los geht oder nicht?
        //TODO das ganze bei den anderen Methoden auch  (doRaise...)
        if (players.length > 2) {
            initRound();
        }
    }

    /**
     * does a raise for the given User.
     * adds the raise to the pod
     * sets the last action that was made to an raise
     * sets the next player who needs to trigger an action
     *
     * @param player the player who is doing the raise
     * @param raise  the amount of the raise
     */
    public void doRaise(User player, int raise) {
        if (isCurrent(player)) {
            pod.add(getLastBet() + raise);
            first = player;
            player.setLastAction(Actions.RAISE);
            setNextUser(player);
        }
    }

    /**
     * does a bet for the given User
     * add the bet to the pod
     * sets the last action that was made to a bet
     * sets the next player who needs to trigger an action
     *
     * @param player the player who is doing the bet
     * @param bet    the amount of the bet
     */
    public void doBet(User player, int bet) {
        if (isCurrent(player)) {
            pod.add(bet);
            player.setLastAction(Actions.BET);
            setNextUser(player);
        }
    }

    /**
     * does a call for the given User
     * add the amount of the last bet to the pod
     * sets the last action that was made to a call
     * sets the next player who needs to trigger an action
     *
     * @param player the player who is doing the call
     */
    public void doCall(User player) {
        if (isCurrent(player)) {
            int bet = getLastBet();
            pod.add(bet);
            player.setLastAction(Actions.CALL);
            setNextUser(player);
        }
    }

    /**
     * does a check for the given User
     * is only possible if the action before was a check or he is the first player
     * sets the last action that was made to a check
     * sets the next player who needs to trigger an action
     *
     * @param player the player who is doing the check
     */
    public void doCheck(User player) {
        if (isCurrent(player)) {
            if (players.getUserByIndex(players.getUsers().indexOf(player) - 1).getLastAction() == Actions.CHECK || player.equals(first)) {
                player.setLastAction(Actions.CHECK);
                setNextUser(player);
            }
        }
    }

    /**
     * does a fold for the given User
     * his last action is set to fold
     *
     * @param player the player who is doing the fold
     */
    public void doFold(User player) {
        if (isCurrent(player)) {
            int i = 0;
            User possible_winner = null;
            player.setHasFolded(true);
            player.setLastAction(Actions.FOLD);
            for (User user : players.getUsers()) {
                if (!user.hasFolded()) {
                    i++;
                    possible_winner = user;
                }
            }
            if (i == 1) {
                noPlayersLeft(possible_winner);
            }
        }
    }

    /**
     * TODO hjier einf+ügen
     *
     * @param possible_winner
     */
    private void noPlayersLeft(User possible_winner) {
        possible_winner.setLimit(possible_winner.getLimit() + getPodValue());
        startRound();
    }

    /**
     * checks if the given User is the current User so he can perform an Action
     *
     * @param player the player to check if he is the current player
     * @return returns TRUE if he is the current player, FALSE otherwise
     */
    private boolean isCurrent(User player) {
        return player.equals(current);
    }

    /**
     * returns the last bet that was made
     *
     * @return returns the value of the last bet (Integer)
     */
    private int getLastBet() {
        return pod.get(pod.size() - 1);
    }

    /**
     * sets the next user who needs to perform an Action.
     * if the next player has folded, the user after him will get next user
     * checks if the next player is the "first" of a round, so the round ends
     * if it is the last turn (full table) the game ends
     *
     * @param current_player the current player who did the last action
     */
    private void setNextUser(User current_player) {
        User person = players.getUserByIndex(players.getUsers().indexOf(current_player) + 1);
        if (person.hasFolded()) {
            setNextUser(person);
        }
        current = person;
        if (current.equals(first)) {
            if (last_turn) {
                //Karten verwerten + Sieger ermitteln
            } else {
                dealBoardCards();
                first = players.getUserByIndex(players.getUsers().indexOf(big_blind) + 1);
            }
        }
    }

    /**
     * sets the small and the big blind of this round
     */
    private void setBlindPlayers() {
        small_blind = players.getUserByIndex(blind_index);
        big_blind = players.getUserByIndex(++blind_index);
    }

    /**
     * initializes a new Round. creates an empty pod, a new CardStack and gives every player two cards.
     * also sets small and big blind.
     * sets the player who does his first action
     */
    private void initRound() {
        pod = new ArrayList<>();
        pod.add(0);
        board = new Card[5];
        card_stack = new CardStack();
        dealCards();
        setBlindPlayers();
        current = players.getUserByIndex(blind_index + 1);
        first = current;
    }

    /**
     * gives every player two cards from the CardStack
     */
    private void dealCards() {
        for (User player : players.getUsers()) {
            for (int i = 0; i < 2; i++) {
                player.setHandCards(card_stack.pop());
            }
        }
    }

    /**
     * draws cards every round to the board (table)
     * burns a card before every draw
     * if zero cards on the table draw 3
     * if there are three cards on the table draw 1
     * if there are four cards on the table draw 1 and set last round
     */
    private void dealBoardCards() {
        card_stack.pop();//burn card;
        if (board[0].equals(null)) {
            board[0] = card_stack.pop();
            board[1] = card_stack.pop();
            board[2] = card_stack.pop();
        } else if (board[3].equals(null)) {
            board[3] = card_stack.pop();
        } else if (board[4].equals(null)) {
            last_turn = true;
            board[4] = card_stack.pop();
        }
    }

    /**
     * returns the value of the pod
     * @return returns the value of the pod (Integer)
     */
    private int getPodValue() {
        int sum = pod.stream().mapToInt(Integer::intValue).sum();
        return sum;
    }
}
