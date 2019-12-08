package upb.ida.intent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BarChartIntentExecutor extends AbstractIntentExecutor implements IntentExecutor {

	protected List<Question> questions = Collections.unmodifiableList(
			Arrays.asList(
					new Question("What is the X-axis?", "x-axis", "X-axis set to ${x-axis}", Question.HandlingStrategy.ACTIVE_TABLE_COLUMNS),
					new Question("What is the Y-axis?", "y-axis", "Y-axis set to ${y-axis}", Question.HandlingStrategy.ACTIVE_TABLE_COLUMNS)
			));

	@Override
	public Question getNextQuestion(Map<String, Object> context) {
		return super.getNextQuestion(questions, context);
	}

	@Override
	public void execute() {
		// Final action
	}

	@Override
	public boolean needsMoreInformation(Map<String, Object> context) {
		return super.needsMoreInformation(questions, context);
	}

	@Override
	public String getNextResponse(Map<String, Object> context) {
		return null;
	}

	@Override
	public void processResponse(String originalMessage, Map<String, Object> context) {
		super.processResponse(originalMessage, context);
	}

}
