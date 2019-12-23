package upb.ida.intent;

import upb.ida.intent.model.ChatbotContext;
import upb.ida.util.BarGraphUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum AnswerHandlingStrategy {
	ACTIVE_TABLE_COLUMNS() {
		@Override
		public List<String> extractAnswer(ChatbotContext context) {
			String userMessage = context.getCurrentUserMessage();
			List<String> activeTableColumns = context.getActiveTableColumns();
			String answer = Util.extractTopKeyword(userMessage, activeTableColumns);
			return answer == null ? new ArrayList<>() : Collections.singletonList(answer);
		}
	},
	BAR_CHART_SUBSET() {

		Pattern firstNPattern = Pattern.compile("(?i)(\\s+)?first\\s+\\d+(\\s+)?");
		Pattern lastNPattern = Pattern.compile("(?i)(\\s+)?last\\s+\\d+(\\s+)?");

		@Override
		public List<String> extractAnswer(ChatbotContext context) {
			/*
			1. First N
			2. Last N
			*/
			List<String> answers = new ArrayList<>();
			Matcher firstNMatcher = firstNPattern.matcher(context.getCurrentUserMessage());
			Matcher lastNMatcher = lastNPattern.matcher(context.getCurrentUserMessage());

			boolean firstN = firstNMatcher.find();
			boolean lastN = lastNMatcher.find();

			if (firstN) {
				int startIndex = firstNMatcher.start();
				int endIndex = firstNMatcher.end();
				String userMessage = context.getCurrentUserMessage().substring(startIndex, endIndex).trim();
				String[] split = userMessage.replaceAll("\\s+", " ").split(" ");
				answers.add(BarGraphUtil.FIRST_N_REC);
				answers.add(split[1]);
			} else if (lastN) {
				int startIndex = lastNMatcher.start();
				int endIndex = lastNMatcher.end();
				String userMessage = context.getCurrentUserMessage().substring(startIndex, endIndex).trim();
				String[] split = userMessage.replaceAll("\\s+", " ").split(" ");

				answers.add(BarGraphUtil.LAST_N_REC);
				answers.add(split[1]);
			}

			return answers;
		}
	};


	public abstract List<String> extractAnswer(ChatbotContext context);
}

