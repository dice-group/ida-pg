package upb.ida.intent;

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
}
