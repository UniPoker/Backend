package tests;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import server.*;

import static org.junit.Assert.*;

/**
 * Created by loster on 23.07.2015.
 */
public class PokerInterfaceTest {
    PokerInterface poker;

    @Before
    public void setUp() throws Exception {
        poker = new PokerInterface();
    }

    @Test
    public void testLogin() throws Exception {
        JSONObject response =  poker.receive("login_user", new JSONObject().put("user", "mustermann").put("password", "123456"), new User(), new UserList(), new RoomList());
        System.out.println(response);
    }
}