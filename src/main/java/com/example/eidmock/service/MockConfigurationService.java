package com.example.eidmock.service;

import com.example.eidmock.dto.MockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class MockConfigurationService {

    private static final String CONFIG_KEY_PREFIX = "mock:config:";
    private static final int CONFIG_TTL_HOURS = 24;

    @Autowired
    private RedisTemplate<String, MockConfiguration> mockConfigurationRedisTemplate;

    public void setConfiguration(MockConfiguration config) {
        String key = CONFIG_KEY_PREFIX + config.getPersonalCode();
        mockConfigurationRedisTemplate.opsForValue().set(key, config, CONFIG_TTL_HOURS, TimeUnit.HOURS);
    }

    public MockConfiguration getConfiguration(String personalCode) {
        String key = CONFIG_KEY_PREFIX + personalCode;
        return mockConfigurationRedisTemplate.opsForValue().get(key);
    }

    public void deleteConfiguration(String personalCode) {
        String key = CONFIG_KEY_PREFIX + personalCode;
        mockConfigurationRedisTemplate.delete(key);
    }

    public List<MockConfiguration> listConfigurations() {
        Set<String> keys = mockConfigurationRedisTemplate.keys(CONFIG_KEY_PREFIX + "*");
        List<MockConfiguration> configs = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                MockConfiguration config = mockConfigurationRedisTemplate.opsForValue().get(key);
                if (config != null) {
                    configs.add(config);
                }
            }
        }

        return configs;
    }

    public void clearAll() {
        Set<String> keys = mockConfigurationRedisTemplate.keys(CONFIG_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            mockConfigurationRedisTemplate.delete(keys);
        }
    }
}
