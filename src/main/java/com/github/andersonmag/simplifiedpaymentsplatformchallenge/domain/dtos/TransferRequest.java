package com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotNull BigDecimal value, @NotNull Long payer, @NotNull Long payee) {
    public Transfer toModel() {
        return new Transfer(this);
    }
}
