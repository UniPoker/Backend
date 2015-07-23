package tests;

import org.junit.Before;
import org.junit.Test;
import server.Mailer;

import static org.junit.Assert.*;

/**
 * Created by loster on 22.07.2015.
 */
public class MailerTest {

    Mailer mailer;
    @Before
    public void setup(){
        mailer = Mailer.getInstance();
    }

    @Test
    public void testGetInstance() throws Exception {
        assertEquals(mailer, Mailer.getInstance());
    }

    @Test
    public void testReadFile() throws Exception {
        String mail =  mailer.readFile("Registrierung");
        System.out.println(mail);
        assertNotNull(mail);
    }
}