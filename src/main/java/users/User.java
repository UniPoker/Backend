package users;

import cards.Card;
import utils.Constants;

import javax.websocket.Session;

/**
 * This class represents a single user who wants to connect to the server.
 * Each user holds several attribute needed to play a game.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see javax.websocket.Session
 */
public class User {

    private Session websession;

    private String name;
    private String password;
    private String last_action = "";

    private int room_id = -1;
    private int limit = 1000;
    private int already_paid = 0;

    private Card[] hand_cards;

    /**
     * A constructor only needed for test cases.
     */
    public User() {
        hand_cards = new Card[2];
    }

    /**
     * Constructor
     *
     * @param websession Session of connected users.User
     * @param name       Name of connected users.User
     */
    User(Session websession, String name, String password) {
        hand_cards = new Card[2];
        this.websession = websession;
        this.name = name;
        this.password = password;
    }

    /**
     * Constructor
     *
     * @param session Session of connected users.User
     */
    public User(Session session) {
        hand_cards = new Card[2];
        this.websession = session;
    }

    /**
     * This method returns true if the user is able to pay a given amount
     *
     * @param amount to be paid
     * @return true when succesfull else false
     */
    public boolean payMoney(int amount) {
        if (limit >= amount) {
            limit -= amount;
            already_paid += amount;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the session of the user
     */
    public Session getWebsession() {
        return websession;
    }

    /**
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * @return the room id of the room the user is in.
     */
    public int getRoomId() {
        return room_id;
    }

    /**
     * set the room id attribute of the user to the given value.
     *
     * @param room_id
     */
    public void setRoomId(int room_id) {
        this.room_id = room_id;
    }

    /**
     * @return all handcards the user has.
     */
    public Card[] getHandCards() {
        return hand_cards;
    }

    /**
     * add cards to the users "hand".
     *
     * @param hand_card to add to his hand
     */
    public void setHandCards(Card hand_card) {
        if (hand_cards[0] == null) {
            hand_cards[0] = hand_card;
        } else if (hand_cards[1] == null) {
            hand_cards[1] = hand_card;
        }
    }

    /**
     * reinitilize the hand cards of the user.
     */
    public void resetHandCards() {
        hand_cards = new Card[2];
    }

    /**
     * @return the limit of the user
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the users limit to a new value.
     *
     * @param limit to be set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the money the user already paid.
     */
    public int getAlready_paid() {
        return already_paid;
    }

    /**
     * add money to the already paid attribute
     * and subtract money from the limit of the user.
     *
     * @param already_paid value to add to the already paid attribute of the user
     */
    public void addAlready_paid(int already_paid) {
        this.already_paid += already_paid;
        this.limit -= already_paid;
    }

    /**
     * reset the already paid attribute to 0.
     */
    public void resetPayment(){
        this.already_paid = 0;
    }

    /**
     * set the last action attribute of the user.
     *
     * @param action value which should overwrite the last action
     * @see Constants.Actions
     */
    public void setLast_action(String action){
        last_action = action;
    }

    /**
     * @return the last action of the user
     * @see Constants.Actions
     */
    public String getLast_action(){
        return last_action;
    }
}