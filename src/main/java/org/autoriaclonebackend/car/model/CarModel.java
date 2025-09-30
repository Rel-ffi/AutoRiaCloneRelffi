package org.autoriaclonebackend.car.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "car_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @ManyToOne
    @JoinColumn(name = "brand_id")
    private CarBrand brand;
}