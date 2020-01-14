package upb.ida.test.intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.util.BarGraphUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class AnswerHandlingStrategyTest {
	@Test
	public void testNumericValue() {
		ChatbotContext context = new ChatbotContext();
		context.setCurrentMessage("give me 10 records");
		List<String> answers = AnswerHandlingStrategy.NUMERIC_VALUE.extractAnswer(context);
		assertEquals(answers.size(), 1);
		assertEquals(answers.get(0), "10");
	}

	@Test
	public void testFilterOptions() {
		ChatbotContext context = new ChatbotContext();

		context.setCurrentMessage("show me first 15 records");
		List<String> firstNAnswers = AnswerHandlingStrategy.FILTER_OPTIONS.extractAnswer(context);
		assertEquals(firstNAnswers.size(), 2);
		assertEquals(firstNAnswers.get(0), BarGraphUtil.FIRST_N_REC);
		assertEquals(firstNAnswers.get(1), "15");

		context.setCurrentMessage("filter last 5 records");
		List<String> lastNAnswers = AnswerHandlingStrategy.FILTER_OPTIONS.extractAnswer(context);
		assertEquals(lastNAnswers.size(), 2);
		assertEquals(lastNAnswers.get(0), BarGraphUtil.LAST_N_REC);
		assertEquals(lastNAnswers.get(1), "5");

		context.setCurrentMessage("display 25 records");
		List<String> nAnswers = AnswerHandlingStrategy.FILTER_OPTIONS.extractAnswer(context);
		assertEquals(nAnswers.size(), 2);
		assertEquals(nAnswers.get(0), BarGraphUtil.FIRST_N_REC);
		assertEquals(nAnswers.get(1), "25");
	}
}
