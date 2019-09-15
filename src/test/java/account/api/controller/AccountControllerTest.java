package account.api.controller;

import account.api.domain.Account;
import account.api.domain.Transaction;
import account.api.service.AccountService;
import account.api.service.TransactionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountControllerTest {

    private static AccountController controller;
    private static TransactionService transactionService;
    private static AccountService accountService;

    @BeforeAll
    public static void init(){
        transactionService = Mockito.mock(TransactionService.class);
        accountService = Mockito.mock(AccountService.class);
        controller = new AccountController(transactionService, accountService);
    }

    @Test
    public void testCreate() {
        Mockito.when(accountService.createAccount()).thenReturn(new Account());
        HttpResponse<Account> response = controller.create();
        Mockito.verify(accountService, Mockito.times(1)).createAccount();
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(response.body().getBalance()));
    }

    @Test
    public void testListAccounts() {
        Mockito.when(accountService.listAccounts()).thenReturn(new ArrayList<>());
        HttpResponse<List<Account>> response = controller.list();
        Mockito.verify(accountService, Mockito.times(1)).listAccounts();
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    public void testGetSingleAccount(){
        Mockito.when(accountService.getAccount(1L)).thenReturn(Mockito.mock(Account.class));
        HttpResponse<Account> response = controller.getAccount(1L);
        Mockito.verify(accountService, Mockito.times(1)).getAccount(1L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
    }

    @Test
    public void testDeposit(){
        Mockito.reset(transactionService);

        HttpResponse response = controller.deposit(1L, BigDecimal.TEN);

        ArgumentCaptor<Transaction> argument = ArgumentCaptor.forClass(Transaction.class);
        Mockito.verify(transactionService, Mockito.times(1)).register(argument.capture());
        Assertions.assertEquals(Transaction.Status.NEW, argument.getValue().getStatus());
        Assertions.assertEquals(Transaction.Operation.DEPOSIT, argument.getValue().getOperation());
        Assertions.assertEquals(0, BigDecimal.TEN.compareTo(argument.getValue().getAmount()));
        Assertions.assertEquals(1L, argument.getValue().getToAccount());
        Assertions.assertNull(argument.getValue().getFromAccount());

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        Assertions.assertNotNull(response.body());
    }

    @Test
    public void testWithdraw(){
        Mockito.reset(transactionService);

        HttpResponse response = controller.withdraw(1L, BigDecimal.TEN);

        ArgumentCaptor<Transaction> argument = ArgumentCaptor.forClass(Transaction.class);
        Mockito.verify(transactionService, Mockito.times(1)).register(argument.capture());
        Assertions.assertEquals(Transaction.Status.NEW, argument.getValue().getStatus());
        Assertions.assertEquals(Transaction.Operation.WITHDRAW, argument.getValue().getOperation());
        Assertions.assertEquals(0, BigDecimal.TEN.compareTo(argument.getValue().getAmount()));
        Assertions.assertEquals(1L, argument.getValue().getFromAccount());
        Assertions.assertNull(argument.getValue().getToAccount());

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        Assertions.assertNotNull(response.body());
    }


    @Test
    public void testTransfer(){
        Mockito.reset(transactionService);

        HttpResponse response = controller.transfer(1L, BigDecimal.TEN, 2L);

        ArgumentCaptor<Transaction> argument = ArgumentCaptor.forClass(Transaction.class);
        Mockito.verify(transactionService, Mockito.times(1)).register(argument.capture());
        Assertions.assertEquals(Transaction.Status.NEW, argument.getValue().getStatus());
        Assertions.assertEquals(Transaction.Operation.TRANSFER, argument.getValue().getOperation());
        Assertions.assertEquals(0, BigDecimal.TEN.compareTo(argument.getValue().getAmount()));
        Assertions.assertEquals(1L, argument.getValue().getFromAccount());
        Assertions.assertEquals(2L, argument.getValue().getToAccount());

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        Assertions.assertNotNull(response.body());
    }

    @Test
    public void testTransactionHist() {
        Mockito.when(transactionService.getHistory(1L)).thenReturn(new ArrayList<>());
        HttpResponse<List<Transaction>> response = controller.getTransaction(1L);
        Mockito.verify(transactionService, Mockito.times(1)).getHistory(1L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertTrue(response.body().isEmpty());
    }
}
