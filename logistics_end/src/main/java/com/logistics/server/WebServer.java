package com.logistics.server;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;

public interface WebServer {
    Account adminLoginServer(Account account) ;

    Account userLoginServer(Account account) ;

    Admin getAdminById(String id);

    User getUserById(String id);
}
