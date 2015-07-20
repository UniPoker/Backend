package server;

import javax.websocket.Session;

/**
 * Created by Stefan on 17.05.2015.
 */
public class User {

    private Session websession;
    private String name;
    private String password;
    private int room_index = -1;
    private Card[] hand_cards;
    private String last_action;
    private boolean has_folded;

    private double limit = 1000.00;

    /**
     * Constructor
     * @param websession Session of connected server.User
     * @param name Name of connected server.User
     */
    User(Session websession, String name, String password) {
        hand_cards = new Card[2];
        this.websession = websession;
        this.name = name;
        this.password = password;
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

    public int getRoomIndex() {
        return room_index;
    }

    public void setRoomIndex(int room_index) {
        this.room_index = room_index;
    }

    public Card[] getHandCards() {
        return hand_cards;
    }

    public void setHandCards(Card hand_card) {
        if(hand_cards[0].equals(null)){
            hand_cards[0] = hand_card;
        }else if(hand_cards[1].equals(null)){
            hand_cards[1] = hand_card;
        }
    }

    public void resetHandCards(){
        hand_cards = new Card[2];
    }

    public String getLastAction() {
        return last_action;
    }

    public void setLastAction(String last_action) {
        this.last_action = last_action;
    }

    public boolean hasFolded() {
        return has_folded;
    }

    public void setHasFolded(boolean has_folded) {
        this.has_folded = has_folded;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public String getPassword() {
        return password;
    }
}