package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loster on 26.05.2015.
 */
public class Game {

    private CardStack card_stack;
    private Card[] board;

    private UserList players;
    private User small_blind;
    private User big_blind;

    private User current;
    private User first;

    private List<Integer> pod;
    private int blind_index = 0;

    private boolean last_turn = false;

    public Game(UserList players) {
        this.players = players;
        startRound();
    }

    public void joinGame(User player) {
        players.add(player);
    }

    public void leaveGame(User player) {
        players.removeUser(player);
    }

    public void startRound() {
        if (players.length() > 2) {
            initRound();
        }
    }

    public void doRaise(User player, int raise) {
        if (isCurrent(player)) {
            pod.add(getLastBet() + raise);
            first = player;
            player.setLastAction(Actions.RAISE);
            setNextUser(player);
        }
    }

    public void doBet(User player, int bet) {
        if (isCurrent(player)) {
            pod.add(bet);
            player.setLastAction(Actions.BET);
            setNextUser(player);
        }
    }

    public void doCall(User player) {
        if (isCurrent(player)) {
            int bet = getLastBet();
            pod.add(bet);
            player.setLastAction(Actions.CALL);
            setNextUser(player);
        }
    }

    public void doCheck(User player) {
        if (isCurrent(player)) {
            if(players.getUserByIndex(players.getUsers().indexOf(player) - 1).getLastAction() == Actions.CHECK || player.equals(first)){
                player.setLastAction(Actions.CHECK);
                setNextUser(player);
            }
        }
    }

    public void doFold(User player){
        if(isCurrent(player)){
            int i = 0;
            User possible_winner = null;
            player.setHasFolded(true);
            player.setLastAction(Actions.FOLD);
            for(User user: players.getUsers()){
                if(!user.hasFolded()){
                    i++;
                    possible_winner = user;
                }
            }
            if(i == 1){
                noPlayersLeft(possible_winner);
            }
        }
    }

    private void noPlayersLeft(User possible_winner) {
        possible_winner.setLimit(possible_winner.getLimit() + getPodValue());
        startRound();
    }

    private boolean isCurrent(User player) {
        return player.equals(current);
    }

    private int getLastBet() {
        return pod.get(pod.size() - 1);
    }

    private void setNextUser(User player) {
        User person = players.getUserByIndex(players.getUsers().indexOf(player) + 1);
        if(person.hasFolded()){
            setNextUser(person);
        }
        current = person;
        if (current.equals(first)) {
            if(last_turn){
                //Karten verwerten + Sieger ermitteln
            }else{
                dealBoardCards();
                first = players.getUserByIndex(players.getUsers().indexOf(big_blind) +1);
            }
        }
    }

    private void setBlindPlayers() {
        small_blind = players.getUserByIndex(blind_index);
        big_blind = players.getUserByIndex(++blind_index);
    }

    private void initRound() {
        pod = new ArrayList<>();
        pod.add(0);
        board = new Card[5];
        card_stack = new CardStack();
        dealCards();
        setBlindPlayers();
        current = players.getUserByIndex(blind_index + 1);
        first = current;
    }

    private void dealCards() {
        for (int i = 0; i < 2; i++) {
            for (User player : players.getUsers()) {
                player.setHandCards(card_stack.pop());
            }
        }
    }

    private void dealBoardCards(){
        card_stack.pop();//burn card;
        if(board[0].equals(null)){
            board[0] = card_stack.pop();
            board[1] = card_stack.pop();
            board[2] = card_stack.pop();
        }else if(board[3].equals(null)){
            board[3] = card_stack.pop();
        }else if(board[4].equals(null)){
            last_turn = true;
            board[4] = card_stack.pop();
        }
    }

    private int getPodValue(){
        int sum = pod.stream().mapToInt(Integer::intValue).sum();
        return sum;
    }
}
