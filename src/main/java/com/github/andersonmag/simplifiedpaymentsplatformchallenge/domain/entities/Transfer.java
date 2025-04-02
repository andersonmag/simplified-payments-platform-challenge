package com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.TransferType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payee;
    @ManyToOne
    @JoinColumn(name = "payer_id")
    private User payer;
    @Column(name = "amount", nullable = false)
    private BigDecimal value;
    @Column(nullable = false, length = 10)
    private String type = TransferType.TRANSFER.name();
    @CreatedDate
    private LocalDateTime createdAt;

    public Transfer(Long payerId, Long payeeId, BigDecimal value, String type) {
        this.value = value;
        payer = Objects.isNull(payerId) ? null : new User(payerId);
        payee = new User(payeeId);
        this.type = type;
    }
}
