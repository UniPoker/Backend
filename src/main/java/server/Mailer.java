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

    private Mailer() {
    }

    public static Mailer getInstance() {
        return MAILER_INSTANCE;
    }

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
