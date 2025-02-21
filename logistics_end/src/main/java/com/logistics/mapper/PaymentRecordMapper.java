package com.logistics.mapper;

import com.logistics.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {

    void insertPaymentRecord(PaymentRecord paymentRecord);

    List<PaymentRecord> selectPaymentList(String userName, Integer transaction, String transactionId, String id);

    List<PaymentRecord> selectPaymentListOfWithdraw(Integer transaction, String id);
}
