package vn.iotstar.repository;
import vn.iotstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    @Override @EntityGraph(attributePaths = "categories") List<User> findAll();
    @Override @EntityGraph(attributePaths = "categories") Optional<User> findById(Long id);
    Optional<User> findByEmailIgnoreCase(String email);
}
