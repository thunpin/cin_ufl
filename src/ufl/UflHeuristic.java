package ufl;

import java.util.HashSet;

public class UflHeuristic extends Ufl {
	public UflHeuristic(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
	}

	@Override
	public UflResult exec() {
		boolean[] checkedConsumers = new boolean[this.consumers.length];
		boolean[] checkedFacilities = new boolean[this.facilities.length];

		GurobiMax fMax = new GurobiMax(this.consumers.length, this.facilities.length);
		fMax.gurobiFacilityMax(this.consumers, this.facilities, this.weight);

		GurobiMin fMin = new GurobiMin(this.consumers.length, this.facilities.length);
		fMin.gurobiFacilityMin(this.consumers, this.facilities, this.weight);

		double v[] = fMax.getV();
		double x[][] = fMin.getX();

		int totalConsumers = 0;
		while (totalConsumers < this.consumers.length) {
			int cluster = 0;
			int clientWithMinimumV = findMinimumV(v, checkedConsumers);

			HashSet<Integer> facilitiesToAvaliate = findFacilitieConnected(clientWithMinimumV, x);
			HashSet<Integer> clusterConsumers = findConsumersConnected(facilitiesToAvaliate, x, checkedConsumers);

			cluster = findMinimumFacilityCost(facilitiesToAvaliate, checkedFacilities, x);

			checkedFacilities[cluster] = true;

			for (int i : clusterConsumers) {
				this.use[cluster][i] = ON;
				checkedConsumers[i] = true;
				totalConsumers++;
			}
		}

		double score = this.avaliate(true);
		return createResult(score);
	}

	private int findMinimumV(double[] v, boolean[] checkedClients) {
		double value = Double.MAX_VALUE;
		int client = -1;

		for (int i = 0; i < v.length; i++) {
			if (value > v[i] && !checkedClients[i]) {
				value = v[i];
				client = i;
			}
		}

		return client;
	}

	private HashSet<Integer> findConsumersConnected(HashSet<Integer> tempFacilitiesCluster, double[][] x,
			boolean[] checkedConsumers) {

		HashSet<Integer> clientsConnected = new HashSet<Integer>();

		for (int f : tempFacilitiesCluster) {
			for (int c = 0; c < this.consumers.length; c++) {
				if (x[f][c] > 0 && !checkedConsumers[c])
					clientsConnected.add(c);
			}
		}

		return clientsConnected;
	}

	protected int findMinimumFacilityCost(HashSet<Integer> tempFacilitiesCluster, boolean[] checkedFacilities,
			double[][] x) {

		double facilityCost = Double.MAX_VALUE;
		int facility = -1;

		for (int f : tempFacilitiesCluster) {
			if (facilityCost > this.facilities[f] && !checkedFacilities[f]) {
				facilityCost = this.facilities[f];
				facility = f;
			}
		}

		return facility;
	}

	private HashSet<Integer> findFacilitieConnected(int minClient, double[][] x) {
		HashSet<Integer> facilitiesConnected = new HashSet<Integer>();
		for (int i = 0; i < this.facilities.length; i++) {
			if (x[i][minClient] != OFF) {
				facilitiesConnected.add(i);
			}
		}

		return facilitiesConnected;
	}
}
