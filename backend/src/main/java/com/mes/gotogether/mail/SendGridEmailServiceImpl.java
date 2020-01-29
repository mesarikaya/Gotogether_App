package com.mes.gotogether.mail;

import com.mes.gotogether.domains.User;
import com.mes.gotogether.domains.responses.HttpResponse;
import com.mes.gotogether.mail.domain.SendGridEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
public class SendGridEmailServiceImpl implements EmailService{
    
    private final Environment env;
    private WebClient client;
    private final String END_POINT= "https://api.sendgrid.com/v3/";
    private final String SEND_URI = END_POINT + "mail/send";
    
    public SendGridEmailServiceImpl(Environment env) {
        this.env = env;
        client = WebClient.builder()
                                        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                                        .baseUrl(END_POINT).build();
    }
    
    @Override
    public Mono<HttpResponse> sendEmail(User user, String origin) {
        
        System.out.println("SendEmail implementation active");
        
        String baseUrl = env.getProperty("SENDGRID_URL");
        String apiKey = env.getProperty("SENDGRID_API_KEY");
        
        String from = "noreplygotogether@gmail.com";
        String subject = "Complete Registration";
        String to = user.getEmail();
        String content = "Dear " + user.getUserId() + "," + "<br>"+ "<br>" +
                                        "We are delighted to see you joining our growing member base."+ "<br>" + 
                                        "To be able to finalize the sign up process, please verify your account by clicking the link: " + "<br>" +
                                        origin+ "/"  + "verify/validate" +
                                        "/" + user.getUserId()+ "/" + user.getVerificationToken() + "." +"<br>"+ "<br>" + 
                                        "Thanks for choosing us." + "<br>" +
                                       "Kind Regards," + "<br>" + "On behalf of GoTogether Team";
          Mono<HttpResponse> response = null;
            try {
                SendGridEmail sendGridEmail = SendGridEmail.build(from, user.getEmail(), subject, content);
                response = client.post().uri(SEND_URI)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(sendGridEmail))
                .exchange().map(sgresponse -> {

                log.info("Send Grid response: {}", sgresponse.statusCode().toString());
                HttpResponse notificationStatusResponse = null;
                if (sgresponse.statusCode().is2xxSuccessful()) {
                    notificationStatusResponse = new HttpResponse(HttpStatus.ACCEPTED, 
                                                                                                      HttpResponse.ResponseType.SUCCESS,
                                                                                                      "Message request delivered");
                } else {
                    notificationStatusResponse = new HttpResponse(HttpStatus.EXPECTATION_FAILED, 
                                                                                                      HttpResponse.ResponseType.FAILURE,
                                                                                                      sgresponse.statusCode().toString());
                }
                return notificationStatusResponse;
          })
         .doOnSuccess( message -> log.debug("sent email via SendGrid successfully {}", message))
         .doOnError((error -> {
               log.error("email via SendGrid failed ", error);
          }));
    } catch (WebClientException webClientException) {
        System.out.println("Unexpected error occured in email delivery" + webClientException);
    }
    
    return response;

  
        
        /*final BodyInserter<String, ReactiveHttpOutputMessage> body =  BodyInserters.fromObject(mailBody);
        System.out.println("Activating webclient");
        return webClient.mutate()
                                    .baseUrl(baseUrl)
                                    .build().post()
                                    .uri("/v3/mail/send")
                                    .body(body)
                                    .header("Authorization", "Bearer ", apiKey)
                                    .header("Content-Type", "application/json")
                                    .retrieve()
                                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> 
                                        Mono.error(new RuntimeException("Error on send email: " + clientResponse))
                                    ).bodyToMono(Void.class);*/
        
        /*if (response.getStatusCode()==202){
                return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                                       .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                                       .body(new HttpResponse(HttpStatus.ACCEPTED, 
                                                                                                                   HttpResponse.ResponseType.SUCCESS, 
                                                                                                                   "Verification token is successfully sent!")));
        }
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                               .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                               .body(new HttpResponse(HttpStatus.EXPECTATION_FAILED, 
                                                                                                           HttpResponse.ResponseType.FAILURE, 
                                                                                                           "Email server rejected the request!\n" 
                                                                                                               + "response body: " 
                                                                                                                + response.getBody()  + "\n"
                                                                                                                + "response headers: " 
                                                                                                                + response.getHeaders())));*/
               
    }
}
/*        } catch (IOException ex) {
            return Mono.just(new HttpResponse(HttpStatus.BAD_REQUEST, 
                                                                          HttpResponse.ResponseType.FAILURE, 
                                                                          ex.getMessage()));
        }*/

