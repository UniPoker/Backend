package server;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jetty.util.ArrayUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Stream;

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
        } else {
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
            setLastActions(Constants.Actions.RAISE, player);
            JSONObject body = new JSONObject();
            body.put("message", "hat um" + raise + "erhöht");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player);
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
            setNextUser(player);
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
            setNextUser(player);
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
            if (lastActionEquals(Constants.Actions.CHECK) || (player == first && board[2] != null)) {
                setLastActions(Constants.Actions.CHECK, player);
                JSONObject body = new JSONObject();
                body.put("message", "hat gechecked");
                body.put("sender", player.getName());
                pusher.pushToAll("action_performed_notification", body);
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
            setLastActions(Constants.Actions.FOLD, player);
            active_players.removeUser(player);
            JSONObject body = new JSONObject();
            body.put("message", "hat gefolded");
            body.put("sender", player.getName());
            pusher.pushToAll("action_performed_notification", body);
            setNextUser(player);
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
        boolean no_players_left = active_players.length == 1;
        if ((!lastActions.isEmpty() && active_players.allUsersPaidSame() && active_players.allPlayersActionNotNull()) || no_players_left) {
            if (last_turn || no_players_left) {
                is_running = false;
                JSONObject body = new JSONObject();
                User won;
                if (no_players_left) {
                    won = active_players.getUserByIndex(0);
                    body.put("sender", won.getName());
                    body.put("message", "hat gewonnen");
                } else {
                    JSONObject winner = getWinner();
                    System.out.println(winner);
                    won = (User) winner.get("user");
                    body.put("sender", won.getName());
                    body.put("message", "hat gewonnen mit " + winner.getInt("value"));
                }
                won.setLimit(won.getLimit() + getPodValue());
                pusher.pushToAll("action_performed_notification", body);
                initRound();
                return;
                //Karten verwerten + Sieger ermitteln
            } else {
                System.out.println("NEUE KARTEN WERDEN AUSGETEILT!!!!!!!!!!");
                lastActions = new ArrayList<>();
                active_players.resetAllPlayersAction();
                current = small_blind;
                JSONObject body = new JSONObject();
                body.put("message", "ist jetzt am Zug");
                body.put("sender", current.getName());
                pusher.pushToAll("action_performed_notification", body);
                dealBoardCards();
            }
        }
        pushGameDataToUsers("action_notification");
    }

    private JSONObject getWinner() {
        JSONObject highest_value = new JSONObject().put("value", -1);
        for (User user : active_players.getUsers()) {
//            Card[] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
            Card[] _cards = ArrayUtils.addAll(board, user.getHandCards());
            ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
            Collections.sort(cards);
            Collections.reverse(cards); //damit absteigend
            JSONObject best_possibility = getBestHand(cards, user);
            if (highest_value.getInt("value") < best_possibility.getInt("value")) {
                highest_value = best_possibility;
            } else if (highest_value.getInt("value") == best_possibility.getInt("value")) {
                User hihgest_user = (User) highest_value.get("user");
                Card[] highest_combination = (Card[]) highest_value.get("cards");
                Card[] challenging_combination = (Card[]) best_possibility.get("cards");

                int highest_user_value = 0; //momentaner sieger
                int user_value = 0; //herausforderer
                for (int i = 0; i < highest_combination.length; i++) {
                    highest_user_value += highest_combination[i].getValue();
                    user_value += challenging_combination[i].getValue();
                }
                if (highest_user_value < user_value) {
                    highest_value = best_possibility;
                } else if (highest_user_value == user_value) {
                    highest_user_value = 0; //momentaner sieger
                    user_value = 0;
                    Card[] highest_cards = ArrayUtils.addAll(board, user.getHandCards());
                    for (int i = 0; i < highest_cards.length; i++) {
                        highest_user_value += highest_cards[i].getValue();
                        user_value += cards.get(i).getValue();
                    }
                    if (highest_user_value < user_value) {
                        highest_value = best_possibility;
                    } else if (highest_user_value == user_value) {
                        //TODO SPLITPOT
                        System.out.println("Splitpot implementieren");
                    }
                }
            }
//            if (hand_value > highest_value) {
//                highest_value = hand_value;
//                possible_winner = user;
//            }
        }
        return highest_value;
    }

    public JSONObject getBestHand(ArrayList<Card> cards, User user) {
        Card[] best_cards;
        if ((best_cards = getRoyalFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.ROYAL_FLUSH);
        } else if ((best_cards = getStraightFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.STRAIGHT_FLUSH);
        } else if ((best_cards = getQuads(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.QUADS);
        } else if ((best_cards = getFullHouse(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.FULL_HOUSE);
        } else if ((best_cards = getFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.FLUSH);
        } else if ((best_cards = getStraight(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.STRAIGHT);
        } else if ((best_cards = getTrips(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.TRIPS);
        } else if ((best_cards = getTwoPair(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.TWO_PAIR);
        } else if ((best_cards = getPair(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.PAIR);
        }
        best_cards = getHighCard(user);
        return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.HIGH_CARD);
    }

    public Card[] getRoyalFlush(ArrayList<Card> cards) {
        int needed_value;
        for (int i = 0; i < 3; i++) {
            Card _card = cards.get(i);
            needed_value = Constants.Cards.Value.ACE;
            if (_card.getValue() == needed_value) {
                Card[] royal_flush = cardsContainStraightFlush(cards, needed_value, _card);
                return royal_flush;
            }
        }
        return null;
    }

    private Card[] cardsContainStraightFlush(ArrayList<Card> cards, int needed_value, Card first_card) {
        ArrayList<Card> return_cards;
        String highest_card_symbol;
        highest_card_symbol = first_card.getSymbol();
        return_cards = new ArrayList<>();
        for (int a = 0; a < 5; a++) {
            if (containsCardByValueAndSymbol(cards, highest_card_symbol, needed_value)) {
                needed_value--;
                return_cards.add(new Card(needed_value, highest_card_symbol));
                if (a == 4) {
                    return return_cards.toArray(new Card[return_cards.size()]);
                }
            } else {
                break;
            }
        }
        return null;
    }

    public Card[] getStraightFlush(ArrayList<Card> cards) {
        int needed_value;
        for (int i = 0; i < 3; i++) {
            Card _card = cards.get(i);
            needed_value = _card.getValue();
            Card[] royal_flush = cardsContainStraightFlush(cards, needed_value, _card);
            return royal_flush;
        }
        return null;
    }

    public Card[] getQuads(ArrayList<Card> cards) {
        return sameAmountOfValue(cards, 4);
    }

    public Card[] getFlush(ArrayList<Card> cards) {
        for (int i = 0; i < 3; i++) {
            Card card = cards.get(i);
            Card[] found_cards = getCardsBySymbol(cards, card.getSymbol());
            if (found_cards.length == 5) {
                return found_cards;
            }
        }
        return null;
    }

    public Card[] getFullHouse(ArrayList<Card> cards) {
        Card[] trips = getTrips(cards);
        if (trips != null) {
            for (Card card : trips) {
                cards.removeIf(c -> c == card);
            }
            Card[] pair = getPair(cards);
            if (pair != null) {
                Card[] full_house = ArrayUtils.addAll(trips, pair);
                return full_house;
            }
        }

        return null;
    }

    public Card[] getStraight(ArrayList<Card> cards) {
        int needed_value;
        ArrayList<Card> return_cards;
        for (int i = 0; i < 3; i++) {
            Card _card = cards.get(i);
            needed_value = _card.getValue();
            return_cards = new ArrayList<>();
            for (int a = 0; a < 5; a++) {
                if (containsCardByValue(cards, needed_value)) {
                    needed_value--;
                    return_cards.add(new Card(needed_value, cards.get(i + a).getSymbol()));
                    if (a == 4) {
                        return return_cards.toArray(new Card[return_cards.size()]);
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

    public Card[] getTrips(ArrayList<Card> cards) {
        return sameAmountOfValue(cards, 3);
    }

    public Card[] getTwoPair(ArrayList<Card> cards) {
        Card[] first_pair = getPair(cards);
        if (first_pair != null) {
            for (Card card : first_pair) {
                cards.removeIf(c -> c == card);
            }
            Card[] second_pair = getPair(cards);
            if (second_pair != null) {
                Card[] two_pair = ArrayUtils.addAll(first_pair, second_pair);
                return two_pair;
            }
        }
        return null;
    }

    public Card[] getPair(ArrayList<Card> cards) {
        return sameAmountOfValue(cards, 2);
    }

    public Card[] getHighCard(User user) {
        Card[] cards = user.getHandCards();
        if (cards[0].getValue() >= cards[1].getValue()) {
            return new Card[]{cards[0]};
        } else {
            return new Card[]{cards[1]};
        }
    }

    private boolean containsCardByValueAndSymbol(List<Card> list, String symbol, int value) {
        return list.stream().filter(o -> o.getSymbol().equals(symbol) && o.getValue() == value).findFirst().isPresent();
    }

    private boolean containsCardByValue(List<Card> list, int value) {
        return list.stream().filter(o -> o.getValue() == value).findFirst().isPresent();
    }

    private Card[] getCardsByValue(List<Card> list, int value) {
        return list.stream().filter(o -> o.getValue() == value).toArray(Card[]::new);
    }

    private Card[] getCardsBySymbol(List<Card> list, String symbol) {
        return list.stream().filter(o -> o.getSymbol() == symbol).toArray(Card[]::new);
    }

    private Card[] sameAmountOfValue(ArrayList<Card> cards, int matches) {
        int steps = cards.size() - matches;
        for (int i = 0; i <= steps; i++) {
            Card card = cards.get(i);
            Card[] found_cards = getCardsByValue(cards, card.getValue());
            if (found_cards.length == matches) {
                return found_cards;
            }
        }
        return null;
    }

//    private boolean hasCard (int value, String color){
//
//    }
//
//    private boolean gotRoyalFlush(User user) {
//        return false;
//    }

    /**
     * sets the small and the big blind of this round
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
        setNextUser(big_blind);
        first = current;
        JSONObject body = new JSONObject();
        body.put("message", "ist jetzt am Zug");
        body.put("sender", current.getName());
        pusher.pushToAll("action_performed_notification", body);
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
            _available_methods.put(Constants.Actions.CHECK, (lastActionEquals(Constants.Actions.CHECK) || (lastActions.isEmpty())));
            _available_methods.put(Constants.Actions.FOLD, true);
            _available_methods.put(Constants.Actions.BET, lastActionEquals(Constants.Actions.CHECK) || (lastActions.isEmpty()));
            _available_methods.put(Constants.Actions.CALL, (lastActionEquals(Constants.Actions.BET) || lastActionEquals(Constants.Actions.CALL) ||lastActionEquals(Constants.Actions.RAISE)));
            _available_methods.put(Constants.Actions.RAISE, lastActionEquals(Constants.Actions.RAISE) || lastActionEquals(Constants.Actions.BET) || lastActionEquals(Constants.Actions.CALL));
        }
        return _available_methods;
    }

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
