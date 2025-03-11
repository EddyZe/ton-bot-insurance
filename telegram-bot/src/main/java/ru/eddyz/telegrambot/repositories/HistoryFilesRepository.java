package ru.eddyz.telegrambot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;

import java.util.List;

@Repository
public interface HistoryFilesRepository extends JpaRepository<HistoryFile, Long> {

    List<HistoryFile> findByTelegramFileGroup(String telegramFileId);
}
