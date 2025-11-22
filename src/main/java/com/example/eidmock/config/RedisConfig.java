package com.example.eidmock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.eidmock.service.SessionStore.SessionData;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, SessionData> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, SessionData> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use Jackson JSON serializer for values
        Jackson2JsonRedisSerializer<SessionData> serializer = new Jackson2JsonRedisSerializer<>(SessionData.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
