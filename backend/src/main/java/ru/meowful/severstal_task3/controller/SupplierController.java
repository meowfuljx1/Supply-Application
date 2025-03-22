package ru.meowful.severstal_task3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.meowful.severstal_task3.DTO.SupplierDTO;
import ru.meowful.severstal_task3.service.SupplierService;

@RestController
@RequestMapping("/supply-app")
@CrossOrigin(origins = "${FRONTEND_URL}") // разрешить запросы с этого хоста
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping("/signIn")
    public ResponseEntity<String[]> signIn(@RequestParam("username") String username, @RequestParam("password") String password) {
        boolean res = supplierService.signIn(username, password);
        return res ?
                new ResponseEntity<>(new String[]{username, "Login successful"}, HttpStatus.OK) :
                new ResponseEntity<>(new String[]{"Invalid login or password"}, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/signUp")
    public ResponseEntity<String[]> signUp(@RequestBody SupplierDTO supplierDTO) {
        boolean res = supplierService.signUp(supplierDTO);
        return res ?
                new ResponseEntity<>(new String[]{supplierDTO.getUsername(), "Registration successful"}, HttpStatus.CREATED) :
                new ResponseEntity<>(new String[]{"User with this username already exists"}, HttpStatus.CONFLICT);
    }
}
