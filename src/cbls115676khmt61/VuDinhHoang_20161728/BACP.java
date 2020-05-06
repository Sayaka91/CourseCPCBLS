import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.*;

public class BACP {
    int N = 12;  // so mon hoc 0,1,2...1
    int P = 4;  // so hoc ki 0,1,2,3
    int[] credits = {2,1,2,1,3,2,1,3,2,3,1,3};
    int[][] pre={
            {1,0},
            {5,8},
            {4,5},
            {4,7},
            {3,10},
            {5,11}
    };
    int a = 3;
    int b = 3;
    int c = 5;
    int d = 7;

    // model
    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
    IFunction[] numberCoursesPeriod;    // so mon hoc trong moi hoc ky
    IFunction[] numberCreditsPeriod;    // so tin chi trong moi hoc ky

    public void stateModel(){
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for(int i = 0; i < N; i++){
            X[i] = new VarIntLS(mgr, 0, P-1);
        }

        S = new ConstraintSystem(mgr);
        for (int k = 0; k < pre.length; k++){
            IConstraint c = new LessThan(X[pre[k][0]], X[pre[k][1]]);
            S.post(c);
        }

        numberCoursesPeriod = new IFunction[P];
        numberCreditsPeriod = new IFunction[P];
        for(int j = 0; j < P; j++){
            numberCoursesPeriod[j] = new ConditionalSum(X, j);
            numberCreditsPeriod[j] = new ConditionalSum(X, credits, j);

            S.post(new LessOrEqual(numberCoursesPeriod[j], b));
            S.post(new LessOrEqual(a, numberCoursesPeriod[j]));
            S.post(new LessOrEqual(numberCreditsPeriod[j], d));
            S.post(new LessOrEqual(c, numberCreditsPeriod[j]));
        }

        mgr.close();    // create dependency graph
    }

    public void search(){
        HillClimb searcher = new HillClimb();
        searcher.search(S, 1000);
    }

    public void printResult(){
        for(int j = 0; j < P; j++){
            System.out.print("HK " + j + " :" );
            for(int i = 0; i < N; i++){
                if (X[i].getValue() == j){
                    System.out.print(i + ", ");
                }
            }
            System.out.println("number courses = "+ numberCoursesPeriod[j].getValue() + ", "
                    +"number credits = " + numberCreditsPeriod[j].getValue());
        }
    }

    public static void main(String[] args) {
        BACP app = new BACP();
        app.stateModel();
        app.search();
        app.printResult();
    }
}
