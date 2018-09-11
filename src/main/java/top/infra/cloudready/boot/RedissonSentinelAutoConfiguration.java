package top.infra.cloudready.boot;

import lombok.extern.slf4j.Slf4j;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass({Redisson.class, RedisTemplate.class})
@ConditionalOnProperty(name = {"spring.redis.sentinel.master", "spring.redis.sentinel.nodes"})
@Configuration
@Slf4j
public class RedissonSentinelAutoConfiguration {

    private RedisProperties redisProperties;

    @Autowired(required = false)
    public void setRedisProperties(final RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    public String[] getRedisSentinelNodes() {
        //should add schema prefix "redis://" or "rediss://" for redisson version >= 3.7.0
        final String schema = this.redisProperties.isSsl() ? "rediss://" : "redis://";
        final String[] redisSentinelNodes = this.redisProperties.getSentinel().getNodes() //
            .stream() //
            .map(node -> schema + node) //
            .toArray(String[]::new);
        log.info("redisNodes: {}", (Object) redisSentinelNodes);
        return redisSentinelNodes;
    }

    private SentinelServersConfig sentinelServers(final Config config) {
        final int redisDatabase = this.redisProperties.getDatabase();
        final String redisPassword = this.redisProperties.getPassword();
        final String redisSentinelMaster = this.redisProperties.getSentinel().getMaster();
        final String[] redisSentinelNodes = this.getRedisSentinelNodes();

        SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
        sentinelServersConfig.setMasterName(redisSentinelMaster);
        sentinelServersConfig.addSentinelAddress(redisSentinelNodes);
        sentinelServersConfig.setDatabase(redisDatabase);
        if (redisPassword != null) { //StringUtils.isNotBlank(redisPassword)
            sentinelServersConfig.setPassword(redisPassword);
        }

        return sentinelServersConfig;
    }

    @Bean
    public RedissonClient redissonClient() {
        final Config config = new Config();
        this.sentinelServers(config);
        return Redisson.create(config);
    }
}
