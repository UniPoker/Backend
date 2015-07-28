package server;

import java.util.HashMap;

/**
 * Created by loster on 20.05.2015.
 */
public class Card {

    private int value;
    private String symbol;

    /**
     * create a new Card with the given value and symbol
     *
     * @param value  the value of the card, represented by an Integer
     * @param symbol the symbol of the card, represented by a String ("hearts","spades","clubs","diamonds")
     */
    public Card(int value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    /**
     * get the value of the card
     *
     * @return returns the value of the card (Integer)
     */
    public int getValue() {
        return value;
    }

    /**
     * get the symbol of the card
     *
     * @return returns the symbol of the card (String)
     */
    public String getSymbol() {
        return symbol;
    }

    public HashMap<String, String> getInterfaceHash() {
        HashMap<String, String> map = new HashMap<>();
        map.put("value", Integer.toString(value));
        map.put("symbol", symbol);
        return map;
    }
}
