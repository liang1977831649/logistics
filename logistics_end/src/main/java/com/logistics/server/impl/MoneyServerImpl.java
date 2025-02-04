package com.logistics.server.impl;

import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.MoneyServer;
import com.logistics.utils.Md5Util;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

@Service
public class MoneyServerImpl implements MoneyServer {

    @Autowired
    private MoneyMapper moneyMapper;
    @Autowired
    private WebMapper webMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public Money getMoneyByUserId() {
        String id = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        return moneyMapper.selectMoneyByUserId(id);
    }

    @Override
    public void passwordValid(Account account) {
        account.setId((String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id"));
        Integer role = (Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");

        Account accountById;
        if (role == 0) {
            accountById = webMapper.getAdminById(account);
        } else {
            accountById = webMapper.getUserById(account);
        }
        account.setPassword(Md5Util.getMD5String(account.getPassword()));
        if (!accountById.getPassword().equals(account.getPassword())) {
            throw new RuntimeException("密码错误");
        }
    }

    @Override
    @Transactional
    public void moneyTransaction(String payId, String collectId, Float cost, int payType) {
        Money moneyPay = moneyMapper.selectMoneyByUserId(payId);
        Money moneyCollect = moneyMapper.selectMoneyByUserId(collectId);

        if (payType == 2) {//如果是账户支付
            if (moneyPay.getBalance() < cost) {
                throw new RuntimeException("余额不足");
            }
            moneyPay.setBalance(moneyPay.getBalance() - cost);
            moneyMapper.updateMoneyByUserId(moneyPay);
        }

        moneyCollect.setBalance(moneyCollect.getBalance() + cost);
        moneyMapper.updateMoneyByUserId(moneyCollect);
    }

    @Override
    public void addMoneyAccount() {
        String id = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        Money money = new Money();
        money.setUserId(id);
        money = moneyMapper.selectMoneyByUserId(id);
        if (money == null) {
            //新增账户
            money = new Money();
            money.setUserId(id);
            money.setCarNum("");
            money.setBalance(Float.valueOf(0));
            moneyMapper.insertAccountForUser(money);


            //新增 替身User
            //先判断是不是管理员身份登陆的
            Account account = new Account();
            account.setId(id);
            Admin adminById = webMapper.getAdminById(account);
            if(adminById==null){
                //证明这个不是管理员
                return ;
            }
            //如果不为空，那就是管理员，需要增加一个替身
            User user = userMapper.selectUserById(id);
            if (user != null) {
                return;
            }
            user = new User();
            user.setId(id);
            user.setName("平台");
            user.setPassword(UUID.randomUUID().toString().substring(0, 15).replace("-", ""));
            user.setRole(1);
            userMapper.insertUserFroRegister(user);
        }
    }

    @Override
    @Transactional
    public void withdrawMoney() {
        String id = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        Money money = moneyMapper.selectMoneyByUserId(id);
        if(money.getBalance()==0){
            throw new RuntimeException("余额0,不可提现");
        }
        if(StringUtil.isNullOrEmpty(money.getCarNum())){
            throw new RuntimeException("没有绑定银行卡，提现失败");
        }
        //创建
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPayId(id);
        paymentRecord.setCollectId(id);
        paymentRecord.setCost(money.getBalance());
        paymentRecord.setPayType(2);
        paymentRecord.setTransaction(3);
        paymentRecord.setTransactionId("");
        //写入
        paymentRecordMapper.insertPaymentRecord(paymentRecord);
        //更改
        money.setBalance(Float.valueOf(0));
        moneyMapper.updateMoneyByUserId(money);

    }

    @Override
    //更新和添加
    @Transactional
    public void updateMoneyByUserId(Money money) {
        String id = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        money.setUserId(id);
        //查找是否存在，如果没有，就创建，如果有就添加卡号
        Money money1 = moneyMapper.selectMoneyByUserId(id);
        if (money1 == null) {
            throw new RuntimeException("账号不存在");
        }
        moneyMapper.updateMoneyByUserId(money);
    }
}
