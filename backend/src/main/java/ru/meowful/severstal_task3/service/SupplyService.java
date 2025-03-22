package ru.meowful.severstal_task3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.meowful.severstal_task3.DTO.PriceDTO;
import ru.meowful.severstal_task3.DTO.ReportDTO;
import ru.meowful.severstal_task3.DTO.SupplyDTO;
import ru.meowful.severstal_task3.model.SupplyReport;
import ru.meowful.severstal_task3.model.*;
import ru.meowful.severstal_task3.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final SupplierRepository supplierRepository;
    private final SupplyContentRepository supplyContentRepository;

    public ReportDTO createReport(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) return null;

        List<SupplyReport> supplyReport = supplyRepository.getSupplyReport(fromDate, toDate);

        // если данных нет
        if (supplyReport.isEmpty()) return null;

        double weightOfAllProducts = supplyReport.stream().mapToDouble(SupplyReport::getTotal_weight).sum();
        double costOfAllProducts = supplyReport.stream().mapToDouble(SupplyReport::getTotal_cost).sum();

        return new ReportDTO(supplyReport, weightOfAllProducts, costOfAllProducts);
    }

    public String saveSupply(List<SupplyDTO> supplyDTOList) {
        // отбираем только объекты с weight > 0, если таких нет, то return
        List<SupplyDTO> validData = validateSupplyData(supplyDTOList);
        if (validData == null)
            return "Неправильное оформление поставки";

        if(!isSupplyProductsUnique(validData))
            return "Имеются дубликаты, переоформите поставку";

        Supplier supplier = supplierRepository.findByUsername(validData.getFirst().getUsername()).get();

        // проверка наличия цен на все продукты
        List<ProductType> list = isPricesOnProductsExists(validData, supplier);
        if (list != null) {
            StringBuilder sb = new StringBuilder("На следующие продукты нет цен на данный период:\n");
            for (ProductType productType : list)
                sb.append(productType).append(", ");
            return sb.deleteCharAt(sb.length() - 2).toString();
        }

        Supply supply = supplyRepository.save(
                Supply.builder()
                        .supplier(supplier)
                        .date(LocalDate.now())
                        .build()
        );

        List<SupplyContent> supplyContentList = validData.stream()
                .map(DTO -> SupplyContent
                        .builder()
                        .supply(supply)
                        .product(productRepository.findByType(DTO.getProductType()))
                        .weight(DTO.getWeight())
                        .build()
                ).toList();

        supplyContentRepository.saveAll(supplyContentList);
        return "Поставка успешно создана!";
    }

    private List<SupplyDTO> validateSupplyData(List<SupplyDTO> supplyDTOList) {
        List<SupplyDTO> validData = supplyDTOList.stream()
                .filter(DTO -> DTO.getWeight() > 0)
                .toList();

        return validData.isEmpty() ? null : validData;
    }

    private boolean isSupplyProductsUnique(List<SupplyDTO> list) {
        Set<ProductType> set = new HashSet<>();
        for (SupplyDTO supplyDTO: list)
            if(!set.add(supplyDTO.getProductType()))
                return false;
        return true;
    }

    private List<ProductType> isPricesOnProductsExists(List<SupplyDTO> validData, Supplier supplier) {
        // получаем ProductType, цена на которые есть по текущей дате
        List<ProductType> productTypesWhichPriceExists = supplyRepository
                .getProductsWhichPriceExists(supplier.getSupplierId(), LocalDate.now());

        // получаем ProductType из поставки
        List<ProductType> supplyProductTypes = validData.stream()
                .map(SupplyDTO::getProductType)
                .collect(Collectors.toCollection(ArrayList::new));

        // если продуктов с ценами по этой дате нет, сразу выходим
        if (productTypesWhichPriceExists.isEmpty()) return supplyProductTypes;

        // оставшиеся элементы - продукты, на которых нет цены
        supplyProductTypes.removeAll(productTypesWhichPriceExists);

        // либо все цены есть и продукты сохраняются, либо сообщаем, какие цены отсутствуют
        return supplyProductTypes.isEmpty() ? null : supplyProductTypes;
    }

    public String[] getProductTypes() {
        return productRepository.findAll().stream()
                .map(Product::getType)
                .map(Enum::toString)
                .toArray(String[]::new);
    }

    public String savePrices(PriceDTO[] pricesDTO) {
        if (pricesDTO.length == 0 || !isDatesExists(pricesDTO[0])) return "Невалидное заполнение!";

        if (!isPricesUnique(pricesDTO)) return "Имеются дубликаты!";

        List<Price> prices = new ArrayList<>(pricesDTO.length);

        for (PriceDTO priceDTO : pricesDTO) {
            if (priceDTO.getFromDate().isAfter(priceDTO.getToDate()))
                return "Неверные даты";
            if (!isValidPrice(priceDTO))
                continue;

            Product product = productRepository.findByType(priceDTO.getProductType());
            Supplier supplier = supplierRepository.findByUsername(priceDTO.getUsername()).get();

            // запрос, проверяющий пересечения по датам
            int count = priceRepository.countPricesOfPeriod(
                    priceDTO.getFromDate(),
                    priceDTO.getToDate(),
                    product.getProductId(),
                    supplier.getSupplierId()
            );
            if (count > 0)
                return "Новые цены не были добавлены, поскольку есть пересечения с ранее созданными периодами";

            prices.add(Price.builder()
                    .fromDate(priceDTO.getFromDate())
                    .toDate(priceDTO.getToDate())
                    .price(priceDTO.getPrice())
                    .product(product)
                    .supplier(supplier)
                    .build()
            );
        }

        if (!prices.isEmpty()) {
            priceRepository.saveAll(prices);
            return "Указанные цены были сохранены";
        }

        return "Невалидное заполнение!";
    }

    private boolean isPricesUnique(PriceDTO [] pricesDTO) {
        Set<ProductType> set = new HashSet<>();
        for (PriceDTO priceDTO: pricesDTO)
            if(!set.add(priceDTO.getProductType()))
                return false;
        return true;
    }

    private boolean isValidPrice(PriceDTO priceDTO) {
        return priceDTO.getPrice() > 0;
    }

    private boolean isDatesExists(PriceDTO priceDTO) {
        return priceDTO.getFromDate() != null && priceDTO.getToDate() != null;
    }

}
