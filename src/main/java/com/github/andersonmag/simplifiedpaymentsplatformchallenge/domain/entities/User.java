package com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, length = 11)
    private String document;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType type;
    private BigDecimal balance = BigDecimal.ZERO;

    public User(Long id) {
        this.id = id;
    }
}
