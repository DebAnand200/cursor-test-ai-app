package com.saas.taskapp.billing;

import com.saas.taskapp.user.User;
import com.saas.taskapp.user.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final UserRepository userRepository;

    @Value("${app.stripe.webhookSecret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handle(@RequestHeader("Stripe-Signature") String sigHeader,
                                         @RequestBody String payload) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        if ("customer.subscription.updated".equals(event.getType()) ||
                "customer.subscription.created".equals(event.getType())) {
            Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            if (subscription != null) {
                String customerId = subscription.getCustomer();
                Optional<User> optionalUser = userRepository.findAll().stream()
                        .filter(u -> customerId.equals(u.getStripeCustomerId()))
                        .findFirst();
                optionalUser.ifPresent(u -> {
                    u.setSubscriptionTier("active".equals(subscription.getStatus()) ? "PRO" : "FREE");
                    userRepository.save(u);
                });
            }
        }
        return ResponseEntity.ok("success");
    }
}

