package com.github.andersonmag.simplifiedpaymentsplatformchallenge.service;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.NotificationSendResponse;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.TransferRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.User;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.UserType;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.exceptions.ResourceNotFoundException;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.exceptions.TransferNotAllowedException;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository.TransferRepository;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Service
@AllArgsConstructor
public class TransferService {

    private final TransferRepository repository;
    private final UserRepository userRepository;
    private final RequestService requestService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    @Transactional
    public Transfer makeTransfer(final TransferRequest request) {
        final var payer = getUserById(request.payer());
        if (UserType.LOGISTIC == payer.getType()) {
            throw new TransferNotAllowedException("Not allowed to transfer for this type user");
        }

        final var payee = getUserById(request.payee());

        if (payer.getBalance().compareTo(request.value()) < 0) {
            throw new TransferNotAllowedException("Enough balance to make transfer");
        }

        if (requestService.requestAuthorization().getStatusCode() != HttpStatus.OK) {
            throw new TransferNotAllowedException("Not autorized to make transfer");
        }

        payer.setBalance(payer.getBalance().subtract(request.value()));
        payee.setBalance(payee.getBalance().add(request.value()));
        userRepository.saveAll(List.of(payer, payee));

        final var createdTransfer = repository.save(request.toModel());
        executorService.submit(this::sendNotificationAsync);

        return createdTransfer;
    }

    private void sendNotificationAsync() {
        ResponseEntity<NotificationSendResponse> notificationResponse = requestService.sendNotification();
        if (notificationResponse.getStatusCode() != HttpStatus.OK) {
            LOGGER.error(
                    "Error sending notification, try again later.",
                    new RuntimeException(Objects.requireNonNull(notificationResponse.getBody()).message())
            );
        }
    }

    private User getUserById(Long idUser) {
        return userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + idUser));
    }
}
