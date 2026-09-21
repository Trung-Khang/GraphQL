package vn.iotstar.repository;
import vn.iotstar.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Override @EntityGraph(attributePaths = {"user", "category"}) List<Product> findAll();
    @Override @EntityGraph(attributePaths = {"user", "category"}) java.util.Optional<Product> findById(Long id);
    @EntityGraph(attributePaths = {"user", "category"})
    List<Product> findAllByOrderByPriceAsc();
    @EntityGraph(attributePaths = {"user", "category"})
    List<Product> findByCategoryIdOrderByPriceAsc(Long categoryId);
    boolean existsByCategoryId(Long categoryId);
}
