package ufl;

import java.io.PrintStream;
import java.util.HashSet;

public class UflHeuristicNew extends Ufl {
	public UflHeuristicNew(double[] facilities, double[] consumers, double[][] weight) {
		super(facilities, consumers, weight);
	}
	
	public double heuristica(int graph, boolean improved, PrintStream out) {
		boolean[] checkedConsumers = new boolean[this.consumers.length];
		boolean[] checkedFacilities = new boolean[this.facilities.length];
		
		GurobiMax fMax = new GurobiMax(this.consumers.length, this.facilities.length);
		fMax.gurobiFacilityMax(this.consumers, this.facilities, this.weight);
		
		GurobiMin fMin = new GurobiMin(this.consumers.length, this.facilities.length);
		fMin.gurobiFacilityMin(this.consumers, this.facilities, this.weight);
		
		double v[] = fMax.getV();
		double x[][] = fMin.getX();
		
		double newX[][] = new double[this.facilities.length][this.consumers.length];
		double newY[] = new double[this.facilities.length];
		
		HashSet<Integer> servedClients = new HashSet<Integer>();
		
		while (servedClients.size() < this.consumers.length) {
		
			int minimumFacilityCost = 0;
			int clientWithMinimumV = findMinimumV(v, checkedClients, x);
		
			HashSet<Integer> tempFacilitiesCluster = findFacilitieConnected(
					clientWithMinimumV, x);
			HashSet<Integer> tempClientsCluster = findClientsConnected(
					tempFacilitiesCluster, x);
		
			if(!improved){
				minimumFacilityCost = findMinimumFacilityCost(
						tempFacilitiesCluster, checkedFacilities);
			}
			else{
				minimumFacilityCost = findMinimumFacilityCostImproved(
						tempFacilitiesCluster, checkedFacilities,x);	
			}
		
			newY[minimumFacilityCost] = 1;
			checkedFacilities[minimumFacilityCost] = true;
		
			for (int c : tempClientsCluster) {
				newX[c][minimumFacilityCost] = 1;
				checkedClients[c] = true;
				servedClients.add(c);
			}
		}
		
		double cost = computeCost(newX, newY);
		
		printResultadoHeuristica(newX, newY, cost, graph, improved, out);
		
		return cost;
	}
	
	private int findMinimumV(double[] v, boolean[] checkedClients, double[][] x) {

		double value = Double.MAX_VALUE;
		int client = -1;

		if (!improved) {
			for (int i = 0; i < v.length; i++) {
				if (value > v[i] && !checkedClients[i]) {
					value = v[i];
					client = i;
				}
			}
			//System.out.println("menor valor cliente: "+value);
		} else {
			for (int i = 0; i < v.length; i++) {
				double custoCliente = computeClientTransportCost(i, x);
				if (value > (v[i] + custoCliente) && !checkedClients[i]) {
					value = v[i] + custoCliente;
					client = i;
				}
			}
		}

		return client;
	}

	@Override
	public UflResult exec() {		
		return this.createResult(this.avaliate());
	}

}
