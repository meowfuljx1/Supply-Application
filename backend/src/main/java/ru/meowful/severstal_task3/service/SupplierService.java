package ru.meowful.severstal_task3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.meowful.severstal_task3.DTO.SupplierDTO;
import ru.meowful.severstal_task3.model.Supplier;
import ru.meowful.severstal_task3.repository.SupplierRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public boolean signIn(String username, String password) {
        return validateSignInData(username, password);
    }

    private boolean validateSignInData(String username, String password) {
        Optional<Supplier> optional = supplierRepository.findByUsername(username);
        return optional.isPresent() && optional.get().getPassword().equals(password);
    }

    public boolean signUp(SupplierDTO supplierDTO) {
        return validateSignUpData(supplierDTO);
    }

    private boolean validateSignUpData(SupplierDTO supplierDTO) {
        boolean isUsernameExists = supplierRepository.existsByUsername(supplierDTO.getUsername());

        if (!isUsernameExists) {
            Supplier supplier = new Supplier();
            supplier.setUsername(supplierDTO.getUsername());
            supplier.setPassword(supplierDTO.getPassword());
            supplierRepository.save(supplier);
            return true;
        }
        return false;
    }
}
