package top.infra.cloudready.boot;

import lombok.extern.slf4j.Slf4j;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass({Redisson.class, RedisTemplate.class})
@Configuration
@Slf4j
public class RedissonAutoConfiguration {

  private RedisProperties redisProperties;

  @Value("${spring.redis.host}:${spring.redis.port}")
  private String address;
  @Value("${spring.redis.database:0}")
  private int database;

  @Autowired(required = false)
  public void setRedisProperties(final RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  public String getRedisAddress() {
    //should add schema prefix "redis://" or "rediss://" for redisson version >= 3.7.0
    final String schema = this.redisProperties.isSsl() ? "rediss://" : "redis://";
    final String redisAddress = schema + this.redisProperties.getHost() + ":" + this.redisProperties.getPort();
    log.info("redisAddress: {}", redisAddress);
    return redisAddress;
  }

  private SentinelServersConfig sentinelServers(final Config config) {
    final int redisDatabase = this.redisProperties.getDatabase();
    final String redisPassword = this.redisProperties.getPassword();
    final String redisSentinelMaster = this.redisProperties.getSentinel().getMaster();
    final String[] redisSentinelNodes = this.redisProperties.getSentinel().getNodes().split(",");

    SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
    sentinelServersConfig.setMasterName(redisSentinelMaster);
    sentinelServersConfig.addSentinelAddress(redisSentinelNodes);
    sentinelServersConfig.setDatabase(redisDatabase);
    if (redisPassword != null) { //StringUtils.isNotBlank(redisPassword)
      sentinelServersConfig.setPassword(redisPassword);
    }

    return sentinelServersConfig;
  }

  private SingleServerConfig singleServer(final Config config) {
    final SingleServerConfig singleServerConfig = config.useSingleServer();

    final String redisAddress = this.getRedisAddress();
    final int redisDatabase = this.redisProperties.getDatabase();
    final String redisPassword = this.redisProperties.getPassword();

    singleServerConfig.setAddress(redisAddress).setDatabase(redisDatabase);
    if (redisPassword != null) { //StringUtils.isNotBlank(redisPassword)
      singleServerConfig.setPassword(redisPassword);
    }

    return singleServerConfig;
  }

  @Bean
  public RedissonClient redissonClient() {
    final Config config = new Config();

    final boolean sentinel = this.redisProperties.getSentinel() != null;
    if (sentinel) {
      this.sentinelServers(config);
    } else {
      this.singleServer(config);
    }

    return Redisson.create(config);
  }
}
