package com.logistics.controller;

import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.entity.PaymentRecord;
import com.logistics.server.PaymentRecordServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paymentRecord")
public class PaymentRecordController {
    @Autowired
    private PaymentRecordServer paymentRecordServer;
    @PostMapping
    public Result addPaymentRecord(@RequestBody @Validated PaymentRecord paymentRecord){
        paymentRecordServer.addPaymentServer(paymentRecord);
        return Result.success();
    }
    @GetMapping("/list")
    public Result getList(Integer pageNum,Integer pageSize,String userName,Integer transaction,String transactionId){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        PageBean<PaymentRecord> paymentRecord = paymentRecordServer.getPaymentRecord(pageNum, pageSize, userName, transaction, transactionId);
        return Result.success(paymentRecord);
    }

}
