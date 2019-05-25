package upb.ida;

import org.springframework.context.annotation.ImportResource;
//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


//@EnableNeo4jRepositories("upb.ida.repository")

@EnableTransactionManagement
@EnableAutoConfiguration
//@ImportResource({
//	"classpath:config/bean-config.xml",
//	"classpath:config/startup-config.xml"
//})
@SpringBootApplication
@EntityScan(basePackages = "upb.ida.domains")
public class Application extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}