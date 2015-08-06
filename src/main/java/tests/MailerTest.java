package tests;

import org.junit.Before;
import org.junit.Test;
import utils.Mailer;

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

    @Test
    public void testSendMail() throws Exception{
        boolean send_mail = mailer.sendRegistrationMail("jan-niklaswortmann@gmx.net","Registrierung", "Jan-Niklas", "123456");
        assertTrue(send_mail);
    }
}