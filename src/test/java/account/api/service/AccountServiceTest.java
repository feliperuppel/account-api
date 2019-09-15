package account.api.service;

import account.api.domain.Account;
import account.api.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountServiceTest {

    private static AccountServiceImpl service;
    private static AccountRepository repository;

    @BeforeAll
    public static void init() {
        repository = Mockito.mock(AccountRepository.class);
        service = new AccountServiceImpl(repository);
    }


    @Test
    public void testDeposit() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ONE);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(account));

        service.deposit(1L, BigDecimal.TEN);

        ArgumentCaptor<Account> argument = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(repository, Mockito.times(1)).save(argument.capture());
        Assertions.assertEquals(0, BigDecimal.valueOf(11).compareTo(argument.getValue().getBalance()));
    }

    @Test()
    public void testDepositAccountNotFound() {
        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.deposit(1L, BigDecimal.TEN);
        });
    }

    @Test
    public void testWithdraw() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.TEN);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(account));

        service.withdraw(1L, BigDecimal.ONE);

        ArgumentCaptor<Account> argument = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(repository, Mockito.times(1)).save(argument.capture());
        Assertions.assertEquals(0, BigDecimal.valueOf(9).compareTo(argument.getValue().getBalance()));
    }

    @Test()
    public void testWithdrawAccountNotFound() {
        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.withdraw(1L, BigDecimal.TEN);
        });
    }

    @Test
    public void testWithdrawNotAvailableFunds() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ONE);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(account));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.withdraw(1L, BigDecimal.TEN);
        });

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testTransfer() {
        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(BigDecimal.TEN);

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(BigDecimal.ONE);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(toAccount));

        service.transfer(1L, 2L, BigDecimal.TEN);

        Mockito.verify(repository, Mockito.times(1)).save(fromAccount, toAccount);
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(fromAccount.getBalance()));
        Assertions.assertEquals(0, BigDecimal.valueOf(11).compareTo(toAccount.getBalance()));
    }

    @Test
    public void testTransferAccountNotFound() {
        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(BigDecimal.TEN);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(repository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.transfer(1L, 2L, BigDecimal.TEN);
        });
        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any(), Mockito.any());
        Assertions.assertEquals(0, BigDecimal.TEN.compareTo(fromAccount.getBalance()));
    }

    @Test
    public void testTransferNotAvailableFunds() {
        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(BigDecimal.ONE);

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(BigDecimal.TEN);

        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(toAccount));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.transfer(1L, 2L, BigDecimal.TEN);
        });
        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any(), Mockito.any());
        Assertions.assertEquals(0, BigDecimal.ONE.compareTo(fromAccount.getBalance()));
        Assertions.assertEquals(0, BigDecimal.TEN.compareTo(toAccount.getBalance()));
    }

    @Test
    public void testTransferToSameAccount() {
        Mockito.reset(repository);
        service.transfer(1L, 1L, BigDecimal.TEN);
        Mockito.verify(repository, Mockito.times(0)).findById(Mockito.anyInt());
        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any(), Mockito.any());
    }

    @Test
    public void testCreateAccount() {
        Mockito.reset(repository);
        service.createAccount();
        Mockito.verify(repository, Mockito.times(1)).save();
    }

    @Test
    public void testGetAccount() {
        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(new Account()));
        service.getAccount(1L);
        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testGetAccountNotFound() {
        Mockito.reset(repository);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getAccount(1L);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testListAccounts() {
        Mockito.reset(repository);
        service.listAccounts();
        Mockito.verify(repository, Mockito.times(1)).findAll();
    }
}
