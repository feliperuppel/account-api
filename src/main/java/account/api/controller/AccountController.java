package account.api.controller;

import account.api.service.AccountService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import javax.inject.Inject;

@Controller
public class AccountController {

    private AccountService service;

    @Inject
    public AccountController(AccountService service){
        this.service = service;
    }

    @Get
    public String hello(){
        return service.hello();
    }
}
