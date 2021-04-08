package uz.raximov.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.raximov.demo.entity.template.AbsEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User extends AbsEntity implements UserDetails {

    @Column(nullable = false, length = 50)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    private Set<Role> roles;

    @Column(unique = true, nullable = false)
    private String email;

    private String verifyCode;

    private String position;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public User(String fullName, String password, Set<Role> roles, @Email String email, String position, boolean isEnabled) {
        this.fullName = fullName;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.position = position;
        this.isEnabled = isEnabled;
    }
}
