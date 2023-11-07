package com.now.naaga.common.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.gameresult.domain.gamescore.FailResultScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.ResultScoreCalculator;
import com.now.naaga.gameresult.domain.gamescore.ResultScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.SuccessResultScorePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class BeanConfig {

    @Value("${cloud.aws.region.static}")
    private String clientRegion;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SuccessResultScorePolicy successGameScorePolicy() {
        return new SuccessResultScorePolicy();
    }

    @Bean
    public FailResultScorePolicy failGameScorePolicy() {
        return new FailResultScorePolicy();
    }

    @Bean
    public ResultScoreCalculator gameScoreCalculator() {
        final List<ResultScorePolicy> gameScorePolicies = List.of(
                successGameScorePolicy(),
                failGameScorePolicy()
        );
        return new ResultScoreCalculator(gameScorePolicies);
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .build();
    }
}
