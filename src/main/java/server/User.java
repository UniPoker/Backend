package server;

import javax.websocket.Session;

/**
 * Created by Stefan on 17.05.2015.
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
     * ONLY FOR TESTS
     */
    public User() {
        hand_cards = new Card[2];
    }

    /**
     * Constructor
     *
     * @param websession Session of connected server.User
     * @param name       Name of connected server.User
     */
    User(Session websession, String name, String password) {
        hand_cards = new Card[2];
        this.websession = websession;
        this.name = name;
        this.password = password;
    }

    public User(Session session) {
        hand_cards = new Card[2];
        this.websession = session;
    }

    public boolean payMoney(int amount) {
        if (limit >= amount) {
            limit -= amount;
            already_paid += amount;
            return true;
        } else {
            return false;
        }
    }

    public Session getWebsession() {
        return websession;
    }

    public void setWebsession(Session websession) {
        this.websession = websession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoomId() {
        return room_id;
    }

    public void setRoomId(int room_id) {
        this.room_id = room_id;
    }

    public Card[] getHandCards() {
        return hand_cards;
    }

    public void setHandCards(Card hand_card) {
        if (hand_cards[0] == null) {
            hand_cards[0] = hand_card;
        } else if (hand_cards[1] == null) {
            hand_cards[1] = hand_card;
        }
    }

    public void resetHandCards() {
        hand_cards = new Card[2];
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getPassword() {
        return password;
    }

    public int getAlready_paid() {
        return already_paid;
    }

    public void addAlready_paid(int already_paid) {
        this.already_paid += already_paid;
        this.limit -= already_paid;
    }

    public void resetPayment(){
        this.already_paid = 0;
    }

    public void setLast_action(String action){
        last_action = action;
    }

    public String getLast_action(){
        return last_action;
    }

}