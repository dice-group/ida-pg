package upb.ida.intent.executor;

import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.exception.IntentExecutorException;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;

import java.util.Arrays;
import java.util.Collections;

public class ForceDirectedGraphExecutor extends AbstractExecutor implements IntentExecutor {

	public ForceDirectedGraphExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
						new Question("What is the source node?", Collections.singletonList("source"), "Source node is set to ${source}.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("What is the target node?", Collections.singletonList("target"), "Target node is set to ${target}.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("Which feature should depict the strength between the nodes?", Collections.singletonList("strength"), "Using ${strength} for strength.", null, AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false)
				))
		);
	}

	@Override
	public void execute(ChatbotContext context) throws IntentExecutorException {
		// Add code here
		System.out.println("FDG done");
		context.addChatbotResponse("Force directed graph is now loaded");
		context.resetOnNextRequest();

	}
}
