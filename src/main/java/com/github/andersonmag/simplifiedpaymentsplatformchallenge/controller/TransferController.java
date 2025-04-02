package com.github.andersonmag.simplifiedpaymentsplatformchallenge.controller;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.DepositRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.TransferRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Transferences", description = "Endpoints for transfer and deposit operations")
@AllArgsConstructor
@RestController
public class TransferController {

    private final TransferService service;

    @Operation(summary = "Make a transfer", description = "Transfer a value from one user to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer successfully created",
                    headers = @Header(name = "Location", description = "URI of the created transfer", required = true)),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    @PostMapping("/transfer")
    public ResponseEntity<HttpMethod> makeTransfer(
            @Parameter(description = "Transfer request containing payer, payee, and value", required = true)
            @Valid @RequestBody TransferRequest request,
            @Parameter(hidden = true) ServletUriComponentsBuilder uriBuilder) {

        final var createdTransfer = service.makeTransfer(request);
        final var uriGet = uriBuilder.path("/{id}").buildAndExpand(createdTransfer.getId()).toUri();
        return ResponseEntity.created(uriGet).build();
    }

    @Operation(summary = "Make a deposit", description = "Deposit a value into a user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit request"),
    })
    @PostMapping("/deposit")
    public ResponseEntity<HttpMethod> makeDeposit(
            @Parameter(description = "Deposit request containing user ID and deposit amount", required = true)
            @Valid @RequestBody DepositRequest request) {

        service.makeDeposit(request);
        return ResponseEntity.ok().build();
    }
}
