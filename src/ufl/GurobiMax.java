package ufl;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

class GurobiMax {

	private double solution;
	private double[] vS;
	private double[][] betaS;

	public GurobiMax(int clients, int facilities) {
		vS = new double[clients];
		betaS = new double[clients][facilities];
		solution = 0;
	}

	public double getSolution() {
		return this.solution;
	}

	public double[][] getBeta() {
		return this.betaS;
	}

	public double[] getV() {
		return this.vS;
	}

	public void gurobiFacilityMax(double[] w1, double[] f1, double[][] d1) {
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

			GRBVar[] v = new GRBVar[clients];
			for (int j = 0; j < clients; ++j) {
				v[j] = model.addVar(0, GRB.INFINITY, 0,
						GRB.CONTINUOUS, "Open" + j);
			}

			GRBVar[][] B = new GRBVar[clients][facilities];
			for (int i = 0; i < facilities; ++i) {
				for (int j = 0; j < clients; ++j) {
					B[j][i] = model.addVar(0, GRB.INFINITY, 0,
							GRB.CONTINUOUS, "Trans" + i + "." + j);
				}
			}

			// The objective is to maximize the total fixed and variable costs
			model.set(GRB.IntAttr.ModelSense, -1);

			model.update();

			GRBLinExpr expr = new GRBLinExpr();
			for (int j = 0; j < clients; j++) {
				expr.addTerm(1.0, v[j]);
			}
			model.setObjective(expr, GRB.MAXIMIZE);

			for (int i = 0; i < facilities; ++i) {
				GRBLinExpr expr2 = new GRBLinExpr();
				for (int j = 0; j < clients; ++j) {
					expr2.addTerm(1.0, B[j][i]);
				}
				model.addConstr(expr2, GRB.LESS_EQUAL, f[i], "Covering_" + i);
			}

			for (int j = 0; j < clients; ++j) {
				for (int i = 0; i < facilities; ++i) {
					model.addConstr(B[j][i], GRB.GREATER_EQUAL, 0, "Cond_" + i
							+ "_" + j);
				}
			}

			for (int j = 0; j < clients; ++j) {				
				for (int i = 0; i < facilities; ++i) {
					GRBLinExpr expr3 = new GRBLinExpr();
					expr3.addTerm(1.0, v[j]);
					expr3.addTerm(-1.0, B[j][i]);
					double value = w[j] * d[j][i];
					model.addConstr(expr3, GRB.LESS_EQUAL, value, "Close_" + i
							+ "_" + j);
				}
			}
			
			model.relax();

			// Use barrier to solve root relaxation
			model.getEnv().set(GRB.IntParam.Method, GRB.METHOD_BARRIER);
			
			model.update();
			
			

			// Solve
			model.optimize();

			solution = model.get(GRB.DoubleAttr.ObjVal);

			for (int j = 0; j < clients; ++j) {
				vS[j] = v[j].get(GRB.DoubleAttr.X);
			}

			for (int i = 0; i < facilities; i++) {
				for (int j = 0; j < clients; ++j) {
					betaS[j][i] = B[j][i].get(GRB.DoubleAttr.X);
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
