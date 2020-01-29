package com.mes.gotogether.mail;

import com.mes.gotogether.domains.User;
import com.mes.gotogether.domains.responses.HttpResponse;
import reactor.core.publisher.Mono;

/**
 * Email Service for user registration process
 * @author Ergin Sarikaya
 */
public interface EmailService {

    Mono<HttpResponse> sendEmail(User user, String origin);
}
