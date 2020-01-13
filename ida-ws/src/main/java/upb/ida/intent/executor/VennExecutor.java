package upb.ida.intent.executor;

import upb.ida.intent.exception.IntentExecutorException;
import upb.ida.intent.model.ChatbotContext;

import java.util.Arrays;
import java.util.Collections;

public class VennExecutor extends AbstractExecutor implements IntentExecutor {


	public VennExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
//						new Question("Which column should I use as latitude?", Collections.singletonList("latitude"), "Using ${latitude} as latitude.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
//						new Question("Which column should I use as longitude?", Collections.singletonList("longitude"), "Using ${longitude} as longitude.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false)
				))
		);
	}

	@Override
	public void execute(ChatbotContext context) throws IntentExecutorException {

	}
}
