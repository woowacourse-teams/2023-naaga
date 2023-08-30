package com.now.naaga.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.gameresult.domain.gamescore.FailGameScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.GameScoreCalculator;
import com.now.naaga.gameresult.domain.gamescore.GameScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.SuccessGameScorePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    public SuccessGameScorePolicy successGameScorePolicy() {
        return new SuccessGameScorePolicy();
    }

    @Bean
    public FailGameScorePolicy failGameScorePolicy() {
        return new FailGameScorePolicy();
    }

    @Bean
    public GameScoreCalculator gameScoreCalculator() {
        final List<GameScorePolicy> gameScorePolicies = List.of(
                successGameScorePolicy(),
                failGameScorePolicy()
        );
        return new GameScoreCalculator(gameScorePolicies);
    }
}
