package com.logistics.handler;

import com.logistics.utils.ThreadLocalUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;

@Component("ex")
public class CustomPermissionVerification {
    public boolean verificationHandler(Integer role){
        Integer roleUser =(Integer)((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        System.out.println("roleUser="+roleUser+"++++"+role);
        return Objects.equals(roleUser, role);
    }
}
