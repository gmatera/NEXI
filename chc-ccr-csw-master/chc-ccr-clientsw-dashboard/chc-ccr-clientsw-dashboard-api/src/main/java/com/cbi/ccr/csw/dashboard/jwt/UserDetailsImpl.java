package com.cbi.ccr.csw.dashboard.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cbi.ccr.csw.domain.user.Users;



public class UserDetailsImpl extends Users implements UserDetails{
	private static final long serialVersionUID = -1538071846874020314L;

	public UserDetailsImpl(final Users user) {
        super(user);
	}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
    	List<SimpleGrantedAuthority> auth = new ArrayList<>();
    	
    	StringTokenizer st = new StringTokenizer(getRoles(), ",");
    	while (st.hasMoreTokens()) {
			String role = st.nextToken();
			auth.add(new SimpleGrantedAuthority(role));
		}
    	return auth;
     }
  
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsImpl that = (UserDetailsImpl) obj;
        return Objects.equals(getId(), that.getId());
    }
    
	@Override
	public boolean isEnabled() {
		return true;
	}

}
