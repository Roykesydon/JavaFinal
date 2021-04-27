<<<<<<< HEAD
package sample;
=======
package WeAreFamily;
>>>>>>> db3b2de85de5e6f299b4d16b3d52ee85650804ce

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< HEAD
//import com.sun.mail.smtp.SMTPTransport;
//import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.Properties;
import java.io.IOException;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

public class forgotPasswordController {

//    public void sendMail() {
//        String to = "toby3924830@gmail.com";//change accordingly
//        // Sender's email ID needs to be mentioned
//        String from = "toby3924830@gmail.com";//change accordingly
//        final String username = "toby3924830@gmail.com";//change accordingly
//        final String password = "ekrndkoymfzuvous";//change accordingly
//        // Assuming you are sending email through relay.jangosmtp.net
//        String host = "smtp.gmail.com";
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.port", "587");
//
//        // Get the Session object.
//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(username, password);
//                    }
//                });
//        session.setDebug(true);
//        try {
//            // Create a default MimeMessage object.
//            Message message = new MimeMessage(session);
//
//            // Set From: header field of the header.
//            message.setFrom(new InternetAddress(from));
//
//            // Set To: header field of the header.
//            message.setRecipients(Message.RecipientType.TO,
//                    InternetAddress.parse(to));
//
//            // Set Subject: header field
//            message.setSubject("Testing Subject");
//
//            // Now set the actual message
//            message.setText("Hello, this is sample for to check send "
//                    + "email using JavaMailAPI ");
//
//            // Send message
//            Transport.send(message);
//
//            System.out.println("Sent message successfully....");
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void switchToResetPassWord(ActionEvent actionEvent) throws IOException {
//        sendMail();
=======
import com.sun.mail.smtp.SMTPTransport;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.Properties;
import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class forgotPasswordController {

    public void sendMail() {
        String to = "toby3924830@gmail.com";//change accordingly
        // Sender's email ID needs to be mentioned
        String from = "toby3924830@gmail.com";//change accordingly
        final String username = "toby3924830@gmail.com";//change accordingly
        final String password = "ekrndkoymfzuvous";//change accordingly
        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        session.setDebug(true);
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Now set the actual message
            message.setText("Hello, this is sample for to check send "
                    + "email using JavaMailAPI ");

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public void switchToResetPassWord(ActionEvent actionEvent) throws IOException {
        sendMail();
>>>>>>> db3b2de85de5e6f299b4d16b3d52ee85650804ce
        Parent page =FXMLLoader.load(this.getClass().getResource("fxml/resetPassWord.fxml"));
        Scene tmp = new Scene(page);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(tmp);
        stage.show();
    }

}