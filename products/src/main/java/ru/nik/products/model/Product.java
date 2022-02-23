package ru.nik.products.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Data
public class Product {

    @Id
    @GeneratedValue
    public Long id;

    @NonNull
    public String name;

    @NonNull
    public BigDecimal price;

    //procent of discount
    public Integer discount;

    public String info;

}
