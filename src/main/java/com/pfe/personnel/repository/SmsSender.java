package com.pfe.personnel.repository;

import com.pfe.personnel.sms.SmsRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsSender {
    void sendSms(SmsRequest smsRequest);
}
