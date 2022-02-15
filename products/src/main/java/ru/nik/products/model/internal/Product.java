package ru.nik.products.model.internal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document
public class Product {
    @Id
    private String uuid;
    private String title;
    private String value;
    private BigDecimal price = BigDecimal.ZERO;
}
