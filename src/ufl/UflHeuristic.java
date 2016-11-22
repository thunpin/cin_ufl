package ufl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UflHeuristic extends Ufl {
	protected final List<Score> scores = new ArrayList<>(this.consumers.length);
	protected final Map<Integer, Integer> consumerMap = new HashMap<>();
	protected int freeConsumers;
	
	public UflHeuristic(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
		
		// initialize the free consumers with the max number of consumers
		freeConsumers = consumers.length;
		
		// initialize the score list	
		for (int i = 0; i < this.consumers.length; i++) {
			scores.add(new Score(i));
		}
	}

	@Override
	public UflResult exec() {
		prepareScores();
		createClusters();		
		return this.createResult(this.avaliate());
	}

	protected void createClusters() {
		while (freeConsumers != EMPTY) {
			// create the clusters
			int index = scores.get(0).getIndex();
			
			int facilityIndex = getBestFacilityToOpen(index);
			
			for (int i = 0; i < this.consumers.length; i++) {
				if (this.weight[facilityIndex][i] != NO_PATH && !this.consumerMap.containsKey(i)) {
					this.consumerMap.put(i, facilityIndex);
					this.use[facilityIndex][i] = ON;
					freeConsumers--;
				}
			}
			scores.remove(0);
		}
	}

	protected int getBestFacilityToOpen(int index) {
		// find facility with the smallest cost
		int facilityIndex = 0;
		double facilityValue = Float.MAX_VALUE;
		for (int i = 0; i < this.facilities.length; i++) {
			if (this.weight[i][index] != NO_PATH) {
				double maxFacilityValue = getMaxFacilityValue(i);
				if (facilityValue > maxFacilityValue) {
					facilityValue = maxFacilityValue;
					facilityIndex = i;
				}
				
			}
		}
		return facilityIndex;
	}

	protected double getMaxFacilityValue(int i) {
		int[][] tempUse = this.use.clone();
		this.use = new int[this.facilities.length][this.consumers.length];
		
		for (int j = 0; j < this.weight[i].length; j++) {
			if (this.weight[i][j] != NO_PATH) {
				this.use[i][j] = ON;
			}
		}
		
		double currentFacilityValue = this.avaliate();
		for (int j = 0; j < this.weight[i].length; j++) {
			if (this.weight[i][j] != NO_PATH) {
				this.use[i][j] = OFF;
			}
		}
		this.use = tempUse.clone();
		
		return currentFacilityValue;
	}

	protected void prepareScores() {
		// prepare the score list
		for (int i = 0; i < this.facilities.length; i++) {
			for (int j = 0; j < this.consumers.length; j++) {
				// only calculate for no empty consumer to a facility
				if (this.weight[i][j] != NO_PATH) {
					this.use[i][j] = ON;
					final double score = this.avaliate();
					scores.get(j).add(score);
					this.use[i][j] = OFF;
				}
			}
		}
		// sort the scores
		scores.sort((score1, score2) -> {
			return score1.getScore().compareTo(score2.getScore());
		});
	}

}
