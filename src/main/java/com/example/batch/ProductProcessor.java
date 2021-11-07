package com.example.batch;

import com.example.batch.model.Product;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<Product, Product> {
    @Override
    public Product process(Product item) {
        return new Product(item.getId(), item.getQuantity(), (int)(item.getPrice()*1.1));
    }
}
