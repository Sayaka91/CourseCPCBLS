package localsearch.search;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class sudoku {
    LocalSearchManager mgr;
    VarIntLS[][] x;
    ConstraintSystem S;
    public void buildModel() {
    	mgr= new LocalSearchManager();
    	x= new VarIntLS[9][9];
    	for (int i=0; i<9; i++) {
    		for( int j=0; j<9; j++) {
    			x[i][j]= new VarIntLS(mgr,1,9);
    		}
    	}
    	S= new ConstraintSystem(mgr);
        //constraint on row
        for(int i=0; i<9; i++) {
    	   VarIntLS[] y= new VarIntLS[9];
    	   for(int j=0; j<9; j++) {
    		  y[j]=x[i][j];
    	   }
    	   S.post(new AllDifferent(y));
        }
        // constraint on column
        for(int j=0; j<9; j++) {
     	   VarIntLS[] y= new VarIntLS[9];
     	   for(int i=0; i<9; i++) {
     		  y[i]=x[i][j];
     	   }
     	   S.post(new AllDifferent(y));
        }
        //constraint on sub-square 3X3;
        for(int I=0;I<3; I++ ) {
        	for(int J=0; J<3; J++) {
        		VarIntLS[]y= new VarIntLS[9];
        		int idx=-1;
        		for(int i=0; i<3; i++) {
        			for(int j=0; j<3; j++) {
        				idx++;
        				y[idx]= x[3*I+i][3*J+j];
        			}
        		}
        		S.post(new AllDifferent(y));
        	}
        	
        }
        mgr.close();
    }
    public void gernerateInitialSolution() {
    	for(int i=0;i<9;i++) {
    		
    		for(int j = 0; j<9;j++) {
    			x[i][j].setValuePropagate(j+1);
    		}
    	}
    	
    	
    
    }  
     
    class Move{
    	int i; int j1; int j2;
    	public  Move(int i, int j1, int j2) {
    		this.i = i;
    		this.j1 = j1;
    		this.j2 = j2;
    		
    	}
    }
    private void exploreNeighborhood(ArrayList<Move> cand) {
    	cand.clear();
    	int minDelta = Integer.MAX_VALUE;
    	for( int i = 0;i<9;i++) {
    		for( int j1 = 0;j1<8;j1++) {
    			for( int j2 = 0;j2<9;j2++) {
    				int delta = S.getSwapDelta(x[i][j1], x[i][j2]);
    				if(delta<=0) {
    					if(delta < minDelta) {
    						cand.clear();
    						cand.add(new Move(i,j1,j2));
    						minDelta = delta;
    					}else if(delta == minDelta) {
    						cand.add(new Move(i,j1,j2));
    					}
    				}
    			}
    		}
    	}
    	
    }
    public void search() {
    	gernerateInitialSolution();
    	Random R = new Random();
    	ArrayList<Move> cand = new ArrayList<Move>();
    	int it = 0;
    	while(it< 10000 & S.violations() > 0) {
    		exploreNeighborhood(cand);
    		if(cand.size()==0) {
    			System.out.println("Reach local optimum"); break;
    		}
    		Move m = cand.get(R.nextInt(cand.size()));
    		x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
    		System.out.println("Step" + it+ ", S=" + S.violations());
    		it++;
    	}
    	
    }
    public void printSolution() {
    	for( int i = 0;i<9;i++) {
    		for( int j = 0;j<9;j++) {
    			System.out.print(x[i][j].getValue() + " ");
    		}
    		System.out.println();
    	}
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       sudoku app = new sudoku();
       app.buildModel();
       app.search();
       app.printSolution();
	}

}
