package upb.ida;

//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@EnableNeo4jRepositories("upb.ida.repository")
//@EntityScan(basePackages = "upb.ida.domains")
//@EnableTransactionManagement
@EnableAutoConfiguration
@ImportResource({
	"classpath:config/bean-config.xml",
	"classpath:config/startup-config.xml"
})
public class Application extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
