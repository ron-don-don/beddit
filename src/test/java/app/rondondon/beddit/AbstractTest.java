package app.rondondon.beddit;


import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class AbstractTest {

    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @AfterAll
    static void cleanUp(@Autowired JdbcTemplate jdbcTemplate,
                        @Autowired RedisConnectionFactory redisConnectionFactory) {
        List<String> tables = jdbcTemplate.queryForList("""
            SELECT tablename FROM pg_tables
            WHERE schemaname = 'public'
              AND tablename NOT IN ('flyway_schema_history', 'databasechangelog', 'databasechangeloglock')
            """, String.class);

        if (!tables.isEmpty()) {
            String sql = "TRUNCATE TABLE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE";
            jdbcTemplate.execute(sql);
        }

        try (var connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushDb();
        }
    }
}
