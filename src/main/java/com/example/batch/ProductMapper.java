package com.example.batch;

import com.example.batch.model.Product;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.Arrays;

public class ProductMapper<T> implements FieldSetMapper<Product> {

    @Override
    public Product mapFieldSet(FieldSet fieldSet) throws BindException {
        String[] values = fieldSet.getValues();

        int id = fieldSet.readInt("id");


        return Product.builder()
                .id(Integer.valueOf(values[0]))
                .price(Integer.valueOf(values[1]))
                .quantity(Integer.valueOf(values[2]))
                .build();
    }
}
