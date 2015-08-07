package game;

import cards.Card;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import users.User;
import users.UserList;
import utils.Constants;
import utils.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Helper Class which helps to evaluate the value
 * of the card combination each player has.
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.apache.commons.lang3.ArrayUtils
 * @see org.json.JSONObject
 * @see java.util.ArrayList
 * @see java.util.Arrays
 * @see java.util.Collection
 * @see java.util.List
 */
public class EvaluateHandCards {


    /**
     * This method evaluate the players combination by its value.
     * It also has to check the winner  when two players got the same combination.
     * @param active_players All active player whose card combinations have to be evaluated.
     * @param board The cards which are on the board which belongs to the players combination.
     * @return a json object with the winner user, his combination value and his cards.
     */
    public JSONObject getWinner(UserList active_players, Card[] board) {
        JSONObject highest_value = new JSONObject().put("value", -1);
        for (User current_user : active_players.getUsers()) {
            Card[] _cards = ArrayUtils.addAll(board, current_user.getHandCards());
            ArrayList<Card> current_cards = new ArrayList<>(Arrays.asList(_cards));
            Collections.sort(current_cards);
            Collections.reverse(current_cards); //damit absteigend
            JSONObject best_possibility = getBestHand(current_cards, current_user);
            if (highest_value.getInt("value") < best_possibility.getInt("value")) {
                highest_value = best_possibility;
            } else if (highest_value.getInt("value") == best_possibility.getInt("value")) {
                // wenn gleichwertige kombination, herausfinden, welche höher ist
                Card[] highest_combination = (Card[]) highest_value.get("cards");
                Card[] challenging_combination = (Card[]) best_possibility.get("cards");

                int highest_user_value = 0; //momentaner sieger
                int user_value = 0; //herausforderer
                for (int i = 0; i < highest_combination.length; i++) {
                    highest_user_value += highest_combination[i].getValue();
                    user_value += challenging_combination[i].getValue();
                }
                if (highest_user_value < user_value) {
                    highest_value = best_possibility;
                } else if (highest_user_value == user_value) {
                    // wenn kombination gleichwertig, dann wer höhere handkarten hat
                    highest_user_value = 0; //momentaner sieger
                    user_value = 0;
                    User _user = (User)highest_value.get("user");
                    Card[] highest_cards = ArrayUtils.addAll(board, current_user.getHandCards());
                    Card[] _current_cards = ArrayUtils.addAll(board, _user.getHandCards());
                    for (int i = 0; i < highest_cards.length; i++) {
                        highest_user_value += highest_cards[i].getValue();
                        user_value += _current_cards[i].getValue();
                    }
                    if (highest_user_value > user_value) {
                        highest_value = best_possibility;
                    } else if (highest_user_value == user_value) {
                        //TODO SPLITPOT
                        System.out.println("Splitpot implementieren");
                    }
                }
            }
        }
        return highest_value;
    }

