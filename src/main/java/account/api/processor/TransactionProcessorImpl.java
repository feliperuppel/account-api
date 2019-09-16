package account.api.processor;

import account.api.domain.Transaction;
import account.api.repository.TransactionRepository;
import account.api.service.AccountService;
import account.api.service.TransactionService;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import static account.api.domain.Transaction.Status.*;

@Slf4j
@Singleton
public class TransactionProcessorImpl implements TransactionProcessor {

    private TransactionService transactionService;
    private TransactionRepository repository;
    private AccountService accountService;

    @Inject
    public TransactionProcessorImpl(TransactionRepository repository, TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.repository = repository;
    }

    @Override
    @Scheduled(fixedDelay = "${processor.scheduled.fixeddelay:5s}")
    public void process() {
        log.info("Checking Queue for new Transactions");
        while (transactionService.hasElements()) {
            final Transaction t = transactionService.getNext();
            t.setStatus(PROCESSING);
            try {
                switch (t.getOperation()) {
                    case DEPOSIT:
                        log.info("TransactionId {}: Performing deposit of {} to account id {}", t.getId(), t.getAmount(), t.getToAccount());
                        accountService.deposit(t.getToAccount(), t.getAmount());
                        break;
                    case WITHDRAW:
                        log.info("TransactionId {}: Performing withdraw of {} from account id {}", t.getId(), t.getAmount(), t.getFromAccount());
                        accountService.withdraw(t.getFromAccount(), t.getAmount());
                        break;
                    case TRANSFER:
                        if(t.getFromAccount().equals(t.getToAccount())){
                            throw new RuntimeException("Illegal Operation - Attempt to Transfer money from one account to the same account");
                        }else {
                            log.info("TransactionId {}: Performing transfer of {} from account id {} to account id {}", t.getId(), t.getAmount(), t.getFromAccount(), t.getToAccount());
                            accountService.transfer(t.getFromAccount(), t.getToAccount(), t.getAmount());
                        }
                        break;
                    default:
                        log.error("Operation Not Supported {} :", t);
                        throw new RuntimeException("Operation Not Supported");
                }
                t.setStatus(COMPLETED);
            } catch (Exception e) {
                log.error("Error processing transaction: Caused by - {} for Transaction - {}", e.getMessage(), t, e);
                t.setStatus(FAILED);
                t.setFailureReason(e.getMessage());
            } finally {
                log.info("Updating transaction : {}",t);
                repository.update(t);
            }
        }
    }
}
