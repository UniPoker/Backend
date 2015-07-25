package server;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Mailer {

    private static final Mailer MAILER_INSTANCE = new Mailer();
    private String host;
    private int port;
    private String from;
    private String password;

    /**
     * creates a new instance of the mailer
     */
    private Mailer() {
    }

    /**
     * returns the instance of the mailer so only one instance is active at the same time
     *
     * @return instance of Mailer
     */
    public static Mailer getInstance() {
        return MAILER_INSTANCE;
    }

    /**
     * reads the HTML Template of the given name and creates a string of it
     *
     * @param fileName the given filename (e.g. "Registrierung")
     * @return returns a string
     */
    public String readFile(String fileName) {
        String line;
        StringBuffer sb = new StringBuffer();
        try {
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
            BufferedReader br = new BufferedReader(new FileReader("MailTemplates/" + fileName + ".html"));
            while ((line = br.readLine()) != null) { // while loop begins here
                sb.append(line);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
        return sb.toString();
    }

    /**
     * sends the mail with the given subject to the given mail
     *
     * @param to      String of the mail to be sent to
     * @param subject the subject of the mail
     * @throws MessagingException if there are any errors
     */
    public void sendMail(String to, String subject) throws MessagingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject(subject);
            String mail_content = MAILER_INSTANCE.readFile(subject);
            message.setContent(mail_content, "text/html; charset=utf-8");
            message.setSentDate(new Date());
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}
