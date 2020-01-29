package com.mes.gotogether.mail;

import com.mes.gotogether.domains.User;
import com.mes.gotogether.domains.responses.HttpResponse;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Mono;

public class GmailEmailServiceImpl {
   
    private final JavaMailSender javaMailSender;
    private final Environment env;
    
    public GmailEmailServiceImpl(JavaMailSender javaMailSender, Environment env) {
        this.javaMailSender = javaMailSender;
        this.env = env;
    }

    public Mono<HttpResponse> sendEmail(User user, String origin) {
        System.out.println("Sender is: " + user);
        System.out.println("Verification code is: " +  user.getVerificationToken());
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration");
        mailMessage.setFrom(env.getProperty("spring.mail.username"));
        mailMessage.setText("Dear " + user.getUserId() + ", \n\n"+ 
                                            "We are delighted to see you joining our growing member base.\n\n" + 
                                            "To be able to finalize the sign up process, please verify your account by clicking the link: \n\n" +
                                            origin+ "\\" + "api" + "\\" + "v1" + "\\"  + "verify" +
                                            "\\" + user.getUserId()+ "\\" + user.getVerificationToken() + ".\n\n"+
                                            "Thanks for choosing us.\n\n" +
                                           "Kind Regards, \n\n" + "On behalf of GoTogether Team");
        System.out.println("Mail message: " + mailMessage);
        Mono.fromRunnable(() -> javaMailSender.send(mailMessage))
                  .doOnNext((t) -> {
                       System.out.println("Sent the messages: " + t);
                  })
                  .doAfterSuccessOrError((t, u) -> {
                      System.out.println("this is after success or error: " + t + "  " + u);
                  }).onErrorResume( e -> Mono.error(new IllegalStateException("Error in sending the email")));

        return Mono.empty();
    }
}
