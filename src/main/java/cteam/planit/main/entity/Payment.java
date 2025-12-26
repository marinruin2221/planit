package cteam.planit.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "payment_seq_gen", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq_gen")
  private Long id;

  @Column(name = "payment_key", nullable = false, unique = true)
  private String paymentKey;

  @Column(name = "order_id", nullable = false, unique = true)
  private String orderId;

  @Column(nullable = false)
  private Long amount;

  @Column(name = "order_name")
  private String orderName;

  private String status;

  @Column(name = "approved_at")
  private String approvedAt;
}
