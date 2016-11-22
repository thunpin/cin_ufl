package ufl;

public class Score {
	private final int index;
	private double score;
	
	public Score(final int index) {
		this.index = index;
	}
	
	public void add(double value) {
		this.score += value;
	}
	
	public int getIndex() {
		return index;
	}
	public Double getScore() {
		return score;
	}
}
