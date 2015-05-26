import java.util.*;

/**
 * Created by loster on 20.05.2015.
 */
public class CardStack {

    private final String [] POSSIBLE_SYMBOLS = {"Herz","Pik","Kreuz","Karo"};
    private final int [] POSSIBLE_VALUES = {2,3,4,5,6,7,8,9,10,11,12,13,14};

    private Stack<Card> cards;

    public CardStack(){
        cards = initAllCards();
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card pop(){
        return cards.pop();
    }

    private Stack<Card> initAllCards() {
        Stack<Card> cards = new Stack<>();

        for (String symbol: POSSIBLE_SYMBOLS) {
            for (int value: POSSIBLE_VALUES){
                cards.add(new Card(value, symbol));
            }
        }
        shuffle();
        return cards;
    }

}
