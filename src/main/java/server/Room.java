package server;

/**
 * Created by loster on 20.05.2015.
 */
public class Room {

    private final int MAX_PLAYER = 8;

    private UserList all_users;


    private int id;

    public Room(User user, int id){
        all_users = new UserList(user);
        this.id = id;
    }

    public boolean joinRoom(User user){
        if(all_users.length < MAX_PLAYER){
            all_users.add(user);
            user.setRoomIndex(this.id);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(User user){
        all_users.removeUser(user);
        return true;
    }




}
