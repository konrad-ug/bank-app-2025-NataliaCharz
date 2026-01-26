package pl.bankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class SMTPClient {

    public boolean send(String subject, String text, String emailAddress) {
        return false;
    }
}
