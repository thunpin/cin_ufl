package ufl;

import java.util.HashSet;

public class UflHeuristicOptimal extends UflHeuristic {
	public UflHeuristicOptimal(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
	}

	@Override
	protected int findMinimumFacilityCost(HashSet<Integer> tempFacilitiesCluster, boolean[] checkedFacilities,
			double[][] x) {
		double bestValue = Double.MAX_VALUE;
		int facility = -1;

		for (int f : tempFacilitiesCluster) {
			double value = 0;
			value = this.facilities[f];
			for (int c = 0; c < this.consumers.length; c++) {
				if (x[f][c] > 0) {
					value += this.weight[f][c] * this.consumers[c];
				}
			}

			if (value < bestValue) {
				facility = f;
			}
		}

		return facility;
	}
}
