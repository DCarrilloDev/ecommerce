package portfolio.carrillo.ecommerce.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio.carrillo.ecommerce.users.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
