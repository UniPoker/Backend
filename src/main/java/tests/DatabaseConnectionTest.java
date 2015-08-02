package tests;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 * Created by loster on 21.07.2015.
 */
public class DatabaseConnectionTest extends TestCase {

    TestDatabaseConnection con;

    @Before
    public void setUp() throws Exception {
        con = TestDatabaseConnection.getInstance();
    }

    @Test
    public void testGetInstance() throws Exception {
        TestDatabaseConnection con1 = TestDatabaseConnection.getInstance();
        assertEquals(con1, con);
    }

    @Test
    public void testInsertUser() throws Exception {
        int count = con.countUsers();
        int i = con.insertUser(new BigInteger(130, new SecureRandom()).toString(32),"test");
        System.out.println(i);
        System.out.println(con.countUsers());
        assertEquals(con.countUsers(), ++count);
    }

    @Test
    public void testGetUserById() throws Exception {
        ResultSet rs = con.getUserById(2);
        assertTrue(rs.next());
    }
}