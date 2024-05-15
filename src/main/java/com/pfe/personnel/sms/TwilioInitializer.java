package com.pfe.personnel.sms;

import com.pfe.personnel.models.TwilioConfiguration;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwilioInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioInitializer.class);

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioInitializer(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    @PostConstruct
    public void initializeTwilio() {
        Twilio.init(
                twilioConfiguration.getAccount_sid(),
                twilioConfiguration.getAuth_token()
        );
        LOGGER.info("Twilio initialized with account sid {}", twilioConfiguration.getAccount_sid());
    }
}
