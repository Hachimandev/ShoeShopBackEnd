package com.fit.shoeshopbackend.config;



import com.fit.shoeshopbackend.model.Role;
import com.fit.shoeshopbackend.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.stream.Collectors;

public class AccountDetails implements UserDetails {

    private Account Account;

    public AccountDetails(Account Account) {
        this.Account = Account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Account.getRoles().stream()
                .map(Role::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return Account.getPassword();
    }

    @Override
    public String getUsername() {
        return Account.getUsername();
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
        return true;
    }
}









