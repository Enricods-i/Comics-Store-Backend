package im.enricods.ComicsStore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {
    
    User findByEmail(String email);

    List<User> findByFirstnameAndLastnameAllIgnoreCase(String firstName, String lastName);

}//UserRepository
