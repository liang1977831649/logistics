package com.logistics.server;

import com.logistics.entity.Admin;
import com.logistics.entity.PageBean;

public interface AdminServer {
    Admin getAdminByIdNoPassword(String id );

    void updatePasswordByAdminId(String oldPwd, String newPwd, String id);

    void updateAdmin(Admin admin);

    String selectAdminPhone(String areaId);

    PageBean<Admin> getAdminList(Integer pageNum, Integer pageSize, String name, String id);

    void addAdmin(Admin admin);

    void updateAdminByManager(Admin admin);
}
