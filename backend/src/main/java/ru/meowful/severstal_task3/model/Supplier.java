package ru.meowful.severstal_task3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@ToString
@Setter
@Getter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;
    @Column(unique = true)
    private String username;
    private String password;
    @OneToMany(mappedBy = "supplier")
    private List<Supply> supplies;
    @OneToMany(mappedBy = "supplier")
    private List<Price> prices;
}
