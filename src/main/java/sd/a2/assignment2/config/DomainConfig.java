package sd.a2.assignment2.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("sd.a2.assignment2.domain")
@EnableJpaRepositories("sd.a2.assignment2.repos")
public class DomainConfig {
}
