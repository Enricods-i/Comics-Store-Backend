package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import im.enricods.ComicsStore.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {
    
}//UserRepository
