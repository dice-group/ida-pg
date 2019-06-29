package upb.ida.smtp;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailForSignup {

	private static boolean emailSent = false;
	private static String ToUserName;
	
	private static final String FROM = "idapg@mail.uni-paderborn.de";
	private static final String FROMNAME = "DICE IDA TEAM";
	private static final String SMTP_USERNAME = "idapg";
	private static final String SMTP_PASSWORD = "diceIDA1-";
	private static final String CONFIGSET = "ConfigSet";
    
	private static final String HOST = "mail.uni-paderborn.de"; 
	private static final int PORT = 25;
    
	private static final String SUBJECT = "DICE Signup Email";
	private static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>WELCOME</h1>",
    	    "<p>Dear User,"
    	    + "<br><br>"
    	    + "Your new IDA user has been created. Welcome to the IDA Community!"
    	    + "<br>"
    	    + "From now on, please log in to your account using your email address and password."
    	    + "<br><br>"
    	    + "Thanks for Registering."
    	    + "<br>"
    	    + "DICE TEAM</p>"
    	);

    public static boolean sendEmail(String userName) throws Exception
    {
    	if(userName.isEmpty()) return emailSent;
    	ToUserName = userName;
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.host", HOST);
    	props.put("mail.smtp.port", PORT); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");

    	Session session = Session.getDefaultInstance(props);
    	
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM,FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(ToUserName));
        msg.setSubject(SUBJECT);
        msg.setContent(BODY,"text/html");
        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
            
        Transport transport = session.getTransport();        
        try
        {          
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            emailSent = true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
        finally
        {
            transport.close();
        }
        return emailSent;
    }
}