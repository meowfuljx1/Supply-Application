package ru.meowful.severstal_task3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.meowful.severstal_task3.DTO.PriceDTO;
import ru.meowful.severstal_task3.DTO.ReportDTO;
import ru.meowful.severstal_task3.DTO.SupplyDTO;
import ru.meowful.severstal_task3.service.SupplyService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/supply-app")
@CrossOrigin(origins = "${FRONTEND_URL}")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;

    @GetMapping("/getProductTypes")
    public ResponseEntity<String[]> getPeriod() {
        String[] productTypes = supplyService.getProductTypes();
        return new ResponseEntity<>(productTypes, HttpStatus.OK);
    }

    @PostMapping("/savePrices")
    public ResponseEntity<String> savePrices(@RequestBody PriceDTO[] pricesDTO) {
        String res = supplyService.savePrices(pricesDTO);
        return res.equals("Указанные цены были сохранены") ?
                new ResponseEntity<>(res, HttpStatus.CREATED) :
                new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/saveSupply")
    public ResponseEntity<String> saveSupply(@RequestBody List<SupplyDTO> supplyDTOList) {
        String res = supplyService.saveSupply(supplyDTOList);
        if (res.equals("Поставка успешно создана!"))
            return new ResponseEntity<>(res, HttpStatus.CREATED);
         else
             return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/createReport")
    public ResponseEntity<ReportDTO> createReport(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        ReportDTO reportDTO = supplyService.createReport(fromDate, toDate);
        return reportDTO != null ?
                new ResponseEntity<>(reportDTO, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
