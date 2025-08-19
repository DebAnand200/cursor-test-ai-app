package com.saas.taskapp.billing;

import com.saas.taskapp.user.User;
import com.saas.taskapp.user.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final UserRepository userRepository;

    @Value("${app.stripe.secretKey}")
    private String stripeSecretKey;

    @Value("${app.stripe.priceIdPro}")
    private String priceIdPro;

    @PostMapping("/checkout-session")
    public ResponseEntity<?> createCheckoutSession(@AuthenticationPrincipal User user) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceIdPro)
                        .setQuantity(1L)
                        .build())
                .setSuccessUrl("http://localhost:5173/dashboard?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/pricing")
                .build();

        Session session = Session.create(params);
        return ResponseEntity.ok(Map.of("url", session.getUrl()));
    }
}

