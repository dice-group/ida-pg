package upb.ida.test.intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import upb.ida.intent.SimilarityUtil;
import upb.ida.intent.model.Keyword;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class SimilarityUtilTest {

	@Test
	public void testExtractKeywords() {
		String input = "Tell me which breads contain fruits in them";
		List<String> targetWords = Arrays.asList("vegetable", "fruit", "bread", "medicine");

		List<Keyword> extractKeywords = SimilarityUtil.extractKeywords(input, targetWords);
		assertEquals(2, extractKeywords.size());
		assertTrue(extractKeywords.contains(new Keyword("fruit", 1)));
		assertTrue(extractKeywords.contains(new Keyword("bread", 1)));
	}

	@Test
	public void testExtractTopKeyword() {
		String input = "He likes his lamborghini is black and she likes her lamboargini in yellow";
		List<String> targetWords = Arrays.asList("lamborghini", "bmw", "audi");

		String topKeyword = SimilarityUtil.extractTopKeyword(input, targetWords);
		assertEquals("lamborghini", topKeyword);
	}
}
