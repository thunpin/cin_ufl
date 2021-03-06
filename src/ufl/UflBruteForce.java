package ufl;

import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics3.Generator;

public class UflBruteForce extends Ufl {
	
	public UflBruteForce(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
	}

	@Override
	public UflResult exec() {
		double minScore = Double.MAX_VALUE;
		int[][] links = new int[this.facilities.length][this.consumers.length];
		
		List<Integer> facilitiesPos = new LinkedList<>();
		for (int i = 0; i < this.facilities.length; i++)
			facilitiesPos.add(i);
		
		for (List<Integer> subSet: Generator.subset(facilitiesPos).simple()) {
			this.use = new int[this.facilities.length][this.consumers.length];
			
			double[] demand = consumers.clone();
			List<Integer> conflicts = new LinkedList<>();
			for (int j = 0; j < this.consumers.length; j++) {
				int numConflicts = 0;
				for (Integer i : subSet) {
					if (this.weight[i][j] != NO_PATH) {
						this.use[i][j] = ON;
						demand[j] = 0;
						numConflicts++;
					}
				}
				if(numConflicts > 1) {
					conflicts.add(j);
				}
			}
			
			if (!isValidateDemand(demand)) {
				continue;
			}
			
			if (conflicts.isEmpty()) {
				double score = this.avaliate();
				if (score <= minScore) {
					minScore = score;
					links = this.use.clone();
				}
				
				continue;
			}
			
			double bestLocalScore = Double.MAX_VALUE;
			for (int j : conflicts) {
				for (int i : subSet) {
					if (this.weight[i][j] != NO_PATH) {
						this.use[i][j] = OFF;
					}
				}
				
				int lastI = -1;
				for (int i : subSet) {
					if (this.weight[i][j] != NO_PATH) {
						if (lastI != -1) {
							this.use[lastI][j] = OFF;
						}
						this.use[i][j] = ON;
						
						double score = this.avaliate();
						if (score <= bestLocalScore) {
							bestLocalScore = score;
							lastI = i;
						} else {
							if (lastI == -1) {
								lastI = i;
							} else {
								this.use[lastI][j] = ON;
							}
							this.use[i][j] = OFF;
						}
					}
				}
			}
			
			double score = this.avaliate();
			if (score  <= minScore) {
				minScore = score ;
				links = this.use.clone();
			}
		}
		
		this.use = links.clone();
		return this.createResult(minScore);
	}
}
