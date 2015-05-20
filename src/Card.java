/**
 * Created by loster on 20.05.2015.
 */
public class Card {

    private int value;
    private String symbol;

    public Card(int value, String symbol){
        this.value = value;
        this.symbol = symbol;
    }

    public int getValue() {
        return value;
    }

    public String getSymbol() {
        return symbol;
    }
}
