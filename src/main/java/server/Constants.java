package server;

/**
 * Created by loster on 02.08.2015.
 */
public final class Constants {

    private Constants(){}

    public static final class Cards{
        private Cards(){}

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

        public static final class Symbols{
            public static final String HEARTS = "hearts";
            public static final String SPADES = "spades";
            public static final String CLUBS = "clubs";
            public static final String DIAMONDS = "diamonds";

            public static final String[] ALL_SYMBOLS = {HEARTS,SPADES,CLUBS,DIAMONDS};
        }

        public final class HandValues {
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

        }
    }

    public final class Actions {

        private Actions(){} //

        public static final String RAISE = "raise";
        public static final String FOLD = "fold";
        public static final String CHECK = "check";
        public static final String CALL = "call";
        public static final String BET = "bet";

    }


}
