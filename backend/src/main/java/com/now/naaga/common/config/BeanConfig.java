package com.now.naaga.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.gameresult.domain.gamescore.FailResultScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.ResultScoreCalculator;
import com.now.naaga.gameresult.domain.gamescore.ResultScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.SuccessResultScorePolicy;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

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
}
