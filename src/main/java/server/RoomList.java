package server;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by loster on 20.07.2015.
 */
public class RoomList {

    private List<Room> rooms;
    public int length = 0;

    /**
     * creates an empty server.UserList
     */
    public RoomList() {
        rooms = new ArrayList<Room>();
    }

    public RoomList(Room room) {
        rooms = new ArrayList<>();
        rooms.add(room);
        length++;
    }

    public RoomList(Room[] rooms) {
        this.rooms = new ArrayList<Room>(Arrays.asList(rooms));
        length += rooms.length;
    }

    public void add(Room room) {
        rooms.add(room);
        length ++;
    }

    public void removeRoom(Room room) {
        rooms.removeIf(p -> p == room);
    }

    public void clearAll() {
        rooms.removeAll(rooms);
        length = 0;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRoomByIndex(int index){
        return rooms.get(index);
    }

    public Room getRoomByUser(User user){
        for(Room room : rooms){
            for(User current_user: room.getAllUsers().getUsers()){
                if(current_user.equals(user)){
                    return room;
                }
            }
        }
        return null;
    }

    public Room getRoomBySession(Session session){
        for(Room room : rooms){
            for(User current_user: room.getAllUsers().getUsers()){
                if(current_user.getWebsession().equals(session)){
                    return room;
                }
            }
        }
        return null;
    }
}