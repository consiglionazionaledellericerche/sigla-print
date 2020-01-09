/*
 * Copyright (C) 2020  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.si.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
                Optional.of(fileName).filter(t -> t.endsWith(PDF)).map(t -> APPLICATION_PDF).orElse(APPLICATION_XLS));
        InternetHeaders internetHeaders = new InternetHeaders();
        internetHeaders.addHeader("Content-Description", TEST_HTML);
        internetHeaders.addHeader(CONTENT_TYPE, TEXT_HTML);
        multipart.addBodyPart(new javax.mail.internet.MimeBodyPart(internetHeaders, body.getBytes(StandardCharsets.ISO_8859_1)));

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
