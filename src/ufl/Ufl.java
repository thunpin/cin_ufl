package ufl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Ufl {
	protected final static int EMPTY = 0;
	protected final static int ON = 1;
	protected final static int OFF = 0;
	protected final static int NO_PATH = 9999;
	
	protected final int[] facilities;
	protected final int[] consumers;
	protected final int[][] weight;
	
	protected int[] open;
	protected int[][] use;
	
	public Ufl(int[] facilities, int[] consumers, int[][] weight) {
		this.facilities = facilities.clone();
		this.consumers = consumers.clone();
		this.weight = weight.clone();
		
		this.open = new int[facilities.length];
		this.use = new int[facilities.length][consumers.length];
	}
	
	public abstract UflResult exec();
	
	protected float avaliate() {
		return avaliate(false);
	}
	
	protected float avaliate(boolean testDemand) {		
		float result = 0;
		int[] demand = consumers.clone();
		this.open = new int[this.facilities.length];
		
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
	
	protected UflResult createResult(float currentScore) {
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

	private boolean isValidateDemand(int[] demand) {
		// validate the demand
		int total = 0;
		for (int value : demand) {
			total += value;
		}
		
		return total == EMPTY;
	}

//	int count = 0;
//	private void debug(float result, int[] demand) {
//		boolean on = true;
//		for (int i = 0; i < use[1].length; i++) {
//			if (use[2][i] == OFF) {
//				on = false;
//			}
//		}
////		for (int i = 0; i < weight.length; i++) {
////			for (int j = 0; j < weight[i].length; j++) {
////				if (use[i][j] == OFF) {
////					on = false;
////				}
////			}
////		}
//		
//		if (isValidateDemand(demand) && on ) {
//			System.out.println(count++);
//			float a = 0;
//			for (int i = 0; i < weight.length; i++) {
//				for (int j = 0; j < weight[i].length; j++) {
//					a += weight[i][j] * use[i][j];
//					System.out.print(weight[i][j] + "-" + use[i][j] + "    ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//			System.out.println(a + " " + result + " ");
//			System.out.println(open[0] + " " + " " + open[1] + " " + open[2]);
//			System.out.println("--------------");
//		}
//	}
}
