package com.example.userservice.util.security;
import com.example.userservice.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class UserPrincipal extends AbstractUserPrincipal implements Serializable {

    private static final long serialVersionUID = 6960173949433045836L;

    private User user;

    /**
     * @param authorities
     * @param user
     */
    public UserPrincipal(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);

        this.user = user;
    }

    public String getUserId() {
        return this.user.getUserId();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }
}

