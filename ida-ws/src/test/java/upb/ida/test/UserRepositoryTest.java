package upb.ida.test;

import org.apache.jena.query.ResultSetFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.constant.IDALiteral;
import upb.ida.dao.UserRepository;
import upb.ida.domains.User;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private static final String expectedUsersList = "{\n" +
			"  \"head\": {\n" +
			"    \"vars\": [ \"subject\" , \"predicate\" , \"object\" ]\n" +
			"  } ,\n" +
			"  \"results\": {\n" +
			"    \"bindings\": [\n" +
			"      {\n" +
			"        \"subject\": { \"type\": \"uri\" , \"value\": \"http://userdata/#john@test.com\" } ,\n" +
			"        \"predicate\": { \"type\": \"uri\" , \"value\": \"http://www.w3.org/2001/vcard-rdf/3.0#userrole\" } ,\n" +
			"        \"object\": { \"type\": \"literal\" , \"value\": \"USER\" }\n" +
			"      } ,\n" +
			"      {\n" +
			"        \"subject\": { \"type\": \"uri\" , \"value\": \"http://userdata/#john@test.com\" } ,\n" +
			"        \"predicate\": { \"type\": \"uri\" , \"value\": \"http://www.w3.org/2001/vcard-rdf/3.0#username\" } ,\n" +
			"        \"object\": { \"type\": \"literal\" , \"value\": \"john@test.com\" }\n" +
			"      } ,\n" +
			"      {\n" +
			"        \"subject\": { \"type\": \"uri\" , \"value\": \"http://userdata/#john@test.com\" } ,\n" +
			"        \"predicate\": { \"type\": \"uri\" , \"value\": \"http://www.w3.org/2001/vcard-rdf/3.0#password\" } ,\n" +
			"        \"object\": { \"type\": \"literal\" , \"value\": \"$2a$12$HUk8e05YP9qyHgM201PlTONnQZwR6dN9cDWgHOY.n5egGXKhxYx72\" }\n" +
			"      } ,\n" +
			"      {\n" +
			"        \"subject\": { \"type\": \"uri\" , \"value\": \"http://userdata/#john@test.com\" } ,\n" +
			"        \"predicate\": { \"type\": \"uri\" , \"value\": \"http://www.w3.org/2001/vcard-rdf/3.0#lastname\" } ,\n" +
			"        \"object\": { \"type\": \"literal\" , \"value\": \"Doe\" }\n" +
			"      } ,\n" +
			"      {\n" +
			"        \"subject\": { \"type\": \"uri\" , \"value\": \"http://userdata/#john@test.com\" } ,\n" +
			"        \"predicate\": { \"type\": \"uri\" , \"value\": \"http://www.w3.org/2001/vcard-rdf/3.0#firstname\" } ,\n" +
			"        \"object\": { \"type\": \"literal\" , \"value\": \"John\" }\n" +
			"      }\n" +
			"    ]\n" +
			"  }\n" +
			"}\n";

	@Test
	public void getUsersListTestPos(){
		String actualUserList = userRepository.listAllUsers("test-user-data");
		assertEquals(expectedUsersList, actualUserList);
	}

	@Test
	public void getUsersListTestNeg(){
		String actualUserList = userRepository.listAllUsers("test-user-data");
		assertNotEquals("", actualUserList);
	}

	@Test
	public void getUserByNameTestPos(){
		String actualuserName = userRepository.getUserByUsername("test-user-data", "john@test.com").getUsername();
		String expectedUserName = "john@test.com";
		assertEquals(expectedUserName, actualuserName);
	}

	@Test
	public void getUserByNameTestNeg(){
		String actualuserName = userRepository.getUserByUsername("test-user-data", "john@test.com").getUsername();
		String expectedUserName = "john.doe@test.com";
		assertNotEquals(expectedUserName, actualuserName);
	}

	@Test
	public void updateUserTestPos(){
		User user = new User("test.user@test.com", "Test@123", "Test", "User", "USER");
		String actualResult = userRepository.updateUser("test-user-data", user, "INSERT DATA", true);
		assertEquals(IDALiteral.RESP_PASS_ROUTINE, actualResult);
	}
}
