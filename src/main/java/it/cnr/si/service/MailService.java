package it.cnr.si.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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
	private static final String TEXT_HTML = "text/html";
	private static final String PDF = "pdf";
	private static final String TEST_HTML = "test.html";
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_XLS = "application/xls";
	private static final String APPLICATION_PDF = "application/pdf";
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
	    messageBodyPart.setHeader(CONTENT_TYPE,
	    		Optional.of(fileName).filter(t -> t.endsWith(PDF)).map( t -> APPLICATION_PDF).orElse(APPLICATION_XLS));
	    InternetHeaders internetHeaders = new InternetHeaders();
	    internetHeaders.addHeader("Content-Description",TEST_HTML);
		internetHeaders.addHeader(CONTENT_TYPE,TEXT_HTML);
		multipart.addBodyPart(new javax.mail.internet.MimeBodyPart(internetHeaders, body.getBytes(ISO_8859_1)));
		
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
