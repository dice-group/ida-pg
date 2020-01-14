package upb.ida.test.intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import upb.ida.intent.executor.BarChartExecutor;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Intent;
import upb.ida.intent.model.Question;
import upb.ida.util.BarGraphUtil;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BarChartExecutorTest {
	@Test
	public void testExecution() {
		ChatbotContext context = new ChatbotContext();
		BarChartExecutor barChartExecutor = new BarChartExecutor();
		context.setCurrentIntent(Intent.BAR);
		context.setCurrentExecutor(barChartExecutor);

		assertTrue(barChartExecutor.needsMoreInformation(context));
		assertFalse(barChartExecutor.isExecutable(context));

		Question xAxisQuestion = barChartExecutor.getNextQuestion(context);
		assertEquals("What is the X-axis?", xAxisQuestion.getQuestion());
		context.setActiveQuestion(xAxisQuestion);
		context.getSavedAnswers().put(xAxisQuestion.getAnswerKeys().get(0), "feature1");

		assertTrue(barChartExecutor.needsMoreInformation(context));
		Question yAxisQuestion = barChartExecutor.getNextQuestion(context);
		assertEquals("What is the Y-axis?", yAxisQuestion.getQuestion());
		context.setActiveQuestion(yAxisQuestion);
		context.getSavedAnswers().put(yAxisQuestion.getAnswerKeys().get(0), "feature2");

		assertTrue(barChartExecutor.needsMoreInformation(context));
		Question filterQuestion = barChartExecutor.getNextQuestion(context);
		assertEquals("How many items would you like to see?", filterQuestion.getQuestion());
		context.setActiveQuestion(filterQuestion);
		context.setCurrentMessage("use first 15");
		barChartExecutor.processResponse(context);
		context.getSavedAnswers().put(filterQuestion.getAnswerKeys().get(0), BarGraphUtil.FIRST_N_REC);
		context.getSavedAnswers().put(filterQuestion.getAnswerKeys().get(1), "15");

		assertFalse(barChartExecutor.needsMoreInformation(context));
	}
}
