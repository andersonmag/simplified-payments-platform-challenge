package com.github.andersonmag.simplifiedpaymentsplatformchallenge.controller;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.DepositRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.TransferRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.service.TransferService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@AllArgsConstructor
@RestController
public class TransferController {

    private final TransferService service;

    @PostMapping("/transfer")
    public ResponseEntity<HttpMethod> makeTransfer(@Valid @RequestBody TransferRequest request,
                                                   ServletUriComponentsBuilder uriBuilder) {
        final var createdTransfer = service.makeTransfer(request);
        final var uriGet = uriBuilder.path("/{id}").buildAndExpand(createdTransfer.getId()).toUri();
        return ResponseEntity.created(uriGet).build();
    }

    @GetMapping("/deposit")
    public ResponseEntity<HttpMethod> makeDeposit(@Valid @RequestBody DepositRequest request) {
        service.makeDeposit(request);
        return ResponseEntity.ok().build();
    }
}
