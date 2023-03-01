package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.user.accessManagment.Role;
import com.pasinski.sl.backend.user.bodyinfo.BodyInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", nullable = false)
    private Long idUser;
    private String name;
    private String email;
    private String password;
    private String image = ApplicationConstants.DEFAULT_USER_IMAGE_NAME;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "id_user"),
            inverseJoinColumns = @JoinColumn(
                    name = "id_role"))
    private Collection<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private BodyInfo bodyInfo;

    public AppUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = getRoles().stream().collect(HashSet::new, HashSet::add, HashSet::addAll);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return idUser.equals(appUser.idUser) && name.equals(appUser.name) && email.equals(appUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, name, email);
    }
}
