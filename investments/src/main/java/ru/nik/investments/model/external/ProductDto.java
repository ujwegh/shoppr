package ru.nik.investments.model.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private String uuid;
    private String title;
    private String value;
    private Double price;
}
