package game;

import cards.Card;
import cards.CardStack;
import org.json.JSONArray;
import org.json.JSONObject;
import server.Pusher;
import users.User;
import users.UserList;
import utils.Constants;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the game logic for poker.
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.json.JSONArray
 * @see org.json.JSONObject
 * @see java.util.ArrayList
 * @see java.util.List
 */
public class Game {

    private Pusher pusher;
    private Pusher active_pusher;//for all playing users in current round

    private CardStack card_stack;
    private Card[] board = new Card[5];

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
        pushGameDataToUsers("user_joined_notification", false);
    }

    /**
     * removes the given User from the list of playing Users
     *
     * @param player the User to be removed
     */
    public void leaveGame(User player) {
        pusher.removeUser(player);
        pushGameDataToUsers("user_left_notification", false);
    }

    public UserList getActive_players(){
        return active_players;
    }

    /**
     * lets the Game start. Only possible if there are two or more Players
     */
    public void startRound(User player) {
        //TODO sollte das vielleicht boolean sein um zu wissen ob es los geht oder nicht?
        //TODO das ganze bei den anderen Methoden auch  (doRaise...)
        if (players.length >= 2 && !is_running) {
            JSONObject body = new JSONObject();
            body.put("message", "Runde beginnt");
            pusher.pushToSingle("request_start_round_response", body, player.getWebsession());
            initRound();
        } else {
            JSONObject start_body = new JSONObject();
            start_body.put("message", "Runde konnte nicht gestartet werden");
            pusher.pushToSingle("request_start_round_response", start_body, player.getWebsession());
            JSONObject body = getJsonGameFrame(player, false);
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
            setLastActions(Constants.Actions.RAISE, player);
            JSONObject body = new JSONObject();
            body.put("message", "hat um" + raise + "erhöht");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player, active_players.getIndexOfUser(player));
            return true;
        } else {
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
            setLastActions(Constants.Actions.BET, player);
            JSONObject body = new JSONObject();
            body.put("message", "hat" + bet + "gesetzt");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player, active_players.getIndexOfUser(player));
            return true;
        } else {
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
            setLastActions(Constants.Actions.CALL, player);
            JSONObject body = new JSONObject();
            body.put("message", "hat gecalled");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player, active_players.getIndexOfUser(player));
            return true;
        } else {
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
            setLastActions(Constants.Actions.CHECK, player);
            JSONObject body = new JSONObject();
            body.put("message", "hat gechecked");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player, active_players.getIndexOfUser(player));
            return true;
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
//            setLastActions(Constants.Actions.FOLD, player);
            int index = active_players.getIndexOfUser(player) -1;
            active_players.removeUser(player);
            JSONObject body = new JSONObject();
            body.put("message", "hat gefolded");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player, index);
            return true;
        } else {
            return false;
        }
    }

    private void setLastActions(String method, User player) {
        lastActions.add(method);
        player.setLast_action(method);
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
    private void setNextUser(User current_player, int user_index) {
        previous = current_player;
        int index = (user_index + 1) % active_players.length;
        current = active_players.getUserByIndex(index);
        boolean no_players_left = active_players.length == 1;
        if ((!lastActions.isEmpty() && active_players.allUsersPaidSame() && active_players.allPlayersActionNotNull()) || no_players_left) {
            // wenn alle etwas gemacht haben und gleich viel bezahlt haben. oder wenn nur noch ein aktiver spieler im Raum ist
            if (last_turn || no_players_left) {
                boolean show_player_cards = false;
                is_running = false;
                JSONObject body = new JSONObject();
                User won;
                if (no_players_left) {
                    won = active_players.getUserByIndex(0);
                    body.put("sender", won.getName());
                    body.put("message", "hat gewonnen");
                } else {
                    show_player_cards = true;
                    JSONObject winner = new EvaluateHandCards().getWinner(active_players, board);
                    System.out.println(winner);
                    won = (User) winner.get("user");
                    body.put("sender", won.getName());
                    body.put("message", "hat gewonnen mit " + Constants.Cards.HandValues.getCombinationValue(winner.getInt("value")));
                }
                won.setLimit(won.getLimit() + getPodValue());
                current = null; //no actions available after game is done
                pusher.pushToAll("action_performed_notification", body);
                pushGameDataToUsers("action_notification", show_player_cards);
//                initRound();
                return;
            } else {
                System.out.println("NEUE KARTEN WERDEN AUSGETEILT!!!!!!!!!!");
                lastActions = new ArrayList<>();
                active_players.resetAllPlayersAction();
                if (active_players.contains(small_blind)) {
                    // nur wenn small noch nicht folded hat
                    current = small_blind;
                } else {
                    int small_blind_index = blind_index-1 % active_players.length;
                    current = active_players.getUserByIndex(small_blind_index);
                }
                JSONObject body = new JSONObject();
                body.put("message", "ist jetzt am Zug");
                body.put("sender", current.getName());
                pusher.pushToAll("action_performed_notification", body);
                dealBoardCards();
            }
        }
        pushGameDataToUsers("action_notification", false);
    }

    /**
     * sets the small and the big blind of this round
     * sets the lastAction of the small and big blind user
     */
    private void setBlindPlayers() {
        small_blind = active_players.getUserByIndex(blind_index);
        if (small_blind.payMoney(SMALL_BLIND_VALUE)) {
            setLastActions(Constants.Actions.BET, small_blind);
            pod.add(SMALL_BLIND_VALUE);
        }
        big_blind = active_players.getUserByIndex(raiseBlindIndex());
        if (big_blind.payMoney(BIG_BLIND_VALUE)) {
            setLastActions(Constants.Actions.RAISE, big_blind);
            pod.add(BIG_BLIND_VALUE);
        }
    }

    /**
     * initializes a new Round. creates an empty pod, a new CardStack and gives every player two cards.
     * also sets small and big blind.
     * sets the player who does his first action
     */
    private void initRound() {
        last_turn = false;
        active_players = new UserList(players.getUsers());
        active_players.resetAllPlayersAction();
        active_players.resetAlreadyPaid();
        active_players.resetAllHandCards();
        active_pusher = new Pusher(active_players);
        pod = new ArrayList<>();
        lastActions = new ArrayList<>();
        pod.add(0);
        board = new Card[5];
        card_stack = new CardStack();
        dealCards();
        setBlindPlayers();
        setNextUser(big_blind, active_players.getIndexOfUser(big_blind));
        first = current;
        JSONObject body = new JSONObject();
        body.put("message", "ist jetzt am Zug");
        body.put("sender", current.getName());
        pusher.pushToAll("action_performed_notification", body);
        previous = active_players.getPreviousUser(first);
        is_running = true;
        pushGameDataToUsers("round_starts_notification", false);
    }


    /**
     * Sends a jsonobject to all players. This notification contains the relevant data for the game.
     * @see Game#getJsonGameFrame(User, boolean)
     * @see Pusher#pushToSingle(String, JSONObject, Session)
     * @param event The event for the push notification
     * @param show_player_cards a boolean if the player cards have to be shown or not
     */
    private void pushGameDataToUsers(String event, boolean show_player_cards) {
        for (User user : players.getUsers()) {
            JSONObject body = getJsonGameFrame(user, show_player_cards);
            pusher.pushToSingle(event, body, user.getWebsession());
        }
    }


    /**
     * This method returns a JSONObject which is needed for all game push notification.
     * This JSONObject contains the keys:
     * <ul>
     * <li> cards Type: JSONArray</li>
     * <ul>
     *     <li> value Type: String </li>
     *     <li> symbol Type: String </li>
     * </ul>
     * <li> your_turn Type: Boolean</li>
     * <li> your_money Type: Int</li>
     * <li> call_value Type: Int</li>
     * <li> is_running Type: Boolean</li>
     * <li> all_users Type: JSONArray</li>
     * <ul>
     *     <li>username Type: String</li>
     *     <li>is_big_blind Type: Boolean</li>
     *     <li>is_small_blind Type: Boolean</li>
     *     <li>is_active Type: Boolean</li>
     *     <li>show_cards Type: Boolean</li>
     *     <li>hand_cards Type: JSONArray</li>
     * </ul>
     * </ul>
     * @param user a User object to get access to his hand cards, his limit etc.
     * @param show_player_cards a boolean to decide if the hand cards have to be shown or not.
     * @return descriped above
     */
    private JSONObject getJsonGameFrame(User user, boolean show_player_cards) {
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
        body.put("call_value", players.getCallValueForUser(user));
        body.put("is_running", is_running); //to start a new round in frontend
        body.put("all_users", players.getInterfaceUserList(small_blind, big_blind, current, show_player_cards));
        return body;
    }

    private JSONObject getAvailableMethods(User user) {
        JSONObject _available_methods = new JSONObject();
        if (isCurrent(user)) {
            _available_methods.put(Constants.Actions.CHECK, (lastActionEquals(Constants.Actions.CHECK) || (lastActions.isEmpty())));
            _available_methods.put(Constants.Actions.FOLD, true);
            _available_methods.put(Constants.Actions.BET, lastActionEquals(Constants.Actions.CHECK) || (lastActions.isEmpty()));
            _available_methods.put(Constants.Actions.CALL, (lastActionEquals(Constants.Actions.BET) || lastActionEquals(Constants.Actions.CALL) || lastActionEquals(Constants.Actions.RAISE)));
            _available_methods.put(Constants.Actions.RAISE, lastActionEquals(Constants.Actions.RAISE) || lastActionEquals(Constants.Actions.BET) || lastActionEquals(Constants.Actions.CALL));
        }
        return _available_methods;
    }

    /**
     * A method to say if a user is the first player
     * @param user to check up
     * @return true when first player else false
     */
    private boolean isFirstPlayer(User user) {
        return first == user && first.getLast_action().equals("");
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
     * sends a push notification to all players with the board cards
     */
    private void dealBoardCards() {
        card_stack.pop();//burn card;
        if (board[0] == null) {
            board[0] = card_stack.pop();
            board[1] = card_stack.pop();
            board[2] = card_stack.pop();
        } else if (board[3] == (null)) {
            board[3] = card_stack.pop();
        } else if (board[4] == null) {
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
        active_pusher.pushToAll("board_cards_notification", body);
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

    /**
     * increase the blind index to the index of the next small blind
     * @return the blind index
     */
    private int raiseBlindIndex() {
        blind_index = ++blind_index % active_players.length;
        return blind_index;
    }

    /**
     * Check if the last element of the lastActions list is equal
     * to the action given as parameter
     * @param action to be checked up
     * @return true when the last element of lastAction is equal to the given action else false
     */
    private boolean lastActionEquals(String action) {
        return !lastActions.isEmpty() && lastActions.get(lastActions.size() - 1).equals(action);
    }
}
