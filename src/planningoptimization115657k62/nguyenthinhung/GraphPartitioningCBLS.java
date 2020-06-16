package planningoptimization115657k62.nguyenthinhung;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCBLS {
	int N;
	int[][] c;
	
	LocalSearchManager mgr;
	VarIntLS[] x;
	IConstraint S;
	IFunction f;
	
	Random R = new Random();
	public void genData(String fn, int N, int M){
		try{
			PrintWriter out = new PrintWriter(fn);
			out.println(N);
			c = new int[N][N];
			for(int i = 0; i < N; i++)
				for(int j = 0; j < N; j++)
					c[i][j] = 0;
			for(int k = 1; k <= M; k++){
				int i = R.nextInt(N);
				int j = R.nextInt(N);
				if(i != j){
					int w = R.nextInt(100) + 1;
					c[i][j] = w; c[j][i] = w;
				}
			}
			for(int i = 0; i < N; i++){
				for(int j =0; j < N; j++)
					out.print(c[i][j] + " ");
				out.println();
			}
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void readData(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			N = in.nextInt();
			c = new int[N][N];
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++)
					c[i][j] = in.nextInt();
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void buildModel(){
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		for(int i = 0; i < N; i++) x[i] = new VarIntLS(mgr,0,1);
		S = new IsEqual(new Sum(x), N/2);
		f = new GraphPartitioningCost(c, x);		
		mgr.close();
	}
	public void generateInitSolutionBalance(){
		ArrayList<Integer> L = new ArrayList<Integer>();
		for(int i = 0; i < N; i++) L.add(i);
		for(int k = 0; k < N/2; k++){
			int idx = R.nextInt(L.size());
			int v = L.get(idx);
			x[v].setValuePropagate(1);
			L.remove(idx);
		}
	}
	public String obj(){
		return "S = " + S.violations() + ", f = " + f.getValue();
	}
	class SwapMove{
		int i;
		int j;
		public SwapMove(int i, int j){
			this.i = i; this.j = j;
		}
	}
	public void printGraph(){
		for(int i = 0;i < N; i++){
			System.out.print("A[" + i + "] = ");
			for(int j = 0;j < N; j++) if(c[i][j] > 0) System.out.print(j + "(" + c[i][j] + ") ");
			System.out.println();
		}
	}
	public void printCrossEdges(){
		for(int i = 0; i < N; i++) if(x[i].getValue() == 0) System.out.print(i + " "); System.out.println();
		for(int i = 0; i < N; i++) if(x[i].getValue() == 1) System.out.print(i + " "); System.out.println();
		
		for(int i = 0; i < N; i++)
			for(int j = 0; j < N; j++)
				if(i != j && x[i].getValue() != x[j].getValue() && c[i][j] > 0)
					System.out.println("c[" + i + "," + j + "] = " + c[i][j]);
	}
	public void search1(int maxIter){
		// phase 1: sinh loi giai ban dau thoa man rang buoc:
		generateInitSolutionBalance();
		
		
		printGraph();
		//for(int i = 0; i < N/2; i++) x[i].setValuePropagate(1);
		System.out.println("init obj = " + obj());
		printCrossEdges();
		
		// phase 2: tim kiem cuc bo, duy tinh tinh thoa man rang buoc 
		ArrayList<SwapMove> cand = new ArrayList<SwapMove>();
		int it = 0;
		int cur = f.getValue();
		/*
		int delta = f.getSwapDelta(x[2], x[3]);
		x[2].swapValuePropagate(x[3]);
		System.out.println("cur = " + cur + ", delta = " + delta + ", new = " + f.getValue()
				 + ", x[" + 2 + "] <-> x[" + 3 + "]");
		printCrossEdges();
		if(true) return;
		*/
		
		while(it < maxIter){
			cand.clear();
			// explore neighborhood (kham pha, truy van lang gieng)
			int minDeltaF = Integer.MAX_VALUE;
			for(int i = 0; i < N; i++){
				for(int j = i+1; j < N; j++) if(x[i].getValue() != x[j].getValue()){
					int d = f.getSwapDelta(x[i], x[j]);
					if(d < 0){
						if(d < minDeltaF){
							minDeltaF = d;
							cand.clear();
							cand.add(new SwapMove(i,j));
						}else if(d == minDeltaF){
							cand.add(new SwapMove(i,j));
						}
					}
				}
			}
			if(cand.size() == 0){
				System.out.println("Reach local optimum"); break;
			}
			SwapMove m = cand.get(R.nextInt(cand.size()));
			x[m.i].swapValuePropagate(x[m.j]);// local move (doi gia tri 2 bien cho nhau)
			
			if(minDeltaF + cur != f.getValue()){
				System.out.println("BUG???, cur = " + cur + ", delta = " + minDeltaF + ", f = " + 
			f.getValue() + ", x[" + m.i + "] <-> x[" + m.j + "]");
				break;
			}
			cur = f.getValue();
			System.out.println("Step " + it + ", obj = " + obj());
			it++;
		}
	}
	class AssignMove{
		int i; int v;
		public AssignMove(int i, int v){
			this.i = i; this.v = v;
		}
	}
	public void search2(int maxIter){
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		int it = 0;
		// F(S.violations(), f)
		while(it < maxIter){
			cand.clear();
			int minDeltaS = Integer.MAX_VALUE;
			int minDeltaF = Integer.MAX_VALUE;
			// explore neighborhood (kham pha lang gieng)
			// lang gieng: chon 1 bien va gan gia tri moi
			for(int i = 0; i < N; i++){
				int v = 1 - x[i].getValue();
				int deltaS = S.getAssignDelta(x[i], v);
				int deltaF = f.getAssignDelta(x[i], v);
				if(deltaS < 0 || deltaS == 0 && deltaF < 0){// hill climbing
					if(deltaS < minDeltaS || deltaS == minDeltaS && deltaF < minDeltaF){
						cand.clear();
						minDeltaS = deltaS; minDeltaF = deltaF;
						cand.add(new AssignMove(i,v));
					}else if(deltaS == minDeltaS && deltaF == minDeltaF){
						cand.add(new AssignMove(i,v));
					}
				}
			}
			
			if(cand.size() == 0){
				System.out.println("Reach  local optimum"); break;
			}
			
			AssignMove m = cand.get(R.nextInt(cand.size()));
			
			x[m.i].setValuePropagate(m.v);
			
			System.out.println("Step " + it + ", obj = " + obj());
			it++;
		}
	}
	public void runExpr(){
		int nbRuns = 20;
		int[] rs = new int[nbRuns];
		
		for(int r = 0; r < nbRuns; r++){
			buildModel();
			search2(10000);
			rs[r] = f.getValue();
		}
		for(int i = 0; i < nbRuns; i++){
			System.out.println("Run " + i + ", rs = " + rs[i]);
		}
		int minf = Integer.MAX_VALUE;
		int maxf = 0;
		double avr = 0;
		double std_dev = 0;
		for(int i = 0; i < nbRuns; i++){
			if(minf > rs[i]) minf = rs[i];
			if(maxf < rs[i]) maxf = rs[i];
			avr += rs[i];
		}
		avr = avr*1.0/nbRuns;
		System.out.println("minf = " + minf + " maxf = " + maxf + ", avr = " + avr);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphPartitioningCBLS app = new GraphPartitioningCBLS();
		String fn = "data/GraphPartitioning/gp-1000.txt";
		//app.genData(fn, 6, 8);
		app.readData(fn);
		//app.runExpr();
		app.buildModel();
		//app.search1(10000);
		app.search2(100000);
	}

}
