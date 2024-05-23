package br.com.ero.sendemailhtml.service;

import br.com.ero.sendemailhtml.utils.EmailUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

import static br.com.ero.sendemailhtml.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private static final String HOST = "http://localhost:8080";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailtemplate";
    public static final String TEXT_HTML_ENCODING = "text/html";
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendSimpleEmailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom("${MAIL_USERNAME}");
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name, HOST, token));
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom("${MAIL_USERNAME}");
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, HOST, token));
            // Add Attachments
            FileSystemResource leopard = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/leopard.jpg"));
            FileSystemResource adventure = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/adventure.jpg"));
            FileSystemResource wordDocument = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/Word.docx"));
            helper.addAttachment(leopard.getFilename(), leopard);
            helper.addAttachment(adventure.getFilename(), adventure);
            helper.addAttachment(wordDocument.getFilename(), wordDocument);
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }


    @Override
    @Async
    public void sendMimeMessagesWithEmbeddedImages(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom("${MAIL_USERNAME}");
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, HOST, token));
            // Add Attachments
            FileSystemResource leopard = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/leopard.jpg"));
            FileSystemResource adventure = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/adventure.jpg"));
            FileSystemResource wordDocument = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/Word.docx"));
            helper.addInline(getContentId(leopard.getFilename()), leopard);
            helper.addInline(getContentId(adventure.getFilename()), adventure);
            helper.addInline(getContentId(wordDocument.getFilename()), wordDocument);
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }


    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(HOST, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom("${MAIL_USERNAME}");
            helper.setTo(to);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom("${MAIL_USERNAME}");
            helper.setTo(to);
//            helper.setText(text, true);
            Context context = new Context();
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(HOST, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            // Add HTML to body
            MimeMultipart multipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            multipart.addBodyPart(messageBodyPart);

            // Add images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Downloads/images/adventure.jpg");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            multipart.addBodyPart(imageBodyPart);

            message.setContent(multipart);

            javaMailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}
