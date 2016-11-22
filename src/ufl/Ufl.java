package ufl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Ufl {
	protected final static double EMPTY = 0;
	protected final static int ON = 1;
	protected final static int OFF = 0;
	protected final static double NO_PATH = 9999;
	
	protected final double[] facilities;
	protected final double[] consumers;
	protected final double[][] weight;
	
	protected double[] open;
	protected int[][] use;
	
	public Ufl(double[] facilities, double[] consumers, double[][] weight) {
		this.facilities = facilities.clone();
		this.consumers = consumers.clone();
		this.weight = weight.clone();
		
		this.open = new double[facilities.length];
		this.use = new int[facilities.length][consumers.length];
	}
	
	public abstract UflResult exec();
	
	protected double avaliate() {
		return avaliate(false);
	}
	
	protected double avaliate(boolean testDemand) {		
		float result = 0;
		double[] demand = consumers.clone();
		this.open = new double[this.facilities.length];
		
		for (int i = 0; i < weight.length; i++) {
			for (int j = 0; j < weight[i].length; j++) {
				if (use[i][j] == ON) {
					demand[j] = EMPTY;
					open[i] = ON;
				}
				
				result += consumers[j] * weight[i][j] * use[i][j];
			}
		}
		
		for (int i = 0; i < facilities.length; i++) {
			result += open[i] * facilities[i];
		}
		
		if (testDemand && !isValidateDemand(demand)) {
			result = Float.MAX_VALUE;
		}
		
		return result;
	}
	
	protected UflResult createResult(double currentScore) {
		final Map<Integer, List<Integer>> currentLinks = new HashMap<>();
		for (int i = 0; i < this.use.length; i++) {
			for (int j = 0; j < this.use[i].length; j++) {
				if (this.use[i][j] == ON) {
					List<Integer> list = currentLinks.get(i+1);
					if (list == null) {
						list = new LinkedList<>();
					}
					list.add(j+1);
					currentLinks.put(i+1, list);
				}
			}
		}
		
		return new UflResult(currentScore, currentLinks);
	}

	protected boolean isValidateDemand(double[] demand) {
		// validate the demand
		double total = 0;
		for (double value : demand) {
			total += value;
		}
		
		return total == EMPTY;
	}

//	double count = 0;
//	private void debug(float result, double[] demand) {
//		boolean on = true;
//		for (double i = 0; i < use[1].length; i++) {
//			if (use[2][i] == OFF) {
//				on = false;
//			}
//		}
////		for (double i = 0; i < weight.length; i++) {
////			for (double j = 0; j < weight[i].length; j++) {
////				if (use[i][j] == OFF) {
////					on = false;
////				}
////			}
////		}
//		
//		if (isValidateDemand(demand) && on ) {
//			System.out.prdoubleln(count++);
//			float a = 0;
//			for (double i = 0; i < weight.length; i++) {
//				for (double j = 0; j < weight[i].length; j++) {
//					a += weight[i][j] * use[i][j];
//					System.out.prdouble(weight[i][j] + "-" + use[i][j] + "    ");
//				}
//				System.out.prdoubleln();
//			}
//			System.out.prdoubleln();
//			System.out.prdoubleln(a + " " + result + " ");
//			System.out.prdoubleln(open[0] + " " + " " + open[1] + " " + open[2]);
//			System.out.prdoubleln("--------------");
//		}
//	}
}
