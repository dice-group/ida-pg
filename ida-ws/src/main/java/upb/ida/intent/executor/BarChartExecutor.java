package upb.ida.intent.executor;

import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.intent.AnswerHandlingStrategy;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.intent.model.Question;
import upb.ida.util.BarGraphUtil;
import upb.ida.util.BeanUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Component
public class BarChartExecutor extends AbstractExecutor implements IntentExecutor {

	public BarChartExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
						new Question("What is the X-axis?", Collections.singletonList("x-axis"), "X-axis set to ${x-axis}.", "Column from the active table to show as X-axis", AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("What is the Y-axis?", Collections.singletonList("y-axis"), "Y-axis set to ${y-axis}.", "Numeric column from the active table to show as Y-axis", AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("How many items would you like to see?", Arrays.asList("subset-type", "subset-size"), "Showing ${subset-size} items.", "HINT: Top N | Last N", AnswerHandlingStrategy.BAR_CHART_SUBSET, false)
				))
		);

	}

	@Override
	public void execute(ChatbotContext context) {
		// Final action
		try {
			BarGraphUtil barGraphUtil = BeanUtil.getBean(BarGraphUtil.class);
			ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
			Map<String, String> savedAnswers = context.getSavedAnswers();
			barGraphUtil.generateBarGraphData(new String[]{savedAnswers.get("x-axis"), savedAnswers.get("y-axis"), savedAnswers.get("subset-type"), savedAnswers.get("subset-size")}, responseBean);
			context.resetOnNextRequest();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			context.addChatbotResponse("Error occurred in executing the action.");
			context.resetOnNextRequest();
		}
	}

}
