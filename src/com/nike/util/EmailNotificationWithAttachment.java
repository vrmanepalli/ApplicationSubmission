/*
 * This utility class is responsible for sending the emails
 * This class is used by all the web services that need to send the email to users
 * This class is extended from a thread in order to make this not a load on a web service that is this can be an asynchronous call.
 * run() will take care of sending the information as email
 * Constructor is used to pass the information to create an email.
 * 
 * */
package com.nike.util;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailNotificationWithAttachment extends Thread {
	
	static Logger logger =  LogManager.getLogger(EmailNotificationWithAttachment.class);

	private String to;
	private String subject;
	private String emailContent;
	private String iconURL;
	private String fileName;

	public EmailNotificationWithAttachment(String to, String subject, String emailContent, String iconURL, String file)
			throws Exception {
		this.to = to;
		this.subject = subject;
		this.emailContent = emailContent;
		this.iconURL = iconURL;
		this.fileName = file;
	}

	@Override
	public void run() {

		final String smtpServer = "mailhost.nike.com";
		final String from = "mobileapps@nike.com";

		Properties properties = System.getProperties();

		// populate the 'Properties' object with the mail
		// server address, so that the default 'Session'
		// instance can use it.
		properties.put("mail.smtp.host", smtpServer);

		Session session = Session.getDefaultInstance(properties);

		Message mailMsg = new MimeMessage(session);// a new email
													// message

		InternetAddress[] addresses = null;

		try {

			if (to != null) {

				// throws 'AddressException' if the 'to' email address
				// violates RFC822 syntax
				addresses = InternetAddress.parse(to, false);

				(mailMsg)
						.setRecipients(MimeMessage.RecipientType.TO, addresses);

			} else {
				logger.error("The mail message requires a 'To' address. " + to);

			}

			if (from != null) {

				(mailMsg).setFrom(new InternetAddress(from));

			} else {
				logger.error("The mail message requires a valid 'From' address. " + from);
			}

			// Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();
	      // Create a multipar message
	         Multipart multipart = new MimeMultipart();
			if (emailContent != null) {
				 // Now set the actual message
		         messageBodyPart.setText(emailContent);
		      // Set text message part
		         multipart.addBodyPart(messageBodyPart);
			}
			
			if(fileName != null) {
				 // Part two is attachment
		         messageBodyPart = new MimeBodyPart();
		         DataSource source = new FileDataSource(fileName);
		         messageBodyPart.setDataHandler(new DataHandler(source));
		         messageBodyPart.setFileName(fileName);
		         multipart.addBodyPart(messageBodyPart);

			}
			// Send the complete message parts
			(mailMsg).setContent(multipart);
			(mailMsg).setSubject(subject);

//			if (emailContent != null) {
//				
//			}
//				(mailMsg).setContent(generateCustomHTMLContent(emailContent, iconURL), "text/html; charset=utf-8");

			// Finally, send the meail message; throws a 'SendFailedException'
			// if any of the message's recipients have an invalid adress
			Transport.send(mailMsg);
		} catch (Exception exc) {
			logger.error("Exception in EmailNotification: " + ErrorUtils.getStackTrace(exc));
		}
	}

	private static String generateHTMLContent(String content, String iconURL) {
		return "<div> "
				+ "<div style='padding: 15px;'><img src='" + iconURL + "' style='height: 75px; width: 65px;' /> </div>"
				+ "<hr size='5'>"
				+ "<p>Hello, </p>"
				+ "<p>"
				+ content
				+ "</p>"
				+ "<p style='font-size: 11px; padding-top: 25px;'> Please do not reply to this automatically-generated email. If you have any questions, please visit our <a href='https://mobileapps.nike.com:8443/ApplicationSubmission/#/ContactUs'> support page </a></p>"
				+ "</div>";
	}
	
	private static String generateCustomHTMLContent(String content, String iconURL) {
		return "<div> "
				+ "<div style='padding: 15px;'><img src='" + iconURL + "' style='height: 75px; width: 65px;' /> </div>"
				+ "<hr size='5'>"
				+ "<p>"
				+ content
				+ "</p>"
				+ "</div>";
	}
}