    /**
     * This method evaluates the cards of a single user.
     * @param cards A List of all cards (Handcards of the user + board cards)
     * @param user The user whose combination have to be checked
     * @return a JSONObject with the user, his card combination and its value representation.
     * @see Constants.Cards.HandValues
     */
    public JSONObject getBestHand(ArrayList<Card> cards, User user) {
        Card[] best_cards;
        if ((best_cards = getRoyalFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.ROYAL_FLUSH);
        } else if ((best_cards = getStraightFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.STRAIGHT_FLUSH);
        } else if ((best_cards = getQuads(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.QUADS);
        } else if ((best_cards = getFullHouse(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.FULL_HOUSE);
        } else if ((best_cards = getFlush(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.FLUSH);
        } else if ((best_cards = getStraight(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.STRAIGHT);
        } else if ((best_cards = getTrips(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.TRIPS);
        } else if ((best_cards = getTwoPair(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.TWO_PAIR);
        } else if ((best_cards = getPair(new ArrayList<Card>(cards))) != null) {
            return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.PAIR);
        }
        best_cards = getHighCard(user);
        return Helper.getWinnerJSON(user, best_cards, Constants.Cards.HandValues.HIGH_CARD);
    }

    /**
     * This method checks if the cards represents a royal flush.
     * A royal flush is the card combination 10, jack, queen, kind and ace, all with the same symbol
     * @param cards the cards which have to be checked if they represents a royal flush
     * @return a card array if it is a royal flush else it returns null
     */
    public Card[] getRoyalFlush(ArrayList<Card> cards) {
        int needed_value;
        for (int i = 0; i < 3; i++) {
            Card _card = cards.get(i);
            needed_value = Constants.Cards.Value.ACE;
            if (_card.getValue() == needed_value) {
                Card[] royal_flush = cardsContainStraightFlush(cards, needed_value, _card);
                return royal_flush;
            }
        }
        return null;
    }

    /**
     * This method checks if the cards contains a straight flush starting by the first_card
     * @param cards all cards which could possibly contains a straight flush
     * @param needed_value the highest value of the straight
     * @param first_card needed for the symbol of the straight
     * @return null when there is no straigh flush or the card Array holding the straight flush.
     */
    private Card[] cardsContainStraightFlush(ArrayList<Card> cards, int needed_value, Card first_card) {
        ArrayList<Card> return_cards;
        String highest_card_symbol;
        highest_card_symbol = first_card.getSymbol();
        return_cards = new ArrayList<>();
        for (int a = 0; a < 5; a++) {
            if (containsCardByValueAndSymbol(cards, highest_card_symbol, needed_value)) {
                needed_value--;
                return_cards.add(new Card(needed_value, highest_card_symbol));
                if (a == 4) {
                    return return_cards.toArray(new Card[return_cards.size()]);
                }
            } else {
                break;
            }
        }
        return null;
    }

    /**
     * This method checks if the cards represents a straight flush.
     * A straight flush are 5 cards in sequence, all in the same suit.
     * @param cards the cards which have to be checked if they represents a straight flush
     * @return a card array if it is a straight flush else it returns null
     */
    public Card[] getStraightFlush(ArrayList<Card> cards) {
        int needed_value;
        for (int i = 0; i < 3; i++) {
            Card _card = cards.get(i);
            needed_value = _card.getValue();
            Card[] staright_flush = cardsContainStraightFlush(cards, needed_value, _card);
            return staright_flush;
        }
        return null;
    }

    /**
     * This method checks if the cards represents quads.
     * This means there have to be four cards with the same value.
     * @param cards the cards which have to be checked if they represents quads
     * @return a card array if it is quads else it returns null
     * @see EvaluateHandCards#sameAmountOfValues(ArrayList, int)
     */
    public Card[] getQuads(ArrayList<Card> cards) {
        return sameAmountOfValues(cards, 4);
    }

    /**
     * This method checks if the cards represents a flush.
     * The card list have to contain five cards with the same value.
     * @param cards the cards which have to be checked if they represents a flush
     * @return a card array if it is a flush else it returns null
     * @see EvaluateHandCards#sameAmountOfValues(ArrayList, int)
     */
    public Card[] getFlush(ArrayList<Card> cards) {
        for (int i = 0; i < 3; i++) {
            Card card = cards.get(i);
            Card[] found_cards = getCardsBySymbol(cards, card.getSymbol());
            if (found_cards.length == 5) {
                return found_cards;
            }
        }
        return null;
    }

    /**
     * This method checks if the cards represents a full house.
     * There have to be five cards of the same symbol
     * @param cards the cards which have to be checked if they represents a full house
     * @return a card array if it is a full house else it returns null
     */
    public Card[] getFullHouse(ArrayList<Card> cards) {
        Card[] trips = getTrips(cards);
        if (trips != null) {
            for (Card card : trips) {
                cards.removeIf(c -> c == card);
            }
            Card[] pair = getPair(cards);
            if (pair != null) {
                Card[] full_house = ArrayUtils.addAll(trips, pair);
                return full_house;
            }
        }

        return null;
    }

    /**
     * This method checks if the cards represents a straight.
     * This means the card list have to contain 5 cards of mixed symbols in sequence
     * @param cards the cards which have to be checked if they represents a straight
     * @return a card array if it is a straight else it returns null
     */
    public Card[] getStraight(ArrayList<Card> cards) {
        int needed_value;
        ArrayList<Card> return_cards;
        for (int i = 0; i < cards.size(); i++) {
            Card _card = cards.get(i);
            needed_value = _card.getValue();
            return_cards = new ArrayList<>();
            for (int a = 0; a < 5; a++) {
                if (containsCardByValue(cards, needed_value)) {
                    Card found_card = getCardsByValue(cards, needed_value)[0];
                    needed_value = (needed_value - 1) % 14;
                    needed_value = needed_value == 1 ? 14 : needed_value;
                    return_cards.add(found_card);
                    if (a == 4) {
                        return return_cards.toArray(new Card[return_cards.size()]);
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

    /**
     * This method checks if the cards represents trips.
     * This means the card list have to contain three cards with the same value.
     * @param cards the cards which have to be checked if they represents trips
     * @return a card array if it is trips else it returns null
     * @see EvaluateHandCards#sameAmountOfValues(ArrayList, int)
     */
    public Card[] getTrips(ArrayList<Card> cards) {
        return sameAmountOfValues(cards, 3);
    }

    /**
     * This method checks if the cards represents a two pair.
     * This means the card list have to contain two combination of cards with the same value.
     * @param cards the cards which have to be checked if they represents a two pair
     * @return a card array if it is a two pair else it returns null
     */
    public Card[] getTwoPair(ArrayList<Card> cards) {
        Card[] first_pair = getPair(cards);
        if (first_pair != null) {
            for (Card card : first_pair) {
                cards.removeIf(c -> c == card);
            }
            Card[] second_pair = getPair(cards);
            if (second_pair != null) {
                Card[] two_pair = ArrayUtils.addAll(first_pair, second_pair);
                return two_pair;
            }
        }
        return null;
    }

    /**
     * This method checks if the cards represents a pair.
     * This means their have to be two cards with the same value.
     * @param cards the cards which have to be checked if they represents a pair
     * @return a card array if it is a pair else it returns null
     * @see EvaluateHandCards#sameAmountOfValues(ArrayList, int)
     */
    public Card[] getPair(ArrayList<Card> cards) {
        return sameAmountOfValues(cards, 2);
    }


    /**
     * High Card means only the highest hand card of the player.
     * @param user needed to get his hand cards
     * @return the highest card of the player
     */
    public Card[] getHighCard(User user) {
        Card[] cards = user.getHandCards();
        if (cards[0].getValue() >= cards[1].getValue()) {
            return new Card[]{cards[0]};
        } else {
            return new Card[]{cards[1]};
        }
    }

    /**
     * checks the list if there is the given symbol and value
     * @param list a card list which should be checked up
     * @param symbol the symbol which should be checked if it is in the list
     * @param value the value which should be checked if it is in the list
     * @return true when symbol and value are in the list else returns false
     * @see java.util.stream
     */
    private boolean containsCardByValueAndSymbol(List<Card> list, String symbol, int value) {
        return list.stream().filter(o -> o.getSymbol().equals(symbol) && o.getValue() == value).findFirst().isPresent();
    }

    /**
     * checks the list if there is the given value
     * @param list a card list which should be checked up
     * @param value the value which should be checked if it is in the list
     * @return true when value IS in the list else returns false
     * @see java.util.stream
     */
    private boolean containsCardByValue(List<Card> list, int value) {
        return list.stream().filter(o -> o.getValue() == value).findFirst().isPresent();
    }

    /**
     * checks the index of a value in the given list
     * @param list a card list which should be checked up
     * @param value the value which should be checked if it is in the list
     * @return index of the value in the list
     */
    private int getIndexByValue(List<Card> list, int value) {
        if (containsCardByValue(list, value)) {
            int i = 0;
            for (Card card : list) {
                if (card.getValue() == value) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    /**
     * Get a card array, where each card have the same value, from the list
     * @param list  a card list which should be checked up
     * @param value the value which should be checked if it is in the list
     * @return the card array where all cards got the same value
     * @see java.util.stream
     */
    private Card[] getCardsByValue(List<Card> list, int value) {
        return list.stream().filter(o -> o.getValue() == value).toArray(Card[]::new);
    }


    /**
     * Get a card array, where each card have the same symbol, from the list
     * @param list  a card list which should be checked up
     * @param symbol the symbol of a card which should be checked if it is in the list
     * @return the card array where all cards got the same symbol
     * @see java.util.stream
     */
    private Card[] getCardsBySymbol(List<Card> list, String symbol) {
        return list.stream().filter(o -> o.getSymbol() == symbol).toArray(Card[]::new);
    }

    /**
     * get a Card array, of a size equal to the parameter matches, from a card list where each card got the same value
     * @param cards a card list which should be checked up
     * @param matches needed amount of equal values in the list
     * @return a card array with the size of matches where each card got the same value
     */
    private Card[] sameAmountOfValues(ArrayList<Card> cards, int matches) {
        int steps = cards.size() - matches;
        for (int i = 0; i <= steps; i++) {
            Card card = cards.get(i);
            Card[] found_cards = getCardsByValue(cards, card.getValue());
            if (found_cards.length == matches) {
                return found_cards;
            }
        }
        return null;
    }


}
