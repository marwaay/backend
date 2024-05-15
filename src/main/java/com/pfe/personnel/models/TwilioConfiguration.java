package com.pfe.personnel.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("twilio")
@Getter
@Setter
public class TwilioConfiguration {
    private String account_sid;
    private String auth_token;
    private String trial_number;

    public TwilioConfiguration() {
        // Default constructor
    }
}
