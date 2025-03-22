package ru.meowful.severstal_task3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.meowful.severstal_task3.model.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByUsername(String username);
    Optional<Supplier> findByUsername(String username);
}
