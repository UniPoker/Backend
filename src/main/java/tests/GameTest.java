package tests;

import game.EvaluateHandCards;
import org.junit.Before;
import org.junit.Test;
import cards.Card;
import game.Game;
import users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by loster on 01.08.2015.
 */
public class GameTest {
    EvaluateHandCards game;
    @Before
    public void setup(){

        game = new EvaluateHandCards();
    }

    @Test
    public void testHasRoyalFlush() throws Exception {
        Card card_1 = new Card(14,"diamonds");
        Card card_2 = new Card(13,"diamonds");
        Card card_3 = new Card(12,"diamonds");
        Card card_4 = new Card(11,"diamonds");
        Card card_5 = new Card(10,"diamonds");
        Card card_6 = new Card(9,"hearts");
        Card card_7 = new Card(8,"spades");
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
        Card[] was_successfull = game.getRoyalFlush(cards);
        assertNotNull(was_successfull);
        assertEquals(5,was_successfull.length);
    }

    @Test
    public void testHasRoyalFlush_2() throws Exception {
        Card card_1 = new Card(14,"hearts");
        Card card_2 = new Card(14,"diamonds");
        Card card_3 = new Card(13,"diamonds");
        Card card_4 = new Card(12,"diamonds");
        Card card_5 = new Card(11,"diamonds");
        Card card_6 = new Card(10,"diamonds");
        Card card_7 = new Card(9,"diamonds");
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
        Card[] was_successfull = game.getRoyalFlush(cards);
        assertNotNull(was_successfull);
        assertEquals(5, was_successfull.length);
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
        Card[] was_successfull = game.getRoyalFlush(cards);
        assertNull(was_successfull);
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
        Card[] was_successfull = game.getRoyalFlush(cards);
        assertNull(was_successfull);
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
        Card[] was_successfull = game.getStraightFlush(cards);
        assertNotNull(was_successfull);
        assertEquals(5, was_successfull.length);
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
        Card[] was_successfull = game.getStraightFlush(cards);
        assertNull(was_successfull);
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
        Card[] was_successfull = game.getStraightFlush(cards);
        assertNull(was_successfull);
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
        Card[] was_successfull = game.getHighCard(user);
        assertNotNull(was_successfull);
        assertEquals(was_successfull[0], card_2);
    }

    @Test
    public void testHasStraight(){
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
        Card[] was_successfull = game.getStraight(cards);
        assertNotNull(was_successfull);
        assertEquals(5,was_successfull.length);
    }

    @Test
    public void testHasStraight_1(){
        Card card_1 = new Card(14,"diamonds");
        Card card_2 = new Card(12,"hearts");
        Card card_3 = new Card(13,"hearts");
        Card card_4 = new Card(10,"spades");
        Card card_5 = new Card(9,"spades");
        Card card_6 = new Card(3,"diamonds");
        Card card_7 = new Card(2,"diamonds");
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
        Card[] was_successfull = game.getStraight(cards);
        assertNotNull(was_successfull);
        assertEquals(5,was_successfull.length);
    }

    @Test
    public void failHasStraight(){
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
        Card[] was_successfull = game.getStraight(cards);
        assertNull(was_successfull);
    }

    @Test
    public void testHasQuads(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(9,"hearts");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(3,"clubs");
        Card card_5 = new Card(3,"spades");
        Card card_6 = new Card(3,"diamonds");
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
        Card[] was_successfull = game.getQuads(cards);
        assertNotNull(was_successfull);
        assertEquals(4, was_successfull.length);
    }

    @Test
    public void failHasQuads(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"hearts");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(9,"clubs");
        Card card_5 = new Card(3,"spades");
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
        Card[] was_successfull = game.getQuads(cards);
        assertNull(was_successfull);
    }

    @Test
    public void testHasQuads_1(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"hearts");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"clubs");
        Card card_5 = new Card(9,"clubs");
        Card card_6 = new Card(6,"diamonds");
        Card card_7 = new Card(9,"hearts");
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
        Card[] was_successfull = game.getQuads(cards);
        assertNotNull(was_successfull);
    }

    @Test
    public void testHasFlush(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"diamonds");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"diamonds");
        Card card_5 = new Card(9,"clubs");
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
        Card[] was_successfull = game.getFlush(cards);
        assertNotNull(was_successfull);
        assertEquals(5, was_successfull.length);
    }

    @Test
    public void failHasFlush(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"diamonds");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"spades");
        Card card_5 = new Card(9,"clubs");
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
        Card[] was_successfull = game.getFlush(cards);
        assertNull(was_successfull);
    }

    @Test
    public void testHasTrips(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"diamonds");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"spades");
        Card card_5 = new Card(9,"clubs");
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
        Card[] was_successfull = game.getTrips(cards);
        assertNotNull(was_successfull);
        assertEquals(3, was_successfull.length);
    }

    @Test
    public void failHasTrips(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"diamonds");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"spades");
        Card card_5 = new Card(14,"clubs");
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
        Card[] was_successfull = game.getTrips(cards);
        assertNull(was_successfull);
    }

    @Test
    public void testHasPair(){
        Card card_1 = new Card(14,"spades");
        Card card_2 = new Card(8,"hearts");
        Card card_3 = new Card(14,"clubs");
        Card card_4 = new Card(3,"clubs");
        Card card_5 = new Card(9,"diamonds");
        Card card_6 = new Card(12,"hearts");
        Card card_7 = new Card(5,"hearts");
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
        Card[] was_successfull = game.getPair(cards);
        assertNotNull(was_successfull);
        assertEquals(2, was_successfull.length);
    }

    @Test
    public void failHasPair(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(3,"diamonds");
        Card card_3 = new Card(8,"spades");
        Card card_4 = new Card(4,"spades");
        Card card_5 = new Card(14,"clubs");
        Card card_6 = new Card(6,"diamonds");
        Card card_7 = new Card(13,"diamonds");
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
        Card[] was_successfull = game.getPair(cards);
        assertNull(was_successfull);
    }

    @Test
    public void testHasFullHouse(){
        Card card_1 = new Card(9,"diamonds");
        Card card_2 = new Card(9,"hearts");
        Card card_3 = new Card(9,"spades");
        Card card_4 = new Card(4,"spades");
        Card card_5 = new Card(4,"clubs");
        Card card_6 = new Card(6,"diamonds");
        Card card_7 = new Card(13,"diamonds");
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
        Card[] was_successfull = game.getFullHouse(cards);
        assertNotNull(was_successfull);
        assertEquals(5, was_successfull.length);
    }
}