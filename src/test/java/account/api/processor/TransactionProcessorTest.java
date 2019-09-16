package account.api.processor;

import account.api.domain.Transaction;
import account.api.repository.TransactionRepository;
import account.api.service.AccountService;
import account.api.service.TransactionService;
import account.api.service.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static account.api.domain.Transaction.Operation.*;
import static account.api.domain.Transaction.Status.*;

public class TransactionProcessorTest {

    private static TransactionService transactionService;
    private static TransactionRepository repository;
    private static AccountService accountService;

    private static TransactionProcessor processor;

    @BeforeAll
    public static void init(){
        repository = Mockito.mock(TransactionRepository.class);
        accountService = Mockito.mock(AccountService.class);
        transactionService = new TransactionServiceImpl(repository);
        processor = new TransactionProcessorImpl(repository,transactionService,accountService);
    }

    @Test
    public void testProcessDeposit(){
        Transaction t = createTransaction(DEPOSIT);
        Assertions.assertEquals(NEW, t.getStatus());
        transactionService.register(t);
        processor.process();
        Mockito.verify(accountService, Mockito.times(1)).deposit(t.getToAccount(), t.getAmount());
        Assertions.assertEquals(COMPLETED, t.getStatus());
        Assertions.assertNull(t.getFailureReason());
        Mockito.verify(repository, Mockito.times(1)).update(t);
    }

    @Test
    public void testProcessWithdraw(){
        Transaction t = createTransaction(WITHDRAW);
        Assertions.assertEquals(NEW, t.getStatus());
        transactionService.register(t);
        processor.process();
        Mockito.verify(accountService, Mockito.times(1)).withdraw(t.getFromAccount(), t.getAmount());
        Assertions.assertEquals(COMPLETED, t.getStatus());
        Assertions.assertNull(t.getFailureReason());
        Mockito.verify(repository, Mockito.times(1)).update(t);
    }

    @Test
    public void testProcessTransfer(){
        Transaction t = createTransaction(TRANSFER);
        Assertions.assertEquals(NEW, t.getStatus());
        transactionService.register(t);
        processor.process();
        Mockito.verify(accountService, Mockito.times(1)).transfer(t.getFromAccount(), t.getToAccount(), t.getAmount());
        Assertions.assertEquals(COMPLETED, t.getStatus());
        Assertions.assertNull(t.getFailureReason());
        Mockito.verify(repository, Mockito.times(1)).update(t);
    }

    @Test
    public void testProcessWithErrorFromService(){
        Transaction t = createTransaction(DEPOSIT);
        Assertions.assertEquals(NEW, t.getStatus());
        Mockito.doThrow(new RuntimeException("Mocked Message")).when(accountService).deposit(Mockito.any(), Mockito.any());
        transactionService.register(t);
        processor.process();
        Assertions.assertEquals(FAILED, t.getStatus());
        Assertions.assertEquals("Mocked Message", t.getFailureReason());
        Mockito.verify(repository, Mockito.times(1)).update(t);
    }

    @Test
    public void testProcessTransferToSameAccount(){
        Transaction t = createTransaction(TRANSFER);
        t.setToAccount(t.getFromAccount());
        Mockito.reset(accountService);
        Assertions.assertEquals(NEW, t.getStatus());
        transactionService.register(t);
        processor.process();
        Mockito.verify(accountService, Mockito.times(0)).transfer(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Assertions.assertEquals(FAILED, t.getStatus());
        Assertions.assertEquals("Illegal Operation - Attempt to Transfer money from one account to the same account", t.getFailureReason());
        Mockito.verify(repository, Mockito.times(1)).update(t);
    }

    private Transaction createTransaction(Transaction.Operation operation){
        Transaction t = new Transaction();
        t.setOperation(operation);
        t.setStatus(NEW);
        t.setAmount(BigDecimal.TEN);
        t.setFromAccount(1L);
        t.setToAccount(2L);

        return t;
    }
}