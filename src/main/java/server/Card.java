package server;

import com.google.common.collect.ComparisonChain;

import java.util.HashMap;

public class Card implements Comparable<Card>{

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


    @Override
    public int compareTo(Card card) {
        return ComparisonChain.start()
                .compare(value, card.value)
//                .compare(symbol, card.symbol)
                .result();
    }
}
