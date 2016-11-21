package ufl;

public class Score {
	private final int index;
	private float score;
	
	public Score(final int index) {
		this.index = index;
	}
	
	public void add(float value) {
		this.score += value;
	}
	
	public int getIndex() {
		return index;
	}
	public Float getScore() {
		return score;
	}
}
