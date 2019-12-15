package upb.ida.intent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.util.BarGraphUtil;
import upb.ida.util.BeanUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Component
public class BarChartIntentExecutor extends AbstractIntentExecutor implements IntentExecutor {

	@Autowired
	private ResponseBean responseBean;
	@Autowired
	private BarGraphUtil barGraphUtil;

	public BarChartIntentExecutor() {
		super(Collections.unmodifiableList(
				Arrays.asList(
						new Question("What is the X-axis?aaa", Collections.singletonList("x-axis"), "X-axis set to ${x-axis}.", "Column from the active table to show as X-axis", AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("What is the Y-axis?", Collections.singletonList("y-axis"), "Y-axis set to ${y-axis}.", "Numeric column from the active table to show as Y-axis", AnswerHandlingStrategy.ACTIVE_TABLE_COLUMNS, false),
						new Question("How many items would you like to see?", Arrays.asList("subset-type", "subset-size"), "Showing ${subset-size} items.", "HINT: Top N | Last N", AnswerHandlingStrategy.BAR_CHART_SUBSET, false)
				))
		);

	}

	@Override
	public void execute(ChatbotContext context) throws IOException, ParseException {
		// Final action
//		args = {"id", "sortation", "TOPN", "10"};
		BarGraphUtil barGraphUtil = BeanUtil.getBean(BarGraphUtil.class);
		ResponseBean responseBean = BeanUtil.getBean(ResponseBean.class);
		Map<String, String> savedAnswers = context.getSavedAnswers();
		barGraphUtil.generateBarGraphData(new String[]{savedAnswers.get("x-axis"), savedAnswers.get("y-axis"), savedAnswers.get("subset-type"), savedAnswers.get("subset-size")}, responseBean);

	}

}
