package account.api.service;

import javax.inject.Singleton;

@Singleton
public class AccountService {

    public String hello(){
        return "Hello From Service";
    }

}