package server;

import java.util.*;

/**
 * Created by loster on 20.05.2015.
 */
public class CardStack {
    private final String[] POSSIBLE_SYMBOLS = Constants.Cards.Symbols.ALL_SYMBOLS;
    private final int[] POSSIBLE_VALUES = Constants.Cards.Value.ALL_VALUES;

    private Stack<Card> cards;

    /**
     * creates a new CardStack and initializes all the cards with their value (shuffled)
     */
    public CardStack() {
        initAllCards();
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
    private void initAllCards() {
        cards = new Stack<>();

        for (String symbol : POSSIBLE_SYMBOLS) {
            for (int value : POSSIBLE_VALUES) {
                cards.add(new Card(value, symbol));
            }
        }
        shuffle();
    }

}
