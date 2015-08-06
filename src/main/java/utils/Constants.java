package utils;

/**
 * Constants is a helper class which holds subclasses.
 * These subclasses have static constants, that they are accessible
 * from all over the project.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 */
public final class Constants {


    private Constants(){}

    /**
     * Holds every constant class needed for cards.
     */
    public static final class Cards{
        private Cards(){}

        /**
         * This Class holds every value a card could have.
         */
        public static final class Value{
            public static final int ACE = 14;

            public static final int KING = 13;
            public static final int QUEEN = 12;
            public static final int JACK = 11;
            public static final int TEN = 10;
            public static final int NINE = 9;
            public static final int EIGHT = 8;
            public static final int SEVEN = 7;
            public static final int SIX = 6;
            public static final int FIVE = 5;
            public static final int FOUR = 4;
            public static final int THREE = 3;
            public static final int TWO = 2;
            public static final int[] ALL_VALUES = {ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO};
            private Value(){}
        }

        /**
         * This Class holds every symbol a card could have.
         */
        public static final class Symbols{
            public static final String HEARTS = "hearts";
            public static final String SPADES = "spades";
            public static final String CLUBS = "clubs";
            public static final String DIAMONDS = "diamonds";

            public static final String[] ALL_SYMBOLS = {HEARTS,SPADES,CLUBS,DIAMONDS};
        }


        /**
         * This class holds the value of combinations a user could have.
         * Each constant is an int value which represents the
         * value of the combination the user has.
         */
        public static final class HandValues {
            private HandValues(){}

            public static final int ROYAL_FLUSH = 10;
            public static final int STRAIGHT_FLUSH = 9;
            public static final int QUADS = 8;
            public static final int FULL_HOUSE = 7;
            public static final int FLUSH = 6;
            public static final int STRAIGHT = 5;
            public static final int TRIPS = 4;
            public static final int TWO_PAIR = 3;
            public static final int PAIR = 2;
            public static final int HIGH_CARD = 1;

            /**
             * @param value the value of the combination the user have.
             * @return the string representation of the combination.
             */
            public static final String getCombinationValue(int value) {
                switch(value){
                    case HIGH_CARD:
                        return "High Card";
                    case PAIR:
                        return "einem Paar";
                    case TWO_PAIR:
                        return "einem Two Pair";
                    case TRIPS:
                        return "einem Drilling";
                    case FLUSH:
                        return "einem Flush";
                    case FULL_HOUSE:
                        return "einem Full House";
                    case QUADS:
                        return "einem Vierling";
                    case ROYAL_FLUSH:
                        return "einem Royal Flush!!";
                    case STRAIGHT:
                        return "einer Straße";
                    case STRAIGHT_FLUSH:
                        return "einer Straight Flush";
                    default:
                        return "";
                }
            }

        }
    }

    /**
     * This class holds all possible actions a user could perform during his turn.
     */
    public final class Actions {

        private Actions(){} //

        public static final String RAISE = "raise";
        public static final String FOLD = "fold";
        public static final String CHECK = "check";
        public static final String CALL = "call";
        public static final String BET = "bet";

    }


}
