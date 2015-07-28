package server;

/**
 * Created by loster on 20.05.2015.
 */
public class Room {

    private final int MAX_PLAYER = 8;
    private Game game;
    private UserList all_users;


    private int id;

    /**
     * @param user
     * @param id
     */
    public Room(User user, int id) {
        all_users = new UserList();
        this.id = id;
        game = new Game(all_users);
        joinRoom(user);
    }

    /**
     * @param user
     * @return boolean when connect
     */
    public boolean joinRoom(User user) {
        if ((all_users.length < MAX_PLAYER) && !(all_users.contains(user))) {
            all_users.add(user);
            user.setRoomId(this.id);
            game.joinGame(user);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(User user) {
        user.setRoomId(-1);
        all_users.removeUser(user);
        game.leaveGame(user);
        return true;
    }

    public int userSize() {
        return all_users.length;
    }

    public int getId() {
        return id;
    }

    public UserList getAllUsers() {
        return all_users;
    }

    public Game getGame(){
        return game;
    }


}
