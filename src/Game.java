/**
 * Created by loster on 26.05.2015.
 */
public class Game {

    private CardStack card_stack;
    private Card[] board;

    private UserList players;
    private User small_blind;
    private User big_blind;

    private int pod;

    public Game(UserList players){
        this.players = players;
        startRound();
    }

    public void joinGame(User player){
        players.add(player);
    }

    public void leaveGame(User player){
        players.removeUser(player);
    }

    public void startRound(){
        if(players.length() > 2){
            pod = 0;
            card_stack = new CardStack();
            deal_cards();
        }
    }

    private void deal_cards(){
        for(int i=0; i < 2; i ++) {
            for (User player : players.getUsers()) {
                player.setHandCards(card_stack.pop());
            }
        }
    }


}
