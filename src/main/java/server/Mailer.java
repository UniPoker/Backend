package server;

import com.sun.mail.smtp.SMTPTransport;

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
    private String host = "mail.gmx.net";
    private int port = 587;
    private String from = "uni-poker@gmx.de";
    private String password = "test123456";

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
    public boolean sendRegistrationMail(String to, String subject, String name, String user_password) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtps.host",host);
        props.put("mail.smtps.auth", "true");
        Session session = Session.getInstance(props, null);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject(subject);
            String mail_content = MAILER_INSTANCE.readFile(subject);
            mail_content = mail_content.replaceAll("#user#", name);
            mail_content = mail_content.replaceAll("#password#", user_password);
            message.setContent(mail_content, "text/html; charset=utf-8");
            message.setSentDate(new Date());
            SMTPTransport t =
                    (SMTPTransport)session.getTransport("smtps");
            t.connect(host, from, password);
            t.sendMessage(message, message.getAllRecipients());
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }


}
