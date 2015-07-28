package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loster on 26.05.2015.
 */
public class Game {

    private Pusher pusher;
    private Pusher active_pusher;//for all playing users in current round

    private CardStack card_stack;
    private Card[] board;

    private UserList players;
    private UserList active_players; //all players in current round
    private User small_blind;
    private User big_blind;

    private User current;
    private User previous;
    private User first;

    private List<String> lastActions;
    private List<Integer> pod;
    private int blind_index = 0;
    private final int SMALL_BLIND_VALUE = 5;
    private final int BIG_BLIND_VALUE = 10;

    private boolean last_turn = false;
    private boolean is_running = false;

    /**
     * creates a new instance of Game initialized with the given Players
     *
     * @param players a UserList of the Players who are playing in the Game
     */
    public Game(UserList players) {
        this.players = players;
        pusher = new Pusher(this.players);
//        startRound();//Falls Game mal später gestartet wird!
    }

    /**
     * lets a User join the game. He is added to the list of playing Users in this Game
     *
     * @param player the joining User
     */
    public void joinGame(User player) {
        JSONObject body = new JSONObject();
        body.put("body", players.getInterfaceUserList());
        pusher.pushToAll("user_joined_notification", body);
        pusher.addUser(player);
//        startRound();
    }

    /**
     * removes the given User from the list of playing Users
     *
     * @param player the User to be removed
     */
    public void leaveGame(User player) {
        //TODO WENN schon in active_players dann da auch raus
        pusher.removeUser(player);
        JSONObject body = new JSONObject();
        body.put("body", players.getInterfaceUserList());
        pusher.pushToAll("user_left_notification", body);
    }

    /**
     * lets the Game start. Only possible if there are two or more Players
     */
    public void startRound() {
        //TODO sollte das vielleicht boolean sein um zu wissen ob es los geht oder nicht?
        //TODO das ganze bei den anderen Methoden auch  (doRaise...)
        if (players.length >= 2 && !is_running) {
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
            lastActions.add(Actions.RAISE);
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
            lastActions.add(Actions.BET);
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
            lastActions.add(Actions.CALL);
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
            if (lastActionEquals(Actions.CHECK) || player == first) {
                lastActions.add(Actions.CHECK);
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
            lastActions.add(Actions.FOLD);
            active_players.removeUser(player);
            if (active_players.length == 1) {
                possible_winner = active_players.getUserByIndex(0);
                noPlayersLeft(possible_winner);
            }
            setNextUser(player);
        }
    }

    /**
     * TODO hier einfügen
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
        return player == current;
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
        previous = current_player;
        int index = (active_players.getUsers().indexOf(current_player) + 1) % active_players.length;
        User person = active_players.getUserByIndex(index);
//        if (person.hasFolded()) {
//            setNextUser(person);
//        }
        current = person;
        if (current == first) {
            if (last_turn) {
                is_running = false;
                //Karten verwerten + Sieger ermitteln
            } else {
                dealBoardCards();
                first = active_players.getUserByIndex(active_players.getUsers().indexOf(big_blind) + 1);
            }
        }
    }

    /**
     * sets the small and the big blind of this round
     */
    private void setBlindPlayers() {
        small_blind = active_players.getUserByIndex(blind_index);
        if (small_blind.payMoney(SMALL_BLIND_VALUE)) {
            pod.add(SMALL_BLIND_VALUE);
        }
        big_blind = active_players.getUserByIndex(raiseBlindIndex());
        if (big_blind.payMoney(BIG_BLIND_VALUE)) {
            pod.add(BIG_BLIND_VALUE);
        }
    }

    /**
     * initializes a new Round. creates an empty pod, a new CardStack and gives every player two cards.
     * also sets small and big blind.
     * sets the player who does his first action
     */
    private void initRound() {
        active_players = new UserList(players.getUsers());
        active_pusher = new Pusher(active_players);
        pod = new ArrayList<>();
        lastActions = new ArrayList<>();
        pod.add(0);
        board = new Card[5];
        card_stack = new CardStack();
        dealCards();
        setBlindPlayers();
        setNextUser(big_blind);
        first = current;
        previous = active_players.getPreviousUser(first);
        is_running = true;
        for (User user : active_players.getUsers()) {
            JSONArray arr = new JSONArray();
            for (Card card : user.getHandCards()) {
                arr.put(card.getInterfaceHash());
            }
            JSONObject body = new JSONObject();
            body.put("cards", arr);
            body.put("your_turn", isCurrent(user));
            body.put("pod", getPodValue());
            body.put("your_money", user.getLimit());
            body.put("available_methods", getAvailableMethods(user));
            active_pusher.pushToSingle("round_starts_notification", body, user.getWebsession());
        }
    }

    //TODO möglicher Weise fehlerhaft!!!
    private JSONObject getAvailableMethods(User user) {
        JSONObject _available_methods = new JSONObject();
        if (isCurrent(user)) {
            _available_methods.put(Actions.CHECK, lastActionEquals(Actions.CHECK) || user == first);
            _available_methods.put(Actions.FOLD, true);
            _available_methods.put(Actions.BET, first == user || lastActionEquals(Actions.CHECK) || lastActions.isEmpty());
            _available_methods.put(Actions.CALL, lastActionEquals(Actions.BET) || lastActionEquals(Actions.RAISE));
            _available_methods.put(Actions.RAISE, first != user || lastActionEquals(Actions.BET) || lastActionEquals(Actions.CHECK));
        }
        return _available_methods;
    }

    /**
     * gives every player two cards from the CardStack
     */
    private void dealCards() {
        for (User player : active_players.getUsers()) {
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
     *
     * @return returns the value of the pod (Integer)
     */
    private int getPodValue() {
        int sum = pod.stream().mapToInt(Integer::intValue).sum();
        return sum;
    }

    private int raiseBlindIndex() {
//        int _index = blind_index;
        blind_index = ++blind_index % active_players.length;
        return blind_index;
    }

    private boolean lastActionEquals(String action) {
        if(!lastActions.isEmpty()){
            return lastActions.get(lastActions.size() - 1).equals(action);
        }else{
            return false;
        }
    }
}
