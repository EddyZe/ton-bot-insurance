package ru.eddyz.telegrambot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.telegrambot.domain.entities.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

}
