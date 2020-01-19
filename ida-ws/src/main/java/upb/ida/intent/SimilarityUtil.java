package upb.ida.intent;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import upb.ida.intent.model.Keyword;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SimilarityUtil {

	private static double similarityThreshold = 0.85;
	private static JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

	public static synchronized List<Keyword> extractKeywords(String text, Collection<String> targetWords) {
		List<Keyword> extractedKeywords = new ArrayList<>();
		String[] sourceWords = text.trim().toLowerCase().replaceAll("\\s+", " ").split(" ");

		// Check if there are exact matches
		for (String token : sourceWords) {
			for (String targetWord : targetWords) {
				if (token.equalsIgnoreCase(targetWord)) {
					extractedKeywords.add(new Keyword(targetWord, 1));
				}
			}
		}

		// Find close matches if there are no exact matches
		if (extractedKeywords.isEmpty()) {
			for (String sourceWord : sourceWords) {
				for (String targetWord : targetWords) {
					Double similarityScore = similarity.apply(sourceWord, targetWord.toLowerCase());
					if (similarityScore >= similarityThreshold) {
						Keyword keyword = new Keyword(targetWord, similarityScore);
						if (!extractedKeywords.contains(keyword)) extractedKeywords.add(keyword);
					}
				}
			}
		}

		return extractedKeywords;
	}

	public static synchronized String extractTopKeyword(String text, Collection<String> targetWords) {
		List<Keyword> keywords = extractKeywords(text, targetWords);

		if (!keywords.isEmpty())
			return keywords.stream().sorted(Comparator.comparingDouble(Keyword::getSimilarityScore).reversed()).collect(Collectors.toList()).get(0).getKeyword();
		else
			return null;
	}
}
