package com.logistics.server;


import com.logistics.entity.PageBean;
import com.logistics.entity.PaymentRecord;

public interface PaymentRecordServer {

    void addPaymentServer(PaymentRecord paymentRecord);

    PageBean<PaymentRecord> getPaymentRecord(Integer pageNum, Integer pageSize, String userName, Integer transaction, String transactionId);
}
