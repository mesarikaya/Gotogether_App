/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mes.gotogether.mail.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendGridEmail {

    private List<Personalization> personalizations;
    private Personalization.EmailEntity from;
    private String subject;
    private List<Map<String, String>> content;

    public static SendGridEmail build(String sentFrom, String sentTo, String subject, String content){
        
        SendGridEmail sendGridEmail = new SendGridEmail();
        Personalization personalization = new Personalization();
        Personalization.EmailEntity from = new Personalization.EmailEntity(sentFrom, "Sendgrid");
        Personalization.EmailEntity to = new Personalization.EmailEntity(sentTo, "User");
        sendGridEmail.setFrom(from);
        personalization.addTo(to);
        sendGridEmail.setSubject(subject);
        sendGridEmail.setContent(List.of(Map.of("type", "text/html", "value", content)));
        sendGridEmail.setPersonalizations(Collections.singletonList(personalization));
        
        return sendGridEmail;
    };

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Personalization {

        @JsonProperty("to")
        private List<EmailEntity> tos;

        @Data
        @AllArgsConstructor
        public static class EmailEntity {
          private String email;
          private String name;
        }

        @Data
        @AllArgsConstructor
        public static class ToHolder {

          private List<EmailEntity> to;
          // private String email;
        }

        @JsonProperty("to")
        public List<EmailEntity> getTos() {
          if (tos == null) {
            return Collections.emptyList();
          }
          return tos;
        }

        private void addTo(Personalization.EmailEntity newEmail) {
          if (tos == null) {
            tos = new ArrayList<>();
            tos.add(newEmail);
          } else {
            tos.add(newEmail);
          }
        }
  }
}