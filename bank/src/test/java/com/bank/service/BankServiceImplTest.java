package com.bank.service;
import com.bank.domain.Account;
import com.bank.persistence.AccountDAO;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceImplTest {
    @Mock
    private AccountDAO accountDAO;
    private BankServiceImpl bankService;

    @BeforeEach
    void setup() {
        bankService = new BankServiceImpl(accountDAO);
    }

    @Test 
    void deposit_withPositiveAmount_callsDaoDeposit() {
        //arrange
        int accountId = 1;
        BigDecimal amount = new BigDecimal("50.00");

        bankService.deposit(accountId, amount); //act
        verify(accountDAO).deposit(accountId, amount); //assert
    }

    @Test
    void deposit_withNegativeAmount_throwsIllegalArgumentException() {
        int accountId = 1;
        BigDecimal amount = new BigDecimal("-10.00");

        assertThrows(IllegalArgumentException.class,
            () -> bankService.deposit(accountId, amount));
        verify(accountDAO, never()).deposit(anyInt(), any());
    }

    @Test
    void transfer_toSameAccount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> bankService.transfer(1, 1, new BigDecimal("20.00")));
        verify(accountDAO, never()).transfer(anyInt(), anyInt(), any());
    }

    @Test void getBalance_forExistingAccount_returnsBalance() {
        Account account = new Account(1, "1234", new BigDecimal("100.00"));
        when(accountDAO.findById(1)).thenReturn(java.util.Optional.of(account));

        BigDecimal balance = bankService.getBalance(1);
        assertEquals(new BigDecimal("100.00"), balance);
    }
}
