package upb.ida.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import upb.ida.Application;
import upb.ida.dao.SoldierTimeLine;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })
public class SoldierTimelineTest {
	@Autowired
	private SoldierTimeLine soldierTimeLine;

	@Test
	public void soldierDataTestPos(){
		Map<String, String> expected = new HashMap<>();
		expected.put("membershipNumber",  "12345");
		expected.put("firstName",  "John");
		expected.put("DALVerified",  "true");
		expected.put("id",  "47540");
		expected.put("label",  "Doe, John");
		expected.put("birthDate",  "1854-01-01");
		expected.put("NSDAPNumber",  "443322");
		Map<String, String> actual = soldierTimeLine.getData("47540", "test").get("basicInfo__0");
		assertEquals(expected, actual);
	}

	@Test
	public void soldierDataTestNeg(){
		Map<String, String> expected = new HashMap<>();
		expected.put("membershipNumber",  "23466y");
		expected.put("firstName",  "Jane");
		expected.put("DALVerified",  "true");
		expected.put("id",  "47540");
		expected.put("label",  "Doe, John");
		expected.put("birthDate",  "1855-01-01");
		expected.put("NSDAPNumber",  "23456");
		Map<String, String> actual = soldierTimeLine.getData("47540", "test").get("basicInfo__0");
		assertNotEquals(expected, actual);
	}
}
