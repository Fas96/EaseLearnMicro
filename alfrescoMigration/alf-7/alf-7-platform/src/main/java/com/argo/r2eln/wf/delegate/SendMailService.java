package com.argo.r2eln.wf.delegate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendMailService {

	private static Log logger = LogFactory.getLog(SendMailService.class);
	
	private static String authUser;
	
	private static String authPassword;
	
	public void setAuthUser(String user) {
		SendMailService.authUser = user;
	}
	
	public void setAuthPassword(String password) {
		SendMailService.authPassword = password;
	}
	
	public void sendMail(String toMail, String subject, String contents, String toUser_id, String toUser_nm, 
			 String fromMail, String fromUser_nm) {
		Session session = getSession();
		Date today = new Date();
		
		try {
			MimeMessage msg = new MimeMessage(session);
			
			msg.addHeader("Content-type", "text/HTML charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
			msg.setFrom(new InternetAddress(fromMail , fromUser_nm)); 
			
			msg.setSubject(subject, "UTF-8");
			msg.setText(contents, "UTF-8");
			msg.setSentDate(today);
			
			Transport.send(msg);
			
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(getErrorMessage(e));
		} catch (MessagingException e) {
			throw new RuntimeException(getErrorMessage(e));
		}
	}

	private Session getSession() {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.auth", "true");
		
		Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(authUser, authPassword);
			}
		};
		
		return Session.getInstance(props, auth);
	}
	
	public static String getErrorMessage(Exception e) {
    	StringWriter writer = new StringWriter();
    	PrintWriter pWriter = new PrintWriter(writer);
    	e.printStackTrace(pWriter);
    	
    	return writer.toString();
	}
}
