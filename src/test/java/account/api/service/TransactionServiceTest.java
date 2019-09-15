package account.api.service;

import account.api.domain.Transaction;
import account.api.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TransactionServiceTest {

    private static TransactionRepository repository;
    private static TransactionService service;

    @BeforeAll
    public static void init() {
        repository = Mockito.mock(TransactionRepository.class);
        service = new TransactionServiceImpl(repository);
    }

    @Test
    public void testListAccounts() {
        Mockito.reset(repository);
        service.getHistory(1L);
        Mockito.verify(repository, Mockito.times(1)).findByAccountId(1L);
    }

    @Test
    public void testQueue() {
        Assertions.assertFalse(service.hasElements());
        Transaction a = new Transaction();

        service.register(a);

        Mockito.verify(repository, Mockito.times(1)).save(a);
        Assertions.assertTrue(service.hasElements());

        Transaction b = service.getNext();

        Assertions.assertNotNull(b);
        Assertions.assertEquals(a, b);
        Assertions.assertFalse(service.hasElements());

    }
}
