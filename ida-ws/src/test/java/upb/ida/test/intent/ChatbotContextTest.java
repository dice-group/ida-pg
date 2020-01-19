package upb.ida.test.intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import upb.ida.intent.IntentExecutorFactoryHelper;
import upb.ida.intent.exception.IntentException;
import upb.ida.intent.executor.GreetingExecutor;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Intent;
import upb.ida.intent.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
public class ChatbotContextTest {
	@Test
	public void testChatbotContext() throws IntentException {
		ChatbotContext context = new ChatbotContext();
		testDefaultChatbotContext(context);

		context.setCurrentMessage("hello");
		context.setResetOnNextRequest(true);
		context.setActiveQuestion(new Question("question text", Collections.singletonList("answerKey"), "feedbackText", "helpText", null, true));
		context.setCurrentExecutor(IntentExecutorFactoryHelper.getExecutorFor(Intent.GREETING));
		context.setCurrentIntent(Intent.GREETING);
		context.setChatbotResponses(new ArrayList<>(Arrays.asList("response 1")));
		context.addChatbotResponse("response 2");
		context.addTip("user tip");
		context.setSavedAnswers(Collections.singletonMap("answerKey", "answerText"));

		assertEquals("hello", context.getCurrentMessage());
		assertTrue(context.isResetOnNextRequest());
		assertEquals("question text", context.getActiveQuestion().getQuestion());
		assertEquals("answerKey", context.getActiveQuestion().getAnswerKeys().get(0));
		assertEquals("feedbackText", context.getActiveQuestion().getFeedbackText());
		assertEquals("helpText", context.getActiveQuestion().getHelpText());
		assertNull(context.getActiveQuestion().getStrategy());
		assertTrue(context.getActiveQuestion().isOptional());
		assertTrue(context.getCurrentExecutor() instanceof GreetingExecutor);
		assertEquals(Intent.GREETING, context.getCurrentIntent());
		assertEquals("response 1", context.getChatbotResponses().get(0));
		assertEquals("response 2", context.getChatbotResponses().get(1));
		assertEquals("user tip", context.getTips().get(0));
		assertTrue(context.getSavedAnswers().containsKey("answerKey"));

		context.resetContext();
		testDefaultChatbotContext(context);

	}

	private void testDefaultChatbotContext(ChatbotContext context) {
		assertEquals(context.getCurrentIntent(), Intent.GREETING);
		assertTrue(context.getCurrentExecutor() instanceof GreetingExecutor);
		assertEquals(context.getSavedAnswers().size(), 0);
		assertEquals(context.getChatbotResponses().size(), 1);
		assertEquals(context.getChatbotResponses().get(0), "Hello! How may I help you?");
		assertTrue(context.getTips().isEmpty());
		assertNull(context.getActiveQuestion());
		assertNull(context.getCurrentMessage());
		assertFalse(context.isResetOnNextRequest());
	}

}
