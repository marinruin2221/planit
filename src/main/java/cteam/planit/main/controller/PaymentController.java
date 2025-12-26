package cteam.planit.main.controller;

import cteam.planit.main.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/confirm")
  public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> payload) {
    try {
      String paymentKey = payload.get("paymentKey");
      String orderId = payload.get("orderId");
      String amount = payload.get("amount");

      Object result = paymentService.confirmPayment(paymentKey, orderId, amount);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }
}
