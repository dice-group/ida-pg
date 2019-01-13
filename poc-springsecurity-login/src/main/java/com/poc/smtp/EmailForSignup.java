package com.poc.smtp;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.*;

public class EmailForSignup {

/*	
	//Code for testing the simple message sent from localhost
	public static void main(String [] args) {
		String to = "itsayazmaqbool@gmail.com";
		String from = "managerdice@gmail.com";
		String host = "localhost";

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    message.setSubject("This is the Subject Line!");
		    message.setText("This is actual message");
		    Transport.send(message);
		    System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	
*/	
	//Complete Code using Transport protocol for SMTP Server
    static final String FROM = "ayazmaqbooluet@gmail.com";
    static final String FROMNAME = "DICE Manager";

    static final String TO = "itsayazmaqbool@gmail.com";
    
    //TODO: need to setup the smtp server on uni paderborn server
    static final String SMTP_USERNAME = "smtp_username";
    static final String SMTP_PASSWORD = "smtp_password";
    
    static final String CONFIGSET = "ConfigSet";
    
    static final String HOST = "localhost"; //"email-smtp.uni-paderborn.com";
    

    static final int PORT = 587;
    
    static final String SUBJECT = "DICE Signup Email";
    
    static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>DICE SIGNUP CONFIRMATION</h1>",
    	    "<p>This email was sent for the confimation of the signup</p>"
    	);

    public static void main(String[] args) throws Exception {

        // Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", PORT); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
    	SmtpAuthenticator authenticator = new SmtpAuthenticator();
    	javax.mail.Message msg = new MimeMessage(Session
    	                    .getInstance(props, authenticator));
    	
        //MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM,FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        msg.setSubject(SUBJECT);
        msg.setContent(BODY,"text/html");
        
        // Add a configuration set header. Comment or delete the 
        // next line if you are not using a configuration set
        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
            
        // Create a transport.
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            System.out.println("Sending...");
            
            // Connect to Uni Paderborn Server using the SMTP username and password you specified above.
            //transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            Transport.send(msg);
            //transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
        //catch (Exception ex) {
        //    System.out.println("The email was not sent.");
        //    System.out.println("Error message: " + ex.getMessage());
        //}
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }
   
}