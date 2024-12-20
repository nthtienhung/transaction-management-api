package com.iceteasoftware.wallet.util.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

public abstract class AbstractUserPrincipal extends User implements Serializable {
    private static final long serialVersionUID = 6960173949433045836L;
    public AbstractUserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

}
