package ru.meowful.severstal_task3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.meowful.severstal_task3.model.ProductType;
import ru.meowful.severstal_task3.model.Supply;
import ru.meowful.severstal_task3.model.SupplyReport;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {
	@Query(value = """
        SELECT
            username AS Supplier,
            product_type AS Product,
            SUM(weight) AS Total_weight ,
            SUM(weight * price) AS Total_cost
        FROM supplier
	        JOIN supply USING (supplier_id)
	        JOIN supply_content USING(supply_id)
            JOIN product USING(product_id)
            JOIN price USING(product_id)
        WHERE
	        (supply_date BETWEEN price.from_date AND price.to_Date) AND
            (supplier.supplier_id = price.supplier_id) AND
	        supply_date BETWEEN :fromDate AND :toDate
        GROUP BY username, product_type;
    """, nativeQuery = true)
	List<SupplyReport> getSupplyReport(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query(value = """
		SELECT product_type AS product
		FROM product JOIN price USING(product_id)
		WHERE
			price.supplier_id = :supplier_id AND
    		:currentDate BETWEEN price.from_date AND price.to_date
		GROUP BY product_type;
	""", nativeQuery = true)
	List<ProductType> getProductsWhichPriceExists(@Param("supplier_id") Long supplier_id, @Param("currentDate") LocalDate currentDate);
}
