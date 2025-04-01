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
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transfer {
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payee;
    @ManyToOne
    @JoinColumn(name = "payer_id")
    private User payer;
    @Column(nullable = false)
    private BigDecimal value;
    @Enumerated(EnumType.STRING)
    private TransferType type = TransferType.TRANSFER;
    @CreatedDate
    private LocalDateTime createdAt;

    public Transfer(Long payerId, Long payeeId, BigDecimal value, TransferType type) {
        this.value = value;
        payer = new User(payerId);
        payee = new User(payeeId);
        this.type = type;
    }
}
