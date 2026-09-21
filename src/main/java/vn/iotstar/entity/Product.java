package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity @Table(name = "products") @Getter @Setter @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "description", length = 2000) private String description;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @Override public boolean equals(Object o) { return o instanceof Product p && id != null && id.equals(p.id); }
    @Override public int hashCode() { return getClass().hashCode(); }
    @Override public String toString() { return "Product{id=" + id + ", title='" + title + "'}"; }
}
