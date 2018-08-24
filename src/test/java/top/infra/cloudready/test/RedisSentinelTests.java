package top.infra.cloudready.test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.net.InetAddress;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = RedissonTestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RedisSentinelTests {

  static final String REDIS_IMAGE = "cloudready/redis:3.0.6";

  public static String getLocalHostQuietly() {
    String localAddress;
    try {
      localAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception ex) {
      log.info("getLocalHostQuietly", "cant resolve localhost address", ex);
      localAddress = "localhost";
    }
    return localAddress;
  }

  private static final String LOCALHOST_STR = getLocalHostQuietly();

  @ClassRule
  public static final GenericContainer redisMaster = new GenericContainer(REDIS_IMAGE)
      .withCommand("redis-server", "--port", "6379")
      .withExposedPorts(6379)
      .withExtraHost("redis-sentinel", LOCALHOST_STR)
      .withExtraHost("redis-slave", LOCALHOST_STR);

  @ClassRule
  public static final GenericContainer redisSentinel = new GenericContainer(REDIS_IMAGE)
      .withCommand("redis-server", "/etc/redis/sentinel.conf", "--sentinel")
      .withEnv("SENTINEL_MASTER_HOSTORIP", LOCALHOST_STR)
      .withEnv("SENTINEL_MASTER_NAME", "mymaster")
      .withEnv("SENTINEL_MASTER_PORT", "6379")
      .withEnv("SENTINEL_PORT", "26379")
      .withExposedPorts(26379)
      .withExtraHost("redis-master", LOCALHOST_STR)
      .withExtraHost("redis-slave", LOCALHOST_STR);

  @ClassRule
  public static final GenericContainer redisSlave = new GenericContainer(REDIS_IMAGE)
      .withCommand("redis-server", "--slaveof", LOCALHOST_STR, "6379", "--port", "6381")
      .withExposedPorts(6381)
      .withExtraHost("redis-master", LOCALHOST_STR)
      .withExtraHost("redis-sentinel", LOCALHOST_STR);

  static {
    log.info("LOCALHOST_STR: {}", LOCALHOST_STR);
    redisMaster.setPortBindings(newArrayList("6379:6379"));
    redisSentinel.setPortBindings(newArrayList("26379:26379"));
    redisSlave.setPortBindings(newArrayList("6381:6381"));

    System.setProperty("spring.redis.sentinel.master", "mymaster");
    System.setProperty("spring.redis.sentinel.nodes", "127.0.0.1:26379");
  }

  @Autowired
  private RedissonClient redissonClient;

  @Test
  public void testRedisson() {
    final RMap<String, String> map = this.redissonClient.getMap("map");

    final String key = "key";
    final String value = "value";

    map.put(key, value);
    assertEquals(value, map.get(key));
    map.remove(key);
  }
}
