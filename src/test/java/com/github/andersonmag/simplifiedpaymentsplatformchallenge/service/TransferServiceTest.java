package com.github.andersonmag.simplifiedpaymentsplatformchallenge.service;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.DepositRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.NotificationSendResponse;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.TransferRequest;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.Transfer;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.entities.User;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.enums.UserType;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.exceptions.ResourceNotFoundException;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.exceptions.TransferNotAllowedException;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository.TransferRepository;
import com.github.andersonmag.simplifiedpaymentsplatformchallenge.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final Long LOGISTIC_USER_ID = 1L;
    private static final Long COMMOM_USER_ID = 2L;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestService requestService;
    @InjectMocks
    private TransferService transferService;
    @Captor
    private ArgumentCaptor<List<User>> captor;

    @Test
    void shouldThrowExceptionForMakeTransferWithPayerNotFound() {
        final var request = getRequestTransfer(LOGISTIC_USER_ID, COMMOM_USER_ID);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> transferService.makeTransfer(request));
    }

    @Test
    void shouldThrowExceptionForMakeTransferFromUserLogistic() {
        final var request = getRequestTransfer(LOGISTIC_USER_ID, COMMOM_USER_ID);

        when(userRepository.findById(LOGISTIC_USER_ID))
                .thenReturn(Optional.of(getUserByType(UserType.LOGISTIC, BigDecimal.ZERO)));

        final var exception = Assertions.assertThrows(TransferNotAllowedException.class,
                () -> transferService.makeTransfer(request));

        Assertions.assertEquals("Not allowed to transfer for this type user", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForMakeTransferWithPayeeNotFound() {
        final var request = getRequestTransfer(COMMOM_USER_ID, LOGISTIC_USER_ID);

        when(userRepository.findById(COMMOM_USER_ID))
                .thenReturn(Optional.of(getUserByType(UserType.COMMON, BigDecimal.valueOf(200.0))));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> transferService.makeTransfer(request));
    }

    @Test
    void shouldThrowExceptionForMakeTransferWithoutEnoughBalanse() {
        final var request = getRequestTransfer(COMMOM_USER_ID, LOGISTIC_USER_ID);

        mockBehaviorFromUsersFinding(BigDecimal.valueOf(10.0));

        final var exception = Assertions.assertThrows(TransferNotAllowedException.class,
                () -> transferService.makeTransfer(request));

        Assertions.assertEquals("Enough balance to make transfer", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionCauseAuthorizationResponseNotAllowed() {
        final var request = getRequestTransfer(COMMOM_USER_ID, LOGISTIC_USER_ID);

        mockBehaviorFromUsersFinding(BigDecimal.valueOf(200.0));

        when(requestService.requestAuthorization()).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(401)));

        final var exception = Assertions.assertThrows(TransferNotAllowedException.class,
                () -> transferService.makeTransfer(request));

        Assertions.assertEquals("Not autorized to make transfer", exception.getMessage());
    }

    @Test
    void shouldSaveTransferSuccessfully() {
        final var request = getRequestTransfer(COMMOM_USER_ID, LOGISTIC_USER_ID);
        var initialBalancePayer = BigDecimal.valueOf(200.0);

        mockBehaviorFromUsersFinding(initialBalancePayer);
        mockBehaviorFromRequestsResponse(200, 200);
        when(transferRepository.save(any(Transfer.class))).thenReturn(request.toModel());

        transferService.makeTransfer(request);
        verify(transferRepository, times(1)).save(any(Transfer.class));

        verify(userRepository, times(1)).saveAll(captor.capture());
        List<User> updatedUsers = captor.getValue();

        Assertions.assertEquals(initialBalancePayer.subtract(request.value()), updatedUsers.get(0).getBalance());
        Assertions.assertEquals(request.value(), updatedUsers.get(1).getBalance());
    }

    @Test
    void shouldSaveTransferSuccessfullyEvenWithoutSendingNotification() {
        final var request = getRequestTransfer(COMMOM_USER_ID, LOGISTIC_USER_ID);

        mockBehaviorFromUsersFinding(BigDecimal.valueOf(200.0));
        mockBehaviorFromRequestsResponse(200, 401);

        Assertions.assertDoesNotThrow(() -> {
            transferService.makeTransfer(request);
            verify(userRepository, times(1)).saveAll(anyList());
            verify(transferRepository, times(1)).save(any(Transfer.class));
        });
    }

    @Test
    void shouldSaveDepositSuccessfully() {
        final var request = getRequestDeposit(LOGISTIC_USER_ID);
        var initialBalance = BigDecimal.valueOf(200.0);
        var logisticUser = getUserByType(UserType.LOGISTIC, initialBalance);

        when(userRepository.findById(LOGISTIC_USER_ID)).thenReturn(Optional.of(logisticUser));
        when(requestService.sendNotification()).thenReturn(
                new ResponseEntity<>(
                        new NotificationSendResponse(String.valueOf(200), "Response message"),
                        HttpStatusCode.valueOf(200)
                )
        );
        when(transferRepository.save(any(Transfer.class))).thenReturn(request.toModel());

        transferService.makeDeposit(request);

        verify(transferRepository, times(1)).save(any(Transfer.class));
        verify(userRepository, times(1)).save(any(User.class));
        Assertions.assertEquals(initialBalance.add(request.value()), logisticUser.getBalance());
    }

    private void mockBehaviorFromUsersFinding(BigDecimal initialBalancePayer) {
        when(userRepository.findById(COMMOM_USER_ID)).thenReturn(Optional.of(getUserByType(UserType.COMMON, initialBalancePayer)));
        when(userRepository.findById(LOGISTIC_USER_ID)).thenReturn(Optional.of(getUserByType(UserType.LOGISTIC, BigDecimal.ZERO)));
    }

    private void mockBehaviorFromRequestsResponse(Integer codeResponseAuthorization, Integer codeResponseNotification) {
        when(requestService.requestAuthorization())
                .thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(codeResponseAuthorization)));
        when(requestService.sendNotification()).thenReturn(
                new ResponseEntity<>(
                        new NotificationSendResponse(String.valueOf(codeResponseNotification), "Response message"),
                        HttpStatusCode.valueOf(codeResponseNotification)
                )
        );
    }

    private TransferRequest getRequestTransfer(Long payer, Long payee) {
        return new TransferRequest(BigDecimal.valueOf(100.0), payer, payee);
    }

    private DepositRequest getRequestDeposit(Long payee) {
        return new DepositRequest(BigDecimal.valueOf(100.0), payee);
    }

    private User getUserByType(UserType type, BigDecimal balance) {
        User user = new User();
        user.setId(UserType.LOGISTIC == type ? LOGISTIC_USER_ID : COMMOM_USER_ID);
        user.setName("User");
        user.setType(type.name());
        user.setEmail("user@user.com");
        user.setDocument("00000000000");
        user.setPassword("password");
        user.setBalance(balance);
        return user;
    }
}