package upb.ida.intent;

import org.apache.commons.lang3.StringUtils;
import upb.ida.intent.model.ChatbotContext;
import upb.ida.util.BarGraphUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Contains various types of logic for extracting answer from the user's massage
 */
public enum AnswerHandlingStrategy {

	ACTIVE_TABLE_COLUMNS() {
		/**
		 * Extracts active table's columns from the message
		 */
		@Override
		public List<String> extractAnswer(ChatbotContext context) {
			String userMessage = context.getCurrentMessage();
			Set<String> activeTableColumns = context.getActiveTableColumns();
			String answer = SimilarityUtil.extractTopKeyword(userMessage, activeTableColumns);
			return answer == null ? new ArrayList<>() : Collections.singletonList(answer);
		}
	},
	FILTER_OPTIONS() {
		/**
		 * Extracts filter options from the message e.g. first 5, last 15
		 */
		Pattern firstNPattern = Pattern.compile("(?i)(\\s+)?first\\s+\\d+(\\s+)?");
		Pattern lastNPattern = Pattern.compile("(?i)(\\s+)?last\\s+\\d+(\\s+)?");

		@Override
		public List<String> extractAnswer(ChatbotContext context) {
			List<String> answers = new ArrayList<>();
			Matcher firstNMatcher = firstNPattern.matcher(context.getCurrentMessage());
			Matcher lastNMatcher = lastNPattern.matcher(context.getCurrentMessage());

			boolean firstN = firstNMatcher.find();
			boolean lastN = lastNMatcher.find();

			if (firstN) {
				int startIndex = firstNMatcher.start();
				int endIndex = firstNMatcher.end();
				String userMessage = context.getCurrentMessage().substring(startIndex, endIndex).trim();
				String[] split = userMessage.replaceAll("\\s+", " ").split(" ");
				answers.add(BarGraphUtil.FIRST_N_REC);
				answers.add(split[1]);
			} else if (lastN) {
				int startIndex = lastNMatcher.start();
				int endIndex = lastNMatcher.end();
				String userMessage = context.getCurrentMessage().substring(startIndex, endIndex).trim();
				String[] split = userMessage.replaceAll("\\s+", " ").split(" ");

				answers.add(BarGraphUtil.LAST_N_REC);
				answers.add(split[1]);
			} else {
				List<String> numericValue = NUMERIC_VALUE.extractAnswer(context);
				if (!numericValue.isEmpty()) {
					answers.add(BarGraphUtil.FIRST_N_REC);
					answers.add(numericValue.get(0));
				}
			}

			return answers;
		}
	},
	NUMERIC_VALUE() {
		@Override
		public List<String> extractAnswer(ChatbotContext context) {
			String userMessage = context.getCurrentMessage();
			String[] tokens = userMessage.split("\\s");

			for (String token : tokens) {
				if (StringUtils.isNumeric(token)) {
					return Collections.singletonList(token);
				}
			}

			return Collections.emptyList();
		}
	};


	public abstract List<String> extractAnswer(ChatbotContext context);
}

