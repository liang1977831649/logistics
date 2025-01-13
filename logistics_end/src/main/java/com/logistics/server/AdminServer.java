package com.logistics.server;

import com.logistics.entity.Admin;

public interface AdminServer {
    Admin getAdminByIdNoPassword(String id );

    void updatePasswordByAdminId(String oldPwd, String newPwd, String id);
}
