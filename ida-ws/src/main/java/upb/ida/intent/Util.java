package upb.ida.intent;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import upb.ida.intent.model.Keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

	private static double similarityThreshold = 0.85;
	private static JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

	public static synchronized List<Keyword> extractKeywords(String text, List<String> targetWords) {
		List<Keyword> output = new ArrayList<>();
		String[] sourceWords = text.trim().replaceAll("\\s+", " ").split(" ");
		for (String sourceWord : sourceWords) {
			for (String targetWord : targetWords) {
				Double similarityScore = similarity.apply(sourceWord, targetWord);
				if (similarityScore >= similarityThreshold) {
					Keyword keyword = new Keyword(targetWord, similarityScore);
					if (!output.contains(keyword)) output.add(keyword);
				}
			}
		}
		return output;
	}

	public static synchronized String extractTopKeyword(String text, List<String> targetWords) {
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

		System.out.println(Util.extractKeywords("this is my Soldiers data which has some Regiment", Arrays.asList("soldier", "regiment", "label", "id", "description", "deathDate", "birthDate")));
	}
}
