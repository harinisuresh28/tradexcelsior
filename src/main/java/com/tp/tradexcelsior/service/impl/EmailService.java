package com.tp.tradexcelsior.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService{

  @Autowired
  private JavaMailSender javaMailSender;

  @Async
  public CompletableFuture<Boolean> sendEmail(String email) {
    try {
      // Create a MimeMessage object for sending HTML email
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

      // Set the necessary details for the email
      helper.setFrom("postmaster@sandbox98456a0115fc4e71877ae8a43fdbc877.mailgun.org"); // Your Mailgun sender
      helper.setTo(email);
      helper.setSubject("Activate Trade Excelsior Account");

      // HTML content for the email body
      String emailContent = "<html><body>"
          + "<h2>Welcome to Trade Excelsior</h2>"
          + "<p>Dear User,</p>"
          + "<p>Thank you for signing up with Trade Excelsior. To activate your account, please click the link below:</p>"
          + "<p><a href=\"https://your-activation-url.com/activate?email=" + email + "\">Activate Your Account</a></p>"
          + "<p>If you did not request this, please ignore this email.</p>"
          + "<p>Best regards,</p>"
          + "<p>The Trade Excelsior Team</p>"
          + "</body></html>";

      // Set the email content as HTML
      helper.setText(emailContent, true);

      // Send the email
      javaMailSender.send(mimeMessage);

      // Return a completed future indicating email was sent
      return CompletableFuture.completedFuture(true);
    } catch (MessagingException e) {
      // Handle error if email fails to send
      e.printStackTrace();
      return CompletableFuture.completedFuture(false); // Failed to send email
    }
  }
}
