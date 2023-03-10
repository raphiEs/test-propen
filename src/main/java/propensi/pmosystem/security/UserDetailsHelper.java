package propensi.pmosystem.security;

import java.security.Principal;
import java.util.Collection;

import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserDetailsHelper {
    public HttpServletRequest req;
    
    public String getUsername(HttpServletRequest req){
        Principal auth = req.getUserPrincipal();
        UserDetails user = (UserDetails) ((Authentication) auth).getPrincipal();
        String name = user.getUsername();
        return name;
    }

    public GrantedAuthority getRole(HttpServletRequest req){
        Principal auth = req.getUserPrincipal();
        UserDetails user = (UserDetails) ((Authentication) auth).getPrincipal();
        Collection<?> roles = user.getAuthorities();
        // Collection<? extends GrantedAuthority> roles = ((Authentication) user).getAuthorities();
        GrantedAuthority role = (GrantedAuthority) roles.iterator().next();
        return role;
    }
}
