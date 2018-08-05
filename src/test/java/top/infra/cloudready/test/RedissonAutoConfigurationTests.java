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
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringBootTest(classes = RedissonTestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = RedissonAutoConfigurationTests.Initializer.class)
public class RedissonAutoConfigurationTests {

  @ClassRule
  public static GenericContainer redis = new GenericContainer("redis:3.0.2")
      .withExposedPorts(6379);

  public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
      // spring-boot 1.5.x
      EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
          "spring.redis.host=" + redis.getContainerIpAddress(),
          "spring.redis.port=" + redis.getMappedPort(6379)
      );
      // spring-boot 2.0.x
//      TestPropertyValues values = TestPropertyValues.of(
//          "spring.redis.host=" + redis.getContainerIpAddress(),
//          "spring.redis.port=" + redis.getMappedPort(6379)
//      );
//      values.applyTo(configurableApplicationContext);
    }
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
