package ufl;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class GurobiMin {
	private double solution;
	private double xS[][];
	private double yS[];

	public GurobiMin(int clients, int facilities) {
		xS = new double[clients][facilities];
		yS = new double[facilities];
		solution = 0;
	}
	
	public double getSolution()
	{
		return this.solution;
	}
	
	public double[][] getX()
	{
		return this.xS;
	}
	
	public double[] getY()
	{
		return this.yS;
	}

	public void gurobiFacilityMin(double[] w1, double[] f1, double[][] d1) {
		try {

			// Warehouse demand in thousands of units
			double[] w = w1.clone();

			// Fixed costs for each plant
			double f[] = f1.clone();

			// Transportation costs per thousand units
			double d[][] = d1.clone();

			// Number of plants and warehouses
			int facilities = f.length;
			int clients = w.length;

			// Model
			GRBEnv env = new GRBEnv();
			env.set(GRB.IntParam.LogToConsole, 0);
			GRBModel model = new GRBModel(env);
			model.set(GRB.StringAttr.ModelName, "facility");

			// Plant open decision variables: open[p] == 1 if plant p is open.
			GRBVar[] y = new GRBVar[facilities];
			for (int i = 0; i < facilities; ++i) {
				y[i] = model.addVar(0, 1, f[i], GRB.CONTINUOUS, "Open" + i);
			}

			// Transportation decision variables: how much to transport from
			// a plant p to a warehouse w
			GRBVar[][] x = new GRBVar[clients][facilities];
			for (int j = 0; j < clients; ++j) {
				for (int i = 0; i < facilities; ++i) {
					x[j][i] = model.addVar(0, 1, d[j][i]*w[j], GRB.CONTINUOUS, "Trans" + i
							+ "." + j);
				}
			}

			// The objective is to minimize the total fixed and variable costs
			model.set(GRB.IntAttr.ModelSense, 1);

			// Update model to integrate new variables
			model.update();

			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < facilities; i++) {
				for (int j = 0; j < clients; j++) {
					expr.addTerm(w[j] * d[j][i], x[j][i]);
					
				}
				expr.addTerm(f[i], y[i]);
			}

			model.setObjective(expr, GRB.MINIMIZE);

			for (int i = 0; i < facilities; ++i) {
				for (int j = 0; j < clients; ++j) {
					model.addConstr(y[i], GRB.GREATER_EQUAL, x[j][i], "open_"
							+ i + "_" + j);
				}
			}

			for (int j = 0; j < clients; ++j) {
				expr = new GRBLinExpr();
				for (int i = 0; i < facilities; ++i) {
					expr.addTerm(1.0, x[j][i]);
				}
				model.addConstr(expr, GRB.EQUAL, 1, "Covering_" + j);
			}

			// Use barrier to solve root relaxation
			model.getEnv().set(GRB.IntParam.Method, GRB.METHOD_BARRIER);
			
			model.relax();

			model.update();

			
			
			// Solve
			model.optimize();

			solution = model.get(GRB.DoubleAttr.ObjVal);
			
			for (int i = 0; i < facilities; ++i) {
				yS[i] = y[i].get(GRB.DoubleAttr.X);
			}

			for (int i = 0; i < facilities; i++) {
				for (int j = 0; j < clients; ++j) {
					xS[j][i] = x[j][i].get(GRB.DoubleAttr.X);
				}
			}

			// Dispose of model and environment
			model.dispose();
			env.dispose();

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". "
					+ e.getMessage());
		}
	}
}
