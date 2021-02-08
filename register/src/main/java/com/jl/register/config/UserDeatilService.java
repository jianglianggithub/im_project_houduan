package com.jl.register.config;


import com.jl.common.entity.UserInfo;
import com.jl.common.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDeatilService implements UserDetailsService {

    //注入 springSecurity 的 客户端细节 处理的 服务类
    //如果你是 缓存 客户端的认证细节那么注入的就是 inMemory
    //繁殖 jdbcClientDetailService
    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    UserInfoService userInfoService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> role_admin = AuthorityUtils.createAuthorityList();

        UserInfo userInfo = userInfoService.getUserByUserName(username);
        if (userInfo == null) {
            return null;
        }
        return new UserExt(userInfo.getId(),username,"{MD5}"+ userInfo.getPwd(),role_admin,"啊啊","你是");    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        //如果为null 代表是 第一次进来  那么就是来验证 客户端Id 和密码是否正确
//        if (authentication==null){
//            //获取客户端 账号密码细节
//            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
//            if (clientDetails==null){
//                //没有查到该客户端id对应的 封装类信息 则
//                return null;
//            }
//            //获取客户端的密码
//            String clientSecret = clientDetails.getClientSecret();
//            System.out.println(clientSecret+"=================");
//            return new UserExt(username,"{bcrypt}"+clientSecret,clientDetails.getAuthorities(),null,null);
//        }
//        //如果不为null代表不是第一次进来  即通过了 客户端id密码 认真   开始认证用户的 账号,密码是否正确
//        return new UserExt(username,new BCryptPasswordEncoder().encode("123456"),null,"啊啊","你是");
//    }



}
