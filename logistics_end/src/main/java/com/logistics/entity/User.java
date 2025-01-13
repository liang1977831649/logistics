package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User extends Account{
    private String phone;
    private String userPic;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
    private Integer sex;

    @Override
    public String toString() {
        return "User{" +"id='" +super.getId()+'\'' +
                ", name='" +super.getName()+'\'' +
                ", areaId='" +super.getAreaId()+'\'' +
                ", role='" +super.getRole()+'\'' +
                ", password='" +super.getPassword()+'\'' +
                ", phone='" + phone + '\'' +
                ", sex='" + sex + '\'' +
                ", userPic='" + userPic + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
