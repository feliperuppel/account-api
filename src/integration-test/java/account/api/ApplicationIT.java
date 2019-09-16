package account.api;

import account.api.domain.Account;
import account.api.domain.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@MicronautTest(application = Application.class)
public class ApplicationIT {

    @Inject
    private EmbeddedServer server;
    @Inject
    private ApplicationContext context;

    private static ObjectMapper mapper;

    @BeforeAll
    private static void init() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    public void testCreate() throws IOException {
        ResponseWrapper response = callRest("/create", "PUT");
        Assertions.assertEquals(HttpStatus.CREATED.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);

        Account a = mapper.readValue(response.body, Account.class);
        Assertions.assertNotNull(a.getId());
        Assertions.assertEquals(BigDecimal.ZERO, a.getBalance());
    }

    @Test
    public void testGet() throws IOException {
        callRest("/create", "PUT");
        callRest("/create", "PUT");
        callRest("/create", "PUT");

        ResponseWrapper response = callRest("/", "GET");
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);

        List<Account> list = mapper.readValue(response.body, new TypeReference<List<Account>>() {
        });
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testGetID() throws IOException {
        callRest("/create", "PUT");

        ResponseWrapper response = callRest("/1", "GET");
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);

        Account account = mapper.readValue(response.body, Account.class);
        Assertions.assertEquals(1L, account.getId());
    }

    @Test
    public void testMoneyFLow() throws IOException, InterruptedException {
        callRest("/create", "PUT");//Creating account 1 case do not exist
        callRest("/create", "PUT");//Creating account 2 case do not exist
        Account a = mapper.readValue(callRest("/1", "GET").body, Account.class);
        Assertions.assertEquals(0, BigDecimal.valueOf(0.00).compareTo(a.getBalance()));
        checkAccountAndWait(1L, BigDecimal.valueOf(100), callRest("/1/deposit/100", "POST"));
        checkAccountAndWait(1L, BigDecimal.valueOf(80), callRest("/1/withdraw/20", "POST"));
        checkAccountAndWait(1L, BigDecimal.valueOf(80), callRest("/1/withdraw/2000", "POST"));
        checkAccountAndWait(1L, BigDecimal.valueOf(49.5), callRest("/1/transfer/30.5/toaccount/2", "POST"));
        checkAccountAndWait(1L, BigDecimal.valueOf(49.5), callRest("/1/transfer/10/toaccount/1", "POST"));
        Account b = mapper.readValue(callRest("/2", "GET").body, Account.class);
        Assertions.assertEquals(0, BigDecimal.valueOf(30.5).compareTo(b.getBalance()));

        //Checking transaction history
        ResponseWrapper response = callRest("/1/transaction", "GET");
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);

        List<Transaction> list = mapper.readValue(response.body, new TypeReference<List<Transaction>>() {
        });
        Assertions.assertFalse(list.isEmpty());


    }

    private void checkAccountAndWait(Long expectedId, BigDecimal expectedAmount, ResponseWrapper response) throws IOException, InterruptedException {
        Assertions.assertEquals(HttpStatus.ACCEPTED.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);
        TimeUnit.SECONDS.sleep(10);//Waiting 10 seconds to make sure that Processor will have enough time to execute
        Account account = mapper.readValue(callRest("/" + expectedId, "GET").body, Account.class);
        Assertions.assertEquals(0, expectedAmount.compareTo(account.getBalance()));
    }

    @Test
    public void testWithdraw() throws IOException {
        callRest("/create", "PUT");

        ResponseWrapper response = callRest("/1/withdraw/10.5", "POST");
        Assertions.assertEquals(HttpStatus.ACCEPTED.getCode(), response.statusCode);
        Assertions.assertNotNull(response.body);
    }

    public ResponseWrapper callRest(String endpoint, String requestMethod) throws IOException {

        URL url = new URL(server.getURL() + "/account" + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String body = br.readLine();
        Integer code = conn.getResponseCode();
        conn.disconnect();

        return new ResponseWrapper(code, body);
    }
}
