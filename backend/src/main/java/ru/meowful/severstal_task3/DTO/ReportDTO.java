package ru.meowful.severstal_task3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.meowful.severstal_task3.model.SupplyReport;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReportDTO {
    private List<SupplyReport> rows;
    private double weightOfAllProducts;
    private double costOfAllProducts;
}
