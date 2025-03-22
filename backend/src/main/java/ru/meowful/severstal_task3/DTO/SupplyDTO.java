package ru.meowful.severstal_task3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.meowful.severstal_task3.model.ProductType;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SupplyDTO {
    private String username;
    private ProductType productType;
    private double weight;
}


