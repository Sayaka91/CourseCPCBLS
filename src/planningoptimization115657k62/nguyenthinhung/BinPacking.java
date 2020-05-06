package planningoptimization115657k62.nguyenthinhung;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainBest;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.IntVar;


public class BinPacking {
	//int N;
	//int H;
	//int W;
	int N = 3;
	int H = 6;
	int W = 4;
	int[] w = {3,3,1};
	int[] h = {2,4,6};
	// int[] w = new int[100];
	// int[] h = new int [100];
	Model model = new Model("BP") ;
	IntVar[] x = new IntVar[100];
	IntVar[] y = new IntVar[100];
	IntVar[] o = new IntVar[100];
	
	public void buid() {
		for(int i = 0;i < N;i++) {
			o[i] = model.intVar("o[" + i + "]", 0,1);
			x[i] = model.intVar("x[" + i + "]", 0,W-1);
			y[i] = model.intVar("y[" + i + "]", 0,H-1);
		}
		
		for(int i=0;i<N-1;i++) {
			for(int j=i+1;j<N;j++) {
				Constraint c1= model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 0));
				Constraint c2= model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
				Constraint c3= model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
				Constraint c4= model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
				Constraint c5= model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				 c1= model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 1));
				 c2= model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], h[j]),"<=",x[i]);
				 c4= model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				 c1= model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 0));
				 c2= model.arithm(model.intOffsetView(x[i], h[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
				 c4= model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				 c1= model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 1));
				 c2= model.arithm(model.intOffsetView(x[i], h[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], h[j]),"<=",x[i]);
				c4= model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
			}
		}
	}
	
	public void Search() {
		Solver s = model.getSolver();
		while (s.solve()) {
			// System.out.println("test");
			for(int i = 0;i < N;i++) {
				System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println("-------------------");
		}
	}

	public void readData(String file_path) {
		try {
			File file = new File(file_path);
            Scanner scanner = new Scanner(file);

            W = scanner.nextInt();
            H = scanner.nextInt();
            int count = 0;
            while (true) {
                int tmp;
                tmp = scanner.nextInt();
                if (tmp == -1) break;
                w[count] = tmp;
                h[count] = scanner.nextInt();
                count++;
            }
            N = count;

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		BinPacking t = new BinPacking();
		//t.readData("./input.txt");
		t.buid();
		t.Search();
	}
}
