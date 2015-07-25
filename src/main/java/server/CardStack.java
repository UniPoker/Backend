package server;

import java.util.*;

/**
 * Created by loster on 20.05.2015.
 */
public class CardStack {
    private final String[] POSSIBLE_SYMBOLS = {"Herz", "Pik", "Kreuz", "Karo"};
    private final int[] POSSIBLE_VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    private Stack<Card> cards;

    /**
     * creates a new CardStack and initializes all the cards with their value (shuffled)
     */
    public CardStack() {
        cards = initAllCards();
    }

    /**
     * shuffles the Cards of the CardStack
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * removes the Card at the top of the CardStack and returns that Card
     *
     * @return the removed Card
     */
    public Card pop() {
        return cards.pop();
    }

    /**
     * initializes the CardStack with every Card needed and shuffles them
     *
     * @return returns the shuffled Cards
     */
    private Stack<Card> initAllCards() {
        Stack<Card> cards = new Stack<>();

        for (String symbol : POSSIBLE_SYMBOLS) {
            for (int value : POSSIBLE_VALUES) {
                cards.add(new Card(value, symbol));
            }
        }
        shuffle();
        return cards;
    }

}
