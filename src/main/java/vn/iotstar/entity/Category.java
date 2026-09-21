package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uk_category_name", columnNames = "name"))
@Getter @Setter @NoArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String name;
    @Column(length = 1000) private String images;
    @ManyToMany @JoinTable(name = "category_users", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();
    @OneToMany(mappedBy = "category") private Set<Product> products = new LinkedHashSet<>();
    @Override public boolean equals(Object o) { return o instanceof Category c && id != null && id.equals(c.id); }
    @Override public int hashCode() { return getClass().hashCode(); }
    @Override public String toString() { return "Category{id=" + id + ", name='" + name + "'}"; }
}
