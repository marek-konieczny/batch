package com.example.batch;

import com.example.batch.model.UserInput;
import com.example.batch.model.UserOutput;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<UserInput, UserOutput> {

    @Override
    public UserOutput process(UserInput item) {
        return new UserOutput(item.getFirstName(), item.getLastName(), item.getBirthDate().getYear());
    }
}
