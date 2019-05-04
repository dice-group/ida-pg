package upb.ida.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import upb.ida.Application;
import upb.ida.util.BarGraphUtil;
import upb.ida.util.FileUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { Application.class })

public class FetchFiltered {

	@Autowired
	FileUtil fileutil;
	@Autowired
	BarGraphUtil barutil;
	@Autowired
	private ListMapsForFilteringCheck filterMaps;

	@Test
	public void checkFilters() throws JsonProcessingException, IOException, NumberFormatException, ParseException {

		File filterTest = new File(fileutil.fetchSysFilePath("FilterTest.csv"));
		List<Map<String, String>> listMapA = fileutil.convertToMap(filterTest);

		String[] topAsc = { "TOPN", "3", "height", "ascending" };
		String[] topDes = { "TopN", "3", "height", "descending" };
		String[] firstN = { "FIRSTN", "3" };
		String[] lastN = { "LASTN", "2" };
		String[] fromTo = { "FROMTO", "1", "3" };

		List<Map<String, String>> actualHeightAsc = barutil.fetchFilteredData(listMapA, topAsc);
		List<Map<String, String>> expectedHeightAsc = filterMaps.generateMapsforTopAsc();

		for (int i = 0; i < actualHeightAsc.size(); i++) {

			Map<String, String> aMap = actualHeightAsc.get(i);
			Map<String, String> bMap = expectedHeightAsc.get(i);
			boolean result = ListMapsForFilteringCheck.compareMaps(aMap, bMap);
			assertTrue(result);
		}

		List<Map<String, String>> actualHeightDes = barutil.fetchFilteredData(listMapA, topDes);
		List<Map<String, String>> expectedHeightDes = filterMaps.generateMapsforTopDes();

		for (int i = 0; i < actualHeightDes.size(); i++) {

			Map<String, String> aMap = actualHeightDes.get(i);
			Map<String, String> bMap = expectedHeightDes.get(i);
			boolean result = ListMapsForFilteringCheck.compareMaps(aMap, bMap);
			assertTrue(result);
		}

		List<Map<String, String>> actualFirstN = barutil.fetchFilteredData(listMapA, firstN);
		List<Map<String, String>> expectedFirstN = filterMaps.generateMapsforfirstN();

		for (int i = 0; i < actualFirstN.size(); i++) {

			Map<String, String> aMap = actualFirstN.get(i);
			Map<String, String> bMap = expectedFirstN.get(i);
			boolean result = ListMapsForFilteringCheck.compareMaps(aMap, bMap);
			assertTrue(result);
		}

		List<Map<String, String>> actualLastN = barutil.fetchFilteredData(listMapA, lastN);
		System.out.println(actualLastN);
		List<Map<String, String>> expectedLastN = filterMaps.generateMapsforLasttN();

		for (int i = 0; i < actualLastN.size(); i++) {

			Map<String, String> aMap = actualLastN.get(i);
			Map<String, String> bMap = expectedLastN.get(i);
			boolean result = ListMapsForFilteringCheck.compareMaps(aMap, bMap);
			assertTrue(result);
		}

		List<Map<String, String>> actualFromTo = barutil.fetchFilteredData(listMapA, fromTo);
		System.out.println(actualFromTo);
		List<Map<String, String>> expectedFromTo = filterMaps.generateMapsforFromNTo();

		for (int i = 0; i < actualFromTo.size(); i++) {

			Map<String, String> aMap = actualFromTo.get(i);
			Map<String, String> bMap = expectedFromTo.get(i);
			boolean result = ListMapsForFilteringCheck.compareMaps(aMap, bMap);
			assertTrue(result);
		}
	}
}
