package ru.eddyz.adminpanel.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.adminpanel.domain.entities.HistoryFilePath;

import java.util.List;

@Repository
public interface HistoryFilePathsRepository extends JpaRepository<HistoryFilePath, Long> {

    List<HistoryFilePath> findByPath(String path);
}
