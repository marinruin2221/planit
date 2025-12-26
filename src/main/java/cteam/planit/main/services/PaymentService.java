package cteam.planit.main.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import cteam.planit.main.entity.Payment;
import cteam.planit.main.repository.PaymentRepository;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

  // secret.properties에서 Toss Payments 시크릿 키를 가져옴
  @Value("${toss.secret.key}")
  private String tossSecretKey;

  private final WebClient.Builder webClientBuilder;
  private final PaymentRepository paymentRepository;

  public Object confirmPayment(String paymentKey, String orderId, String amount) {
    String encodedAuth = Base64.getEncoder()
        .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

    Map<String, String> body = new HashMap<>();
    body.put("paymentKey", paymentKey);
    body.put("orderId", orderId);
    body.put("amount", amount);

    Map response = webClientBuilder.build()
        .post()
        .uri("https://api.tosspayments.com/v1/payments/confirm")
        .header("Authorization", "Basic " + encodedAuth)
        .header("Content-Type", "application/json")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    if (response != null) {
      Payment payment = Payment.builder()
          .paymentKey((String) response.get("paymentKey"))
          .orderId((String) response.get("orderId"))
          .amount(Long.parseLong(amount))
          .orderName((String) response.get("orderName"))
          .status((String) response.get("status"))
          .approvedAt((String) response.get("approvedAt"))
          .build();

      paymentRepository.save(payment);
    }

    return response;
  }
}
