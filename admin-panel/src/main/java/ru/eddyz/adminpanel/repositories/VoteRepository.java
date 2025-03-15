package ru.eddyz.adminpanel.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpanel.domain.entities.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

}
