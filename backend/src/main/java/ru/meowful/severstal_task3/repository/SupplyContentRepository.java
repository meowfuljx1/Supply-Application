package ru.meowful.severstal_task3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.meowful.severstal_task3.model.SupplyContent;

@Repository
public interface SupplyContentRepository extends JpaRepository<SupplyContent, Long> {
}
