package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findByFirstnameOrLastnameAllIgnoreCase(String firstName, String lastName);
    
    User findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByPhoneNumber(String phoneNumber);

    List<User> findByCity(String city);

}//UserRepository
