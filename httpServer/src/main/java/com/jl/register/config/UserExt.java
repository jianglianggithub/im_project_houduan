package com.jl.register.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;



@Getter
@Setter
public class UserExt extends User {
    public String userId;
    public String gongsi;
    public String address;

    public UserExt(String userId,String username, String password, Collection<? extends GrantedAuthority> authorities, String gongsi, String address) {
        super(username, password, authorities);
        this.userId = userId;
        this.gongsi=gongsi;
        this.address=address;
    }
}
