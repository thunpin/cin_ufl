package ufl;

public class UflHeuristicOptimal extends UflHeuristic {
	public UflHeuristicOptimal(int[] facilities, int[] consumers, int[][] weight) {
		super(facilities, consumers, weight);
	}

	@Override
	protected void createClusters() {
		while (freeConsumers != EMPTY) {
			// create the clusters
			int index = scores.get(0).getIndex();
			
			if (!this.consumerMap.containsKey(index)) {
				int facilityIndex = getBestFacilityToOpen(index);
				balance(facilityIndex);
			}
			
			scores.remove(0);
		}
	}

	private void balance(int facilityIndex) {
		for (int i = 0; i < this.consumers.length; i++) {
			if (this.weight[facilityIndex][i] != NO_PATH && !this.consumerMap.containsKey(i)) {
				freeConsumers--;
				this.consumerMap.put(i, facilityIndex);
				this.use[facilityIndex][i] = ON;
			} else if (this.weight[facilityIndex][i] != NO_PATH) {
				int oldFacilityIndex = this.consumerMap.get(i);
				double score1 = this.avaliate();
				this.use[facilityIndex][i] = ON;
				this.use[oldFacilityIndex][i] = OFF;
				double score2 = this.avaliate();
				
				if (score2 < score1) {
					this.consumerMap.put(i, facilityIndex);
				} else {
					this.use[facilityIndex][i] = OFF;
					this.use[oldFacilityIndex][i] = ON;
				}
			}
		}
	}
}
