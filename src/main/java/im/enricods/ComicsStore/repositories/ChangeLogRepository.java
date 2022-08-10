package im.enricods.ComicsStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.enricods.ComicsStore.entities.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog,Long>{
    
}//ChangeLogRepository
