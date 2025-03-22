package ru.meowful.severstal_task3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.meowful.severstal_task3.model.Product;
import ru.meowful.severstal_task3.model.ProductType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByType(ProductType type);
}
