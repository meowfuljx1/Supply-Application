package ru.meowful.severstal_task3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.meowful.severstal_task3.model.Price;

import java.time.LocalDate;

@Repository
public interface PriceRepository extends JpaRepository <Price, Long> {

    @Query(value = """
    SELECT COUNT(*) > 0
    FROM price
    WHERE
	    product_id = :product_id AND
        supplier_id = :supplier_id AND
	    ((:fromDate BETWEEN from_date AND to_date) OR (:toDate BETWEEN from_date AND to_date))
    """, nativeQuery = true)
    int countPricesOfPeriod(@Param("fromDate") LocalDate fromDate,
                                  @Param("toDate") LocalDate toDate,
                                  @Param("product_id") long product_id,
                                  @Param("supplier_id") long supplier_id);
}
