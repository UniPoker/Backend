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
    private List<Integer> pod = new ArrayList<>();
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
        pusher.addUser(player);
        pushGameDataToUsers("user_joined_notification");
    }

    /**
     * removes the given User from the list of playing Users
     *
     * @param player the User to be removed
     */
    public void leaveGame(User player) {
        pusher.removeUser(player);
        pushGameDataToUsers("user_left_notification");
    }

    /**
     * lets the Game start. Only possible if there are two or more Players
     */
    public void startRound(User player) {
        //TODO sollte das vielleicht boolean sein um zu wissen ob es los geht oder nicht?
        //TODO das ganze bei den anderen Methoden auch  (doRaise...)
        if (players.length >= 2 && !is_running) {
            initRound();
        }else{
            JSONObject body = getJsonGameFrame(player);
            pusher.pushToSingle("user_joined_notification", body, player.getWebsession());
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
    public boolean doRaise(User player, int raise) {
        if (isCurrent(player)) {
            int value = active_players.getHighestBet() + raise;
            value = value - player.getAlready_paid();
            pod.add(value);
            player.addAlready_paid(value);
            setLastActions(Actions.RAISE, player);
            setNextUser(player);
            return true;
        }else{
            return false;
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
    public boolean doBet(User player, int bet) {
        if (isCurrent(player)) {
            pod.add(bet);
            player.addAlready_paid(bet);
            setLastActions(Actions.BET, player);
            setNextUser(player);
            return true;
        }else{
            return false;
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
    public boolean doCall(User player) {
        if (isCurrent(player)) {
            int bet = active_players.getHighestBet() - player.getAlready_paid();
            pod.add(bet);
            player.addAlready_paid(bet);
            setLastActions(Actions.CALL, player);
            setNextUser(player);
            return true;
        }else{
            return false;
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
    public boolean doCheck(User player) {
        if (isCurrent(player)) {
            if (lastActionEquals(Actions.CHECK) || (player == first && board[2] != null)) {
                setLastActions(Actions.CHECK, player);
                setNextUser(player);
                return true;
            }
        }
        return false;
    }

    /**
     * does a fold for the given User
     * his last action is set to fold
     *
     * @param player the player who is doing the fold
     */
    public boolean doFold(User player) {
        if (isCurrent(player)) {
            int i = 0;
            setLastActions(Actions.FOLD, player);
            active_players.removeUser(player);
            if (active_players.length == 1) {
                User possible_winner = active_players.getUserByIndex(0);
                noPlayersLeft(possible_winner);
                return true;
            }
            setNextUser(player);
            return true;
        }else{
            return false;
        }
    }

    private void setLastActions(String method, User player) {
        lastActions.add(method);
        player.setLast_action(method);
    }

    /**
     * TODO hier einfügen
     *
     * @param possible_winner
     */
    private void noPlayersLeft(User possible_winner) {
        possible_winner.setLimit(possible_winner.getLimit() + getPodValue());
        startRound(possible_winner);
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
        current = active_players.getUserByIndex(index);
        pushGameDataToUsers("action_notification");
        if (!lastActions.isEmpty() && active_players.allUsersPaidSame() && active_players.allPlayersActionNotNull(big_blind)) {
            if (last_turn) {
                is_running = false;
                //Karten verwerten + Sieger ermitteln
            } else {
                System.out.println("NEUE KARTEN WERDEN AUSGETEILT!!!!!!!!!!");
                dealBoardCards();
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
        active_players.resetAllHandCards();
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
        pushGameDataToUsers("round_starts_notification");
    }

    private void pushGameDataToUsers(String event) {
        for (User user : players.getUsers()) {
            JSONObject body = getJsonGameFrame(user);
            pusher.pushToSingle(event, body, user.getWebsession());
        }
    }

    private JSONObject getJsonGameFrame(User user) {
        JSONArray arr = new JSONArray();
        for (Card card : user.getHandCards()) {
            if (card != null) {
                arr.put(card.getInterfaceHash());
            }
        }
        JSONObject body = new JSONObject();
        body.put("cards", arr);
        body.put("your_turn", isCurrent(user));
        body.put("pod", getPodValue());
        body.put("your_money", user.getLimit());
        body.put("available_methods", getAvailableMethods(user));
        body.put("all_users", players.getInterfaceUserList(small_blind, big_blind));
        return body;
    }

    //TODO möglicher Weise fehlerhaft!!!
    private JSONObject getAvailableMethods(User user) {
        JSONObject _available_methods = new JSONObject();
        if (isCurrent(user)) {
            _available_methods.put(Actions.CHECK, (lastActionEquals(Actions.CHECK) || first == user) && board[2] != null);
            _available_methods.put(Actions.FOLD, true);
            _available_methods.put(Actions.BET, first == user && board[2] != null);
            _available_methods.put(Actions.CALL, lastActionEquals(Actions.BET) || lastActionEquals(Actions.RAISE) || first == user);
            _available_methods.put(Actions.RAISE, first == user || lastActionEquals(Actions.BET) || lastActionEquals(Actions.CHECK));
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
        if (board[0] == null) {
            board[0] = card_stack.pop();
            board[1] = card_stack.pop();
            board[2] = card_stack.pop();
        } else if (board[3].equals(null)) {
            board[3] = card_stack.pop();
        } else if (board[4].equals(null)) {
            last_turn = true;
            board[4] = card_stack.pop();
        }
        //TODO Kann in Funktion mit Array als Parameter ausgelagert werden
        JSONObject body = new JSONObject();
        JSONArray arr = new JSONArray();
        for (Card card : board) {
            if (card != null) {
                arr.put(card.getInterfaceHash());
            }
        }
        //bis hier!
        body.put("cards", arr);
        active_pusher.pushToAll("board_cards_notification",body);
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
        if (!lastActions.isEmpty()) {
            return lastActions.get(lastActions.size() - 1).equals(action);
        } else {
            return false;
        }
    }
}
