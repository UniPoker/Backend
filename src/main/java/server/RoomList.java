package server;

import org.json.JSONArray;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        length++;
    }

    /**
     * creates and returns a new room and let the given user join this room
     *
     * @param joining_user
     * @return Room
     */
    public Room addNewRoom(User joining_user) {
        Room room = new Room(joining_user, (this.length + 1));
        this.add(room);
        return room;
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

    public Room getRoomByIndex(int index) {
        return rooms.get(index);
    }

    public Room getRoomByUser(User user) {
        for (Room room : rooms) {
            for (User current_user : room.getAllUsers().getUsers()) {
                if (current_user.equals(user)) {
                    return room;
                }
            }
        }
        return null;
    }

    public Room getRoomBySession(Session session) {
        for (Room room : rooms) {
            for (User current_user : room.getAllUsers().getUsers()) {
                if (current_user.getWebsession().equals(session)) {
                    return room;
                }
            }
        }
        return null;
    }

    public Room getRoomByRoomId(int room_id) {
        for (Room room : rooms) {
            if (room.getId() == room_id) {
                return room;
            }
        }
        return null;
    }

    /**
     * returns a JSONArray with the room_id and the number of seats of every room
     *
     * @return JSONArray
     */
    public JSONArray getInterfaceRoomList() {
        JSONArray arr = new JSONArray();
        for (Room room : rooms) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("room_id", room.getId());
            map.put("room_seats", room.userSize());
            arr.put(map);
        }
        return arr;
    }
}
