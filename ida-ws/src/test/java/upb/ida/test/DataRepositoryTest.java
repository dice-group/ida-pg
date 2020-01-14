package upb.ida.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import upb.ida.Application;
import upb.ida.dao.DataRepository;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {Application.class})
public class DataRepositoryTest {

	private DataRepository dataRepository;
	private Set<String> expected = new TreeSet<>();

	public DataRepositoryTest() {
		dataRepository = new DataRepository(true);
		expected.add("sameAs");
		expected.add("capitalOf");
		expected.add("hasCode");
		expected.add("featureCode");
		expected.add("featureClass");
		expected.add("name");
	}

	@Test
	public void getColumnListTestPos() {
		Set<String> actual = dataRepository.getColumnsList("Capital", "test");
		assertEquals(expected, actual);
	}

	@Test
	public void getColumnListTestNeg() {
		Set<String> expected = new TreeSet<>();
		expected.add("area");
		expected.add("population");

		Set<String> actual = dataRepository.getColumnsList("Capital", "test");

		assertNotEquals(expected, actual);
	}
}
