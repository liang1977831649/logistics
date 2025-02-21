package com.logistics.entity;

import com.alibaba.fastjson.annotation.JSONField;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class Account implements UserDetails {
    @NotNull
    @Pattern(regexp = "\\S{6,10}",groups = Register.class)
    private String id;
    @NotNull(groups = {Add.class,Update.class})
    private String name;
    @NotNull(groups = {Add.class, Login.class,Register.class})
    private String password;
    @NotNull(groups = Login.class)
    private Integer role;
    @NotNull(groups = Register.class)
    private String area;
    @NotNull(groups = {Register.class,Update.class})
    private String areaId;

    @JSONField(serialize = false)
    private List<GrantedAuthority> grantedAuthorities;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        //if(grantedAuthorities!=null){
        //    return grantedAuthorities;
        //}
        //SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.toString());
        //grantedAuthorities=new ArrayList<>();
        //grantedAuthorities.add(simpleGrantedAuthority);
        //return grantedAuthorities;
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true ;
    }


    //检验分组
    //登陆的时候，不需要检验id的长度，也不需要检验地域为空，但是角色不能为空
    public interface Login{};
    //注册的时候，必须校验Id的长度，同时地域code不能为空，角色可以为空，因为注册的只能是普通用户
    public interface Register{};

    public interface Add{}

    public interface Update{}
}
