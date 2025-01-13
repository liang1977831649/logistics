package com.logistics;

import com.logistics.utils.DateCreateUtil;
import org.junit.jupiter.api.Test;

public class TestOther {
    @Test
    public void date(){
        String s = DateCreateUtil.dateFileName();
        System.out.println(s);
    }
}
