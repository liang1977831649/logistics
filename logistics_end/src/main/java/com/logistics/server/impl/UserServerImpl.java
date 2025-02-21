package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.UserServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserServerImpl implements UserServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private WebMapper webMapper;
    @Autowired
    private RoomCostMapper roomCostMapper;
    @Autowired
    private GoodsCostMapper goodsCostMapper;
    @Autowired
    private MoneyMapper moneyMapper;
    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private TransportationMapper transportationMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public void insertUserForRegister(Account account) {

        //检查账号是否存在
        Account userById = webMapper.getUserById(account);
        if (userById != null) {
            throw new RuntimeException("该账号已存在");
        }

        User user = new User();
        user.setId(account.getId());
        user.setPassword(account.getPassword());
        user.setArea(account.getArea());
        user.setAreaId(account.getAreaId());

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));//设置密码

        user.setRole(1);//设置role
        user.setName("HelloWorld");

        userMapper.insertUserFroRegister(user);//插入
    }


    @Override
    public PageBean<User> getUserListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectUserByAreaId(areaId, name, id);

        //设置地域名
        String areaCodeByName = areaMapper.getAreaNameByAreaCode(areaId);
        for (User user : users) {
            user.setArea(areaCodeByName);
        }
        //向下接口转型
        Page<User> page = (Page<User>) users;
        PageBean<User> userPageBean = new PageBean<>();
        userPageBean.setItems(page.getResult());
        userPageBean.setTotal((int) page.getTotal());

        return userPageBean;
    }

    @Override
    public void addUser(User user) {
        //账号是否重复
        Account account = new Account();
        account.setId(user.getId());
        User userById = webMapper.getUserById(account);
        if (userById != null) {
            throw new RuntimeException("该账号已存在");
        }
        //设置一些字段
        user.setRole(1);
        if (user.getPhone() == null) {
            user.setPhone("");
        }
        if (user.getUserPic() == null) {
            user.setUserPic("");
        }
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        user.setAreaId(areaId);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.insertUserForAdd(user);
    }

    @Override
    public void updateUser(User user) {
        //验证账号是否存在
        Account account = new Account();
        account.setId(user.getId());
        User userById = webMapper.getUserById(account);
        if (userById == null) {
            throw new RuntimeException("该账户不存在");
        }
        if (!StringUtil.isNullOrEmpty(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userMapper.update(user);
    }

    @Override
    public User getUserByIdNoPassword(String id) {
        return userMapper.getUserByIdNoPassword(id);
    }

    @Override
    public void delete(String id) {
        //判断是否有未支付或未收款的订单
        List<GoodsCost> goodsCostsType1 = goodsCostMapper.selectGoodsCostListByUserId("", null, null, 1, id, 1);
        List<GoodsCost> goodsCostsType2 = goodsCostMapper.selectGoodsCostListByUserId("", null, null, 1, id, 2);
        if (!goodsCostsType1.isEmpty()) {
            throw new RuntimeException("存在未支付订单,不可删除");
        }
        if (!goodsCostsType2.isEmpty()) {
            throw new RuntimeException("存在未收款订单，不可删除");
        }
        List<RoomCost> roomCostList = roomCostMapper.selectRoomCostList(id, null, 1, null);
        if(!roomCostList.isEmpty()){
            throw new RuntimeException("存在未支付的冷链室使用费，不可删除");
        }
        Money money = moneyMapper.selectMoneyByUserId(id);
        if(money!=null){
            if(money.getBalance()>0){
                throw new RuntimeException("账户余额不为空，不可删除");
            }
        }

        //如果存在运输子单或配送子单，并且是未有到最终状态的也不能删除
        List<Transportation> transportationListStatusIsOne = transportationMapper.selectTransportationListByUserId(null,id,null,1,null);
        List<Transportation>  transportationListStatusIsTwo=transportationMapper.selectTransportationListByUserId(null,id,null,2,null);
        if(!transportationListStatusIsOne.isEmpty()||!transportationListStatusIsTwo.isEmpty()){
            throw new RuntimeException("该用户存在运输子单，不可删除");
        }

        List<Delivery> deliveryList = deliveryMapper.selectDeliveryByUserId(null, id, null, 4, null);
        if(!deliveryList.isEmpty()){
            throw new RuntimeException("该用户存在配送子单，不可删除");
        }
        userMapper.deleteUserById(id);
    }

    @Override
    public void updatePassword(String oldPwd, String newPwd, String id) {
        //查看原密码是否正确
        Account account = new Account();
        account.setId(id);
        User user = webMapper.getUserById(account);
        if (user == null) {
            throw new RuntimeException("该账号不存在");
        }
        //校验密码
        if (!bCryptPasswordEncoder.matches(oldPwd,user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        newPwd =bCryptPasswordEncoder.encode(newPwd);
        userMapper.updateUserPassword(newPwd, id);
    }

    @Override
    public User detailUser(String id) {
        User user = userMapper.selectUserById(id);
        return user;
    }
}
