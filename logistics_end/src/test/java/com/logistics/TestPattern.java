package com.logistics;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class TestPattern {
    @Test
    public void test1(){
        String str="sj574686";
        String regx="sj.+";
        if(Pattern.matches(regx,str)){
            System.out.println("成功");
        }
    }
    @Test
    public void test2(){
        String str="1977831649";
        String regx="\\d{11,12}";
        if(Pattern.matches(regx,str)){
            System.out.println("成功");
        }
    }
}
