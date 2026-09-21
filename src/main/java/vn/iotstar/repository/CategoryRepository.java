package vn.iotstar.repository;
import vn.iotstar.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Long> { boolean existsByNameIgnoreCase(String name); }
