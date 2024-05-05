package com.mrwantesting;

import com.mrwantesting.dao.DBConnection;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SendMessage {
    public static void sendMessageToEmail(String email) {
        final String username = "relaxmy00@gmail.com";
        final String password = "rhlowhmzlvtqtagm";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("relaxmy00@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your Password");

            String foundPassword = getPasswordFromDatabase(email); // Retrieve password from database
            if (foundPassword != null) {
                message.setText("Hello, your password is: " + foundPassword);
                Transport.send(message);
                System.out.println("Email sent successfully!");
            } else {
                System.out.println("Email not found in database.");
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPasswordFromDatabase(String email) {
        String password = null;
        Connection connection = DBConnection.getConnection(); // Obtain connection using DBConnection class
        try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM logup WHERE email = ?")) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return password;
    }

    public static void main(String[] args) {
        SendMessage sender = new SendMessage();
        sender.sendMessageToEmail("devopscode9@gmail.com"); // Test send email
    }
}
