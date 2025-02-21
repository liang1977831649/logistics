package com.logistics.confg;

import com.logistics.filter.AuthenticationFilter;
import com.logistics.handler.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity  // 开启spring sercurity支持
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    //配置加密方式
    @Bean
    public PasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //@Bean
    ////手动创建一个AuthenticationManager用于处理密码校验
    //public AuthenticationManager authenticationManager(UserDetailsManager manager, PasswordEncoder encoder) {
    //    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //    provider.setUserDetailsService(manager);
    //    provider.setPasswordEncoder(encoder);
    //    return new ProviderManager(provider);
    //}
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        //账号密码校验实现的类
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);//实现userDetailsService的类
        authenticationProvider.setPasswordEncoder(passwordEncoder);//加密方式
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //授权配置
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                //设置哪些路径可以直接访问，不需认证
                .requestMatchers("/login","/register","/getPhone/*").permitAll()
                //其他的路径都需要认证
                .anyRequest().authenticated());


        //退出登录成功处理器
        //httpSecurity.logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));

        //授权失败处理配置
        //httpSecurity.exceptionHandling(exceptionHandling ->
        //        exceptionHandling.accessDeniedHandler(accessDeniedHandler));

        //禁用csrf
        httpSecurity.csrf(csrf -> csrf.disable());

        //设置自定义的UserDetailService，如果自定了UserDetailService，则需要设置这个
        //httpSecurity.userDetailsService(userDetailsService);

        //授权失败处理配置
        httpSecurity.exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedHandler(myAccessDeniedHandler));

        //处理跨域
        // 解决跨域问题
        httpSecurity.cors(withDefaults());

        //配置过滤器的顺序
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
