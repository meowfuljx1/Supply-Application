package ru.meowful.severstal_task3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", unique = true, nullable = false)
    private ProductType type;

    @OneToMany(mappedBy = "product")
    private List<SupplyContent> contents = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Price> prices = new ArrayList<>();


}
