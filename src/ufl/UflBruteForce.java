package ufl;

import java.util.HashMap;

public class UflBruteForce extends Ufl {
	private UflResult result;
	private final int[] currentUse;
	
	public UflBruteForce(int[] facilities, int[] consumers, int[][] distance) {
		super(facilities, consumers, distance);
		result = new UflResult(Float.MAX_VALUE, new HashMap<>());
		currentUse = new int[this.facilities.length * this.consumers.length];
	}

	@Override
	public UflResult exec() {
		exec(0);
		return result;
	}
	
	private void exec(int pos) {
		if (pos < currentUse.length) {
			currentUse[pos] = ON;
			verifyMin(currentUse);
			exec(pos+1);
			
			currentUse[pos] = OFF;
			exec(pos+1);
		}
	}
	
	protected void verifyMin(int[] currentUse) {
		if (parseToUse(currentUse)) {
			float currentScore = avaliate(true);
			if (result.getScore() > currentScore) {			
				result = createResult(currentScore);
			}
		}
	}
	
	protected boolean parseToUse(int[] currentUse) {
		int i = -1;
		int[] toAvaliate = new int[this.consumers.length];
		for (int j = 0; j < currentUse.length; j++) {
			int jj = j % this.consumers.length;
			if (jj == 0) {
				i++;
			}
			this.use[i][jj] = currentUse[j];
			toAvaliate[jj] ^= currentUse[j];
		}
		
		boolean result = true;
		for (i = 0; i < toAvaliate.length; i++) {
			if (toAvaliate[i] == OFF) {
				result = false;
				break;
			}
		}
		return result;
	}
}
