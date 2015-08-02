package tests;

import org.junit.Before;
import org.junit.Test;
import server.Card;
import server.Game;
import server.User;
import server.UserList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by loster on 01.08.2015.
 */
public class GameTest {
    Game game;
    @Before
    public void setup(){
        game = new Game(new UserList());
    }

    @Test
    public void testHasRoyalFlush() throws Exception {
        Card card_1 = new Card(14,"diamonds");
        Card card_2 = new Card(13,"diamonds");
        Card card_3 = new Card(12,"diamonds");
        Card card_4 = new Card(11,"diamonds");
        Card card_5 = new Card(10,"diamonds");
        Card card_6 = new Card(9,"diamonds");
        Card card_7 = new Card(8,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_successfull = game.hasRoyalFlush(cards);
        assertTrue(was_successfull);
    }

    @Test
    public void failHasRoyalFlushWrongValues() throws Exception {
        Card card_1 = new Card(13,"diamonds");
        Card card_2 = new Card(12,"diamonds");
        Card card_3 = new Card(11,"diamonds");
        Card card_4 = new Card(11,"diamonds");
        Card card_5 = new Card(10,"diamonds");
        Card card_6 = new Card(9,"diamonds");
        Card card_7 = new Card(8,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasRoyalFlush(cards);
        assertFalse(was_not_successfull);
    }

    @Test
    public void failHasRoyalFlushWrongSymbols() throws Exception {
        Card card_1 = new Card(14,"diamonds");
        Card card_2 = new Card(13,"hearts");
        Card card_3 = new Card(12,"spades");
        Card card_4 = new Card(11,"diamonds");
        Card card_5 = new Card(10,"diamonds");
        Card card_6 = new Card(9,"diamonds");
        Card card_7 = new Card(8,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasRoyalFlush(cards);
        assertFalse(was_not_successfull);
    }

    @Test
    public void testHasStraightFlush() throws Exception {
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"diamonds");
        Card card_3 = new Card(7,"diamonds");
        Card card_4 = new Card(6,"diamonds");
        Card card_5 = new Card(5,"diamonds");
        Card card_6 = new Card(4,"diamonds");
        Card card_7 = new Card(3,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_successfull = game.hasStraightFlush(cards);
        assertTrue(was_successfull);
    }

    @Test
    public void failHasStraightFlushWrongValues() throws Exception {
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(12,"diamonds");
        Card card_3 = new Card(7,"diamonds");
        Card card_4 = new Card(4,"diamonds");
        Card card_5 = new Card(4,"diamonds");
        Card card_6 = new Card(4,"diamonds");
        Card card_7 = new Card(3,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasStraightFlush(cards);
        assertFalse(was_not_successfull);
    }

    @Test
    public void failHasStraightFlushWrongSymbols() throws Exception {
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(7,"hearts");
        Card card_4 = new Card(6,"spades");
        Card card_5 = new Card(5,"spades");
        Card card_6 = new Card(4,"diamonds");
        Card card_7 = new Card(3,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasStraightFlush(cards);
        assertFalse(was_not_successfull);
    }

    @Test
    public void testHasHighCard() throws Exception {
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(7,"hearts");
        Card card_4 = new Card(6,"spades");
        Card card_5 = new Card(14,"spades");
        Card card_6 = new Card(4,"diamonds");
        Card card_7 = new Card(3,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_successfull = game.hasHighCard(cards);
        assertTrue(was_successfull);
    }

    @Test
    public void failHasHighCard() throws Exception {
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(7,"hearts");
        Card card_4 = new Card(6,"spades");
        Card card_5 = new Card(13,"spades");
        Card card_6 = new Card(4,"diamonds");
        Card card_7 = new Card(3,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasHighCard(cards);
        assertFalse(was_not_successfull);
    }

    @Test
    public void testHasStright(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(11,"hearts");
        Card card_4 = new Card(7,"spades");
        Card card_5 = new Card(9,"spades");
        Card card_6 = new Card(6,"diamonds");
        Card card_7 = new Card(5,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_successfull = game.hasStraight(cards);
        assertTrue(was_successfull);
    }

    @Test
    public void failHasStright(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(11,"hearts");
        Card card_4 = new Card(7,"spades");
        Card card_5 = new Card(9,"spades");
        Card card_6 = new Card(6,"diamonds");
        Card card_7 = new Card(14,"diamonds");
        Card [] board = new Card[5];
        board[0] = card_1;
        board[1] = card_6;
        board[2] = card_3;
        board[3] = card_7;
        board[4] = card_5;
        User user = new User();
        user.setHandCards(card_2);
        user.setHandCards(card_4);
        Card [] _cards = Stream.concat(Arrays.stream(board), Arrays.stream(user.getHandCards())).toArray(Card[]::new);
        ArrayList<Card> cards = new ArrayList<Card>(Arrays.asList(_cards));
        Collections.sort(cards);
        Collections.reverse(cards); //damit absteigend:D
        boolean was_not_successfull = game.hasStraight(cards);
        assertFalse(was_not_successfull);
    }
}