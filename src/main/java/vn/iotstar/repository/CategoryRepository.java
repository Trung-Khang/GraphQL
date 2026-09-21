package vn.iotstar.repository;
import vn.iotstar.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Override @EntityGraph(attributePaths = "users") List<Category> findAll();
    @Override @EntityGraph(attributePaths = "users") Optional<Category> findById(Long id);
    boolean existsByNameIgnoreCase(String name);
}
