package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name="Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="email")
    @NotNull
    @Email
    private String email;

    @Column(name="password")
    @NotNull
    private String password;

    //field List<File> files -> user files

    public User()
    {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() { return id;}

    public void setId(int id) {this.id = id;}

    public @Email String getEmail() {return email;}

    public void setEmail(@Email String email) { this.email = email;}

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
