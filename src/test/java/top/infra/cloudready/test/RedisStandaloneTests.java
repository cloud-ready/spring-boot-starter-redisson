package top.infra.cloudready.test;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import top.infra.test.containers.GenericContainerInitializer;
import top.infra.test.containers.InitializerCallbacks;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = RedissonTestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = GenericContainerInitializer.class)
public class RedisStandaloneTests {

  @ClassRule
  public static final GenericContainer container = new GenericContainer("redis:3.0.2")
      .withExposedPorts(6379);

  static {
    GenericContainerInitializer.onInitialize(container, InitializerCallbacks.SPRING_DATA_REDIS);
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
