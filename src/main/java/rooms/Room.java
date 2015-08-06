package rooms;

import game.Game;
import users.User;
import users.UserList;

/**
 * A room is a logical representation a game room.
 * So one room contains a game and a userlist with all users in this room.
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 */
public class Room {

    private final int MAX_PLAYER = 8;
    private Game game;
    private UserList all_users;

    private int id;

    /**
     * @param user which will be directly in the userlist
     * @param id
     */
    public Room(User user, int id) {
        all_users = new UserList();
        this.id = id;
        game = new Game(all_users);
        joinRoom(user);
    }

    /**
     * add a user to the userlist of a room
     *
     * @param user to add
     * @return boolean when add successfull
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

    /**
     * removes a user from the user list.
     *
     * @param user to leave room
     * @return always true
     */
    public boolean leaveRoom(User user) {
        user.setRoomId(-1);
        all_users.removeUser(user);
        game.leaveGame(user);
        return true;
    }

    /**
     * @return the amount of users connected to the room
     */
    public int userSize() {
        return all_users.length;
    }

    /**
     * @return the id of the room.
     */
    public int getId() {
        return id;
    }

    /**
     * @return a userlist with all users who are in the room.
     */
    public UserList getAllUsers() {
        return all_users;
    }

    /**
     * @return the game instance which is in the room.
     */
    public Game getGame(){
        return game;
    }


}
