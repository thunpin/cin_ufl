package ufl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UflResult {
	private final float score;
	private final Map<Integer, List<Integer>> links;
	
	public UflResult(float score, Map<Integer, List<Integer>> links) {
		super();
		this.score = score;
		this.links = links;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("score:").append(this.score).append("\n");
		
		for (Entry<Integer, List<Integer>> entry : this.links.entrySet()) {
			builder.append("facility ").append(entry.getKey()).append(":");
			for (Integer value : entry.getValue()) {
				builder.append(value).append(" ");
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}

	public float getScore() {
		return score;
	}
	public Map<Integer, List<Integer>> getLinks() {
		return links;
	}
	
	
}
