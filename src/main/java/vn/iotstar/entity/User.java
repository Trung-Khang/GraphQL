package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String fullname;
    @Column(nullable = false, length = 254) private String email;
    @Column(nullable = false, length = 100) private String password;
    @Column(length = 30) private String phone;
    @ManyToMany(mappedBy = "users") private Set<Category> categories = new LinkedHashSet<>();
    @OneToMany(mappedBy = "user") private Set<Product> products = new LinkedHashSet<>();
    @Override public boolean equals(Object o) { return o instanceof User u && id != null && id.equals(u.id); }
    @Override public int hashCode() { return getClass().hashCode(); }
    @Override public String toString() { return "User{id=" + id + ", email='" + email + "'}"; }
}
