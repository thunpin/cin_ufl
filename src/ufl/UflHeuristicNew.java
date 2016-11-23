package ufl;

public class UflHeuristicNew extends Ufl {
	public UflHeuristicNew(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
	}
	
	@Override
	public UflResult exec() {
		boolean[] checkedConsumers = new boolean[this.consumers.length];
		
		GurobiMax fMax = new GurobiMax(this.consumers.length, this.facilities.length);
		fMax.gurobiFacilityMax(this.consumers, this.facilities, this.weight);
		
		GurobiMin fMin = new GurobiMin(this.consumers.length, this.facilities.length);
		fMin.gurobiFacilityMin(this.consumers, this.facilities, this.weight);
		
		double v[] = fMax.getV();		
		int servedConsumers = 0;
		while (servedConsumers < this.consumers.length) {
			int index = findMinimumV(v, checkedConsumers);
		
			int facilityIndex = getBestFacilityToOpen(index);
			
			for (int i = 0; i < this.consumers.length; i++) {
				if (this.weight[facilityIndex][i] != NO_PATH && !checkedConsumers[i]) {
					this.use[facilityIndex][i] = ON;
					servedConsumers++;
					checkedConsumers[i] = true;
				}
			}
		}
		
		double score = this.avaliate();		
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
}
