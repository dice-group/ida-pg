package upb.ida.intent;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import upb.ida.intent.model.Keyword;

import java.util.*;
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
			return keywords.stream().sorted(Comparator.comparingDouble(Keyword::getSimilarityScore)).collect(Collectors.toList()).get(0).getKeyword();
		else
			return null;
	}

	public static void main(String[] args) {
//		String input = "labbel";
//		String target = "label";
//
//		System.out.println("CosineDistance: " + new CosineDistance().apply(input, target));
////		System.out.println("CosineSimilarity: " + new CosineSimilarity().cosineSimilarity(input, target));
////		System.out.println("xxxxxxx: " + new FuzzyScore(Locale.ENGLISH).apply(input, target));
//		System.out.println("JaccardSimilarity: " + new JaccardSimilarity().apply(input, target));
//		System.out.println("JaroWinklerSimilarity: " + new JaroWinklerSimilarity().apply(input, target));
//		System.out.println("LevenshteinDistance: " + new LevenshteinDistance().apply(input, target));
//		System.out.println("LevenshteinDetailedDistance: " + new LevenshteinDetailedDistance().apply(input, target));

		System.out.println(SimilarityUtil.extractKeywords("this is my Soldiers data which has some Regiment", Arrays.asList("soldier", "regiment", "label", "id", "description", "deathDate", "birthDate")));
	}
}
