package tests;

import junit.framework.TestCase;
import org.junit.Before;
import server.DatabaseConnection;

import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 * Created by loster on 21.07.2015.
 */
public class DatabaseConnectionTest extends TestCase {

    DatabaseConnection con;

    @Before
    public void setUp() throws Exception {
        con = DatabaseConnection.getInstance();
    }

    public void testGetInstance() throws Exception {
        DatabaseConnection con1 = DatabaseConnection.getInstance();
        assertEquals(con1, con);
    }

    public void testInsertUser() throws Exception {
        int count = con.countUsers();
        int i = con.insertUser("test","test");
        System.out.println(i);
        System.out.println(con.countUsers());
        assertEquals(con.countUsers(), ++count);
    }

    public void testGetUserById() throws Exception {
        ResultSet rs = con.getUserById(2);
        assertTrue(rs.next());
    }

    public void testCloseConnection() throws Exception {
    }
}