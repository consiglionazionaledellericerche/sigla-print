package it.cnr.si.service;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);	
	@Autowired
	private JavaMailSender mailSender;	

	@Value("${spring.mail.from}")
	private String from;
	
	public void send(String subject, String body, String to, String cc, String bcc, File output, String fileName) throws MessagingException, IOException {
		LOGGER.info("Try to send email to {} with attach {}", to, fileName);
	
    	MimeMessage message = mailSender.createMimeMessage();
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(body);
		
		MimeMultipart multipart = new javax.mail.internet.MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();
	    messageBodyPart.attachFile(output);
	    messageBodyPart.setFileName(fileName);
	    messageBodyPart.setHeader("Content-Type","application/pdf");
	    InternetHeaders internetHeaders = new InternetHeaders();
	    internetHeaders.addHeader("Content-Description","test.html");
		internetHeaders.addHeader("Content-Type","text/html");
		multipart.addBodyPart(new javax.mail.internet.MimeBodyPart(internetHeaders, body.getBytes("ISO-8859-1")));
		
	    multipart.addBodyPart(messageBodyPart);
	    message.setContent(multipart);
	    message.setHeader("To", to);
	    if (cc != null)
	    	message.setHeader("Cc", cc);
	    if (bcc != null)
	    	message.setHeader("Bcc", bcc);
		mailSender.send(message);
	}
}
