package pl.bankapp.service;

import org.springframework.stereotype.Service;

/** Feature 19 - wysyłanie historii przelewów na email
 */
@Service
public class SMTPClient {

    public boolean send(String subject, String text, String emailAddress) {
        return false;
    }
}
