package sd.a2.assignment2.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.a2.assignment2.domain.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

//    @Query("select u from User u where u.username = ?1")
//    Optional<User> findByUsername(String username);

}
