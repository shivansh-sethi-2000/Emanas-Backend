package com.ehrc.utility;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtility {
	
	static Logger logger = LoggerFactory.getLogger(EmailUtility.class);
	
	public static void sendRegisterServiceEmail(String toEmail, String subject, String body) throws Exception
	{
		final String fromEmail =LoadConfig.getConfigValue("FROM_EMAIL_ID"); //requires valid gmail id
		final String password = LoadConfig.getConfigValue("FROM_EMAIL_PASS"); // correct password for gmail id
//			final String fromEmail = "portaltest014@gmail.com";
//			final String password = "portal@123";
		
		logger.info("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", LoadConfig.getConfigValue("SMTP_HOST")); //SMTP Host
		props.put("mail.smtp.port", LoadConfig.getConfigValue("SMTP_PORT")); //TLS Port
		props.put("mail.smtp.auth", LoadConfig.getConfigValue("SMTP_AUTH")); //enable authentication
		props.put("mail.smtp.starttls.enable", LoadConfig.getConfigValue("SMTP_STARTTLS_ENABLE")); //enable STARTTLS
		
		
//			props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
//			props.put("mail.smtp.port", "587"); //TLS Port
//			props.put("mail.smtp.auth", "true"); //enable authentication
//			props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
		
		
		
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);	
		
      MimeMessage msg = new MimeMessage(session);
      //set message headers
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");
      msg.setFrom(new InternetAddress(fromEmail, "KMHMS COVID BMR Portal Admin"));
      msg.setReplyTo(InternetAddress.parse(fromEmail, false));
      msg.setSubject(subject, "UTF-8");
      msg.setText(body, "UTF-8");
      msg.setSentDate(new Date());
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
      logger.info("Message is ready");
	  Transport.send(msg);
	  logger.info("EMail Sent Successfully!!");
	
}
//	public static void main(String[] args) {
//		
//		EmailUtility.sendRegisterServiceEmail("shivanshsethi2000@gmail.com", "Test mail", "Test Mail");
//	}

}
