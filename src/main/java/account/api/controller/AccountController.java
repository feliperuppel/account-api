package account.api.controller;

import account.api.domain.Account;
import account.api.domain.Transaction;
import account.api.service.AccountService;
import account.api.service.TransactionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Controller("/account")
@Produces
public class AccountController {

    private TransactionService transactionService;
    private AccountService accountService;

    private static final String TRANSACTION_ADDED_TO_THE_QUEUE = "Transaction added to the processing queue - %s";

    @Inject
    public AccountController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @Put("/create")
    public HttpResponse<Account> create() {
        return HttpResponse.created(accountService.createAccount());
    }

    @Get
    public HttpResponse<List<Account>> list() {
        return HttpResponse.ok(accountService.listAccounts());
    }

    @Get("/{id}")
    public HttpResponse<Account> getAccount(final Long id) {
        return HttpResponse.ok(accountService.getAccount(id));
    }

    @Post("/{id}/deposit/{amount}")
    public HttpResponse deposit(final Long id, final BigDecimal amount) {
        Transaction t = new Transaction();
        t.setOperation(Transaction.Operation.DEPOSIT);
        t.setToAccount(id);
        t.setAmount(amount);
        transactionService.register(t);
        return HttpResponse.accepted().body(new String(String.format(TRANSACTION_ADDED_TO_THE_QUEUE, t)));
    }

    @Post("/{id}/withdraw/{amount}")
    public HttpResponse withdraw(final Long id, final BigDecimal amount) {
        Transaction t = new Transaction();
        t.setOperation(Transaction.Operation.WITHDRAW);
        t.setFromAccount(id);
        t.setAmount(amount);
        transactionService.register(t);
        return HttpResponse.accepted().body(String.format(TRANSACTION_ADDED_TO_THE_QUEUE, t));
    }

    @Post("/{from}/transfer/{amount}/toaccount/{to}")
    public HttpResponse transfer(final Long from, final BigDecimal amount, final Long to) {
        Transaction t = new Transaction();
        t.setOperation(Transaction.Operation.TRANSFER);
        t.setFromAccount(from);
        t.setToAccount(to);
        t.setAmount(amount);
        transactionService.register(t);
        return HttpResponse.accepted().body(String.format(TRANSACTION_ADDED_TO_THE_QUEUE, t));
    }

    @Get("/{accountId}/transaction")
    public HttpResponse<List<Transaction>> getTransaction(final Long accountId) {
        return HttpResponse.ok(transactionService.getHistory(accountId));
    }

}
