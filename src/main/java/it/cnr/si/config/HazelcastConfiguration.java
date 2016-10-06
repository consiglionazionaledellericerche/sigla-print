package it.cnr.si.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by francesco on 28/09/16.
 */

@Configuration
public class HazelcastConfiguration {

    @Bean
    public Config config() {
        Config config = new Config();
        config.setInstanceName("sigla-print-server");
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        managementCenterConfig.setEnabled(true);
        managementCenterConfig.setUrl("http://localhost:8980");
        config.setManagementCenterConfig(managementCenterConfig);
        return config;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        HazelcastCacheManager cacheManager = new HazelcastCacheManager(hazelcastInstance);
        return cacheManager;
    }


}
