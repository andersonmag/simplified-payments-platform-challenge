package com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.TransferType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotNull @Min(value = 1) BigDecimal value, @NotNull Long payer, @NotNull Long payee) {
    public Transfer toModel() {
        return new Transfer(payer, payee, value, TransferType.TRANSFER.name());
    }
}
