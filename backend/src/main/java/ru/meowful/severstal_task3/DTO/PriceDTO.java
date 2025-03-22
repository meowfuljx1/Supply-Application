package ru.meowful.severstal_task3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.meowful.severstal_task3.model.Product;
import ru.meowful.severstal_task3.model.ProductType;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class PriceDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private double price;
    private ProductType productType;
    private String username;
}
