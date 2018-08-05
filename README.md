# spring-boot-starter-redisson

[![Sonar](https://sonarcloud.io/api/project_badges/measure?project=top.infra%3Aspring-boot-starter-redisson&metric=alert_status)](https://sonarcloud.io/dashboard?id=top.infra%3Aspring-boot-starter-redisson)  
[Maven Site (github.io)](https://cloud-ready.github.io/cloud-ready/snapshot/spring-boot-starter-redisson/index.html)  
[Maven site (infra.top)](https://maven-site.infra.top/cloud-ready/snapshot/staging/spring-boot-starter-redisson/index.html)  
[Source Repository](https://github.com/cloud-ready/spring-boot-starter-redisson/tree/develop)  
[![Build Status](https://travis-ci.org/cloud-ready/spring-boot-starter-redisson.svg?branch=develop)travis-ci](https://travis-ci.org/cloud-ready/spring-boot-starter-redisson)  
[![Build status](https://ci.appveyor.com/api/projects/status/any0kvwcxs5b6s8c?svg=true)(appveyor)](https://ci.appveyor.com/project/chshawkn/spring-boot-starter-redisson)    


spring-boot-starter-redisson

### Usage:

Just put it into classpath.  

Maven:
```xml
<dependency>
    <groupId>top.infra</groupId>
    <artifactId>spring-boot-starter-redisson</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

```java
import org.redisson.api.RedissonClient;

@SpringBootApplication
public class SomeSpringBootApplication {

  @Autowired
  private RedissonClient redissonClient;
  
  public static void main(final String... args) {
    SpringApplication.run(SomeSpringBootApplication.class, args);
  }
}
```
