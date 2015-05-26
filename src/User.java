import javax.websocket.Session;

/**
 * Created by Stefan on 17.05.2015.
 */
public class User {

    private Session websession;
    private String name;
    private int room_index;
    private Card[] hand_cards;

    private double limit = 1000.00;

    /**
     * Constructor
     * @param websession Session of connected User
     * @param name Name of connected User
     */
    User(Session websession, String name) {
        hand_cards = new Card[2];
        this.websession = websession;
        this.name = name;
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

}