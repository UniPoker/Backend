package cards;


import utils.Constants;

import java.util.Collections;
import java.util.Stack;

/**
 * Represents the whole Card Stack with 52 cards in it.
 * The Stack holds one instance of each card.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see java.util.Collection
 * @see java.util.Stack
 */
public class CardStack {


    /**
     * An Array of all possible symbols a card could have (hearts,spades,clubs,diamonds)
     */
    private final String[] POSSIBLE_SYMBOLS = Constants.Cards.Symbols.ALL_SYMBOLS;

    /**
     * An Array of all possible values a card could have.
     * Int from 2 - 14.
     */
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
     * @see Card
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
