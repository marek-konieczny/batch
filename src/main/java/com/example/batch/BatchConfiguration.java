package com.example.batch;

import com.example.batch.model.UserInput;
import com.example.batch.model.UserOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ChunkListener chunkListener;

    BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.chunkListener = new CustomChunkLister();
    }

    @Bean
    FlatFileItemReader<UserInput> reader() {
        FlatFileItemReader<UserInput> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("input.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("firstName", "lastName", "birthDate");

        BeanWrapperFieldSetMapper<UserInput> mapper = new BeanWrapperFieldSetMapperCustom<>();
        mapper.setTargetType(UserInput.class);

        mapper.setConversionService(new DefaultConversionService());

        DefaultLineMapper<UserInput> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);

        return reader;
    }


    @Bean
    FlatFileItemWriter<UserOutput> writer() {
        BeanWrapperFieldExtractor<UserOutput> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"firstName", "lastName", "birthYear"});

        DelimitedLineAggregator<UserOutput> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<UserOutput> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    ProductProcessor processor() {
        return new ProductProcessor();
    }

    @Bean
    Step priceChange(
            ItemReader<UserInput> reader,
            ItemProcessor<UserInput, UserOutput> processor,
            ItemWriter<UserOutput> writer) {

        return stepBuilderFactory.get("priceChange")
                .<UserInput, UserOutput>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(chunkListener)
                .build();

    }

    @Bean
    Job changePriceJob(Step priceChange) {
        return jobBuilderFactory.get("changePriceJob")
                .incrementer(new RunIdIncrementer())
                .flow(priceChange)
                .end()
                .build();
    }

}
