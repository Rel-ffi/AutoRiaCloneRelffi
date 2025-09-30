package org.autoriaclonebackend.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BrandAndModelResponse {
    private String brand;
    private String model;
}
