package com.logistics.server;

import com.logistics.entity.Account;
import com.logistics.entity.Money;
import com.logistics.entity.User;

public interface MoneyServer {

    void updateMoneyByUserId(Money money);
    Money getMoneyByUserId();

    void passwordValid(Account account);

    void moneyTransaction(String payId,String collectId,Float cost,int payType);

    void addMoneyAccount();

    void withdrawMoney();
}
