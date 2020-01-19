package upb.ida.intent.model;

import java.util.Objects;

public class Keyword {
	private String keyword;
	private double similarityScore;

	public Keyword(String keyword, double similarityScore) {
		this.keyword = keyword;
		this.similarityScore = similarityScore;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Keyword keyword1 = (Keyword) o;
		return this.keyword.equals(keyword1.keyword);
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyword);
	}
}
