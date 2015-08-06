package rooms;

import org.json.JSONArray;
import users.User;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *
 * @author Stefan Fuchs
 * @author Jan-Niklas Wortmann
 * @see org.json.JSONArray
 * @see java.util.ArrayList
 * @see java.util.Arrays
 * @see java.util.HashMap
 * @see java.util.List
 */
public class RoomList {

    private List<Room> rooms;
    public int length = 0;

    /**
     * creates an empty users.UserList
     */
    public RoomList() {
        rooms = new ArrayList<Room>();
    }

    public RoomList(Room room) {
        rooms = new ArrayList<>();
        rooms.add(room);
        length++;
    }

    /**
     * A constructor with a room array where all rooms get added to
     * the room list
     *
     * @param rooms to be added to the roomlist
     */
    public RoomList(Room[] rooms) {
        this.rooms = new ArrayList<Room>(Arrays.asList(rooms));
        length += rooms.length;
    }

    /**
     * This Method adds a single room to the roomlist and increase its length by 1
     *
     * @param room to be inserted in the roomlist
     */
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

    /**Removes a room from the roomlist and decrease
     * the length attribute by 1
     *
     * @param room to be removed from the roomlist
     */
    public void removeRoom(Room room) {
        rooms.removeIf(p -> p == room);
    }

    /**
     * This method removes all rooms from the roomlist
     * and sets the length attribute to 0.
     */
    public void clearAll() {
        rooms.removeAll(rooms);
        length = 0;
    }

    /**
     * @return all rooms from the roomlist
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * This method is used to get a room by its index in the roomlist
     *
     * @param index to be looked up in the list
     * @return a single room
     */
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

    /**
     * This method is used to get a room. It checks up the websession of all users in the room
     * to be equal to the given session parameter
     * @param session lookup parameter.
     * @return a single room with a user in it with the given session.
     */
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

    /**
     * Lookup up for a room in the roomlist by its room id.
     *
     * @param room_id lookup parameter
     * @return the room with the given parameter
     */
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
