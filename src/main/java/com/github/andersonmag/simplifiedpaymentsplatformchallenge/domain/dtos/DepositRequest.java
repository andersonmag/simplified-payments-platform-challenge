package com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.TransferType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(@NotNull @Min(value = 1) BigDecimal value, @NotNull Long payee) {
    public Transfer toModel() {
        return new Transfer(null, payee, value, TransferType.DEPOSIT);
    }
}