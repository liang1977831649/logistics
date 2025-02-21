package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.GoodsCostServer;
import com.logistics.server.PaymentRecordServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class PaymentRecordServerImpl implements PaymentRecordServer {

    @Autowired
    private GoodsCostMapper goodsCostMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private RoomCostMapper roomCostMapper;

    @Autowired
    private MoneyServerImpl moneyServerImpl;
    @Autowired
    private AdminMapper adminMapper;

    @Override
    @Transactional
    public void addPaymentServer(PaymentRecord paymentRecord) {
        String id = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        paymentRecord.setPayId(id);
        //判断是什么不同的业务订单
        if (paymentRecord.getTransaction() == 1) {//农产品交易业务
            //获取，该农产品的对象
            GoodsCost goodsCost = goodsCostMapper.selectGoodsCostById(paymentRecord.getTransactionId());
            moneyServerImpl.moneyTransaction(id, goodsCost.getSalesId(), goodsCost.getCost(), paymentRecord.getPayType());
            //设置卖家
            paymentRecord.setCollectId(goodsCost.getSalesId());
            //设置买家
            //设置金额
            paymentRecord.setCost(goodsCost.getCost());
            goodsCost.setStatus(2);
            //更新状态
            goodsCostMapper.updateCostMapper(goodsCost);
        } else {//冷链室使用服务费用

            //根据当前管理员的areaId，查找对应的admin对象的Id
            String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
            Admin adminByAreaId=adminMapper.selectAdminByAreaId(areaId);

            paymentRecord.setCollectId(adminByAreaId.getId());

            //获取对应的冷链室费用的对象
            RoomCost roomCost = roomCostMapper.selectRoomCostById(paymentRecord.getTransactionId());

            //交易钱包余额
            moneyServerImpl.moneyTransaction(id, adminByAreaId.getId(), roomCost.getCost(), paymentRecord.getPayType());

            paymentRecord.setTransactionId(roomCost.getId());
            //更改状态
            roomCost.setStatus(2);
            roomCostMapper.updateRoomCost(roomCost);
        }
        //插入
        paymentRecordMapper.insertPaymentRecord(paymentRecord);
    }

    @Override
    public PageBean<PaymentRecord> getPaymentRecord(Integer pageNum, Integer pageSize, String userName, Integer transaction, String transactionId) {
        PageHelper.startPage(pageNum, pageSize);
        String id =(String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");

        List<PaymentRecord> paymentRecordList= paymentRecordMapper.selectPaymentList(userName, transaction, transactionId,id);

        return getPaymentRecordPageBean(paymentRecordList);
    }

    @Override
    public PageBean<PaymentRecord> getPaymentRecordOfWithdrew(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        String id =(String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        Integer transaction=3;
        List<PaymentRecord> paymentRecordList= paymentRecordMapper.selectPaymentListOfWithdraw( transaction,id);

        return getPaymentRecordPageBean(paymentRecordList);
    }

    public PageBean<PaymentRecord> getPaymentRecordPageBean(List<PaymentRecord> paymentRecordList){
        Page<PaymentRecord> page = (Page<PaymentRecord>) paymentRecordList;
        PageBean<PaymentRecord> paymentRecordPageBean = new PageBean<>();
        paymentRecordPageBean.setItems(page.getResult());
        paymentRecordPageBean.setTotal((int) page.getTotal());
        return paymentRecordPageBean;
    }
}
