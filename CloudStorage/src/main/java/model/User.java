package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="email")
    @NotNull(message="email field should be not empty")
    @Email
    private String email;

    @Column(name="password")
    @NotNull(message = "password field should not be empty")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User()
    {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public int getId() { return id;}

    public void setId(int id) {this.id = id;}

    public @Email String getEmail() {return email;}

    public void setEmail(@Email String email) { this.email = email;}

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
