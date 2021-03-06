/*************************************************************************
 *  Full credit to http://algs4.cs.princeton.edu/65reductions/Simplex.java.html from 
 *  http://algs4.cs.princeton.edu/65reductions/ by Robert Sedgewick and Kevin Wayne.

 *  Compilation:  javac Simplex.java
 *  Execution:    java Simplex
 *
 *  Given an M-by-N matrix A, an M-length vector b, and an
 *  N-length vector c, solve the  LP { max cx : Ax <= b, x >= 0 }.
 *  Assumes that b >= 0 so that x = 0 is a basic feasible solution.
 *
 *  Creates an (M+1)-by-(N+M+1) simplex tableaux with the 
 *  RHS in column M+N, the objective function in row M, and
 *  slack variables in columns M through M+N-1.
 *
 *************************************************************************/

public class Simplex {
    private static final double EPSILON = 1.0E-10;
    private double[][] a;   // tableaux
    private int M;          // number of constraints
    private int N;          // number of original variables

    private int[] basis;    // basis[i] = basic variable corresponding to row i
                            // only needed to print out solution, not book

    // sets up the simplex tableaux
    public Simplex(double[][] A, double[] b, double[] c) {
        M = b.length;
        N = c.length;
        a = new double[M+1][N+M+1];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = A[i][j];
        for (int i = 0; i < M; i++) a[i][N+i] = 1.0;
        for (int j = 0; j < N; j++) a[M][j]   = c[j];
        for (int i = 0; i < M; i++) a[i][M+N] = b[i];

        basis = new int[M];
        for (int i = 0; i < M; i++) basis[i] = N + i;


//this is just moving the show() method
	System.out.println("M = " + M);
        System.out.println("N = " + N);
        for (int i = 0; i <= M; i++) {
            for (int j = 0; j <= M + N; j++) {
                System.out.printf("%7.2f ", a[i][j]);
            }
            System.out.println();
        }
        System.out.println("value = " + value());
        for (int i = 0; i < M; i++)
            if (basis[i] < N) System.out.println("x_" + basis[i] + " = " + a[i][M+N]);
        System.out.println();






        solve();

        // check optimality conditions
        assert check(A, b, c);
    }

    // run simplex algorithm starting from initial BFS
    private void solve() {
        while (true) {

            // find entering column q
            int q = bland();
            if (q == -1) break;  // optimal

            // find leaving row p
            int p = minRatioRule(q);
            if (p == -1) throw new ArithmeticException("Linear program is unbounded");

            // pivot
            pivot(p, q);

            // update basis
            basis[p] = q;
        }
    }

    // lowest index of a non-basic column with a positive cost
    private int bland() {
        for (int j = 0; j < M + N; j++)
            if (a[M][j] > 0) return j;
        return -1;  // optimal
    }

   // index of a non-basic column with most positive cost
    private int dantzig() {
        int q = 0;
        for (int j = 1; j < M + N; j++)
            if (a[M][j] > a[M][q]) q = j;

        if (a[M][q] <= 0) return -1;  // optimal
        else return q;
    }

    // find row p using min ratio rule (-1 if no such row)
    private int minRatioRule(int q) {
        int p = -1;
        for (int i = 0; i < M; i++) {
            if (a[i][q] <= 0) continue;
            else if (p == -1) p = i;
            else if ((a[i][M+N] / a[i][q]) < (a[p][M+N] / a[p][q])) p = i;
        }
        return p;
    }

    // pivot on entry (p, q) using Gauss-Jordan elimination
    private void pivot(int p, int q) {

        // everything but row p and column q
        for (int i = 0; i <= M; i++)
            for (int j = 0; j <= M + N; j++)
                if (i != p && j != q) a[i][j] -= a[p][j] * a[i][q] / a[p][q];

        // zero out column q
        for (int i = 0; i <= M; i++)
            if (i != p) a[i][q] = 0.0;

        // scale row p
        for (int j = 0; j <= M + N; j++)
            if (j != q) a[p][j] /= a[p][q];
        a[p][q] = 1.0;
    }

    // return optimal objective value
    public double value() {
        return -a[M][M+N];
    }

    // return primal solution vector
    public double[] primal() {
        double[] x = new double[N];
        for (int i = 0; i < M; i++)
            if (basis[i] < N) x[basis[i]] = a[i][M+N];
        return x;
    }

    // return dual solution vector
    public double[] dual() {
        double[] y = new double[M];
        for (int i = 0; i < M; i++)
            y[i] = -a[M][N+i];
        return y;
    }


    // is the solution primal feasible?
    private boolean isPrimalFeasible(double[][] A, double[] b) {
        double[] x = primal();

        // check that x >= 0
        for (int j = 0; j < x.length; j++) {
            if (x[j] < 0.0) {
                System.out.println("x[" + j + "] = " + x[j] + " is negative");
                return false;
            }
        }

        // check that Ax <= b
        for (int i = 0; i < M; i++) {
            double sum = 0.0;
            for (int j = 0; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            if (sum > b[i] + EPSILON) {
                System.out.println("not primal feasible");
                System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // is the solution dual feasible?
    private boolean isDualFeasible(double[][] A, double[] c) {
        double[] y = dual();

        // check that y >= 0
        for (int i = 0; i < y.length; i++) {
            if (y[i] < 0.0) {
                System.out.println("y[" + i + "] = " + y[i] + " is negative");
                return false;
            }
        }

        // check that yA >= c
        for (int j = 0; j < N; j++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++) {
                sum += A[i][j] * y[i];
            }
            if (sum < c[j] - EPSILON) {
                System.out.println("not dual feasible");
                System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
                return false;
            }
        }
        return true;
    }

    // check that optimal value = cx = yb
    private boolean isOptimal(double[] b, double[] c) {
        double[] x = primal();
        double[] y = dual();
        double value = value();

        // check that value = cx = yb
        double value1 = 0.0;
        for (int j = 0; j < x.length; j++)
            value1 += c[j] * x[j];
        double value2 = 0.0;
        for (int i = 0; i < y.length; i++)
            value2 += y[i] * b[i];
        if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
            System.out.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
            return false;
        }

        return true;
    }

    private boolean check(double[][]A, double[] b, double[] c) {
        return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
    }

    // print tableaux
    public void show() {
        System.out.println("M = " + M);
        System.out.println("N = " + N);
        for (int i = 0; i <= M; i++) {
            for (int j = 0; j <= M + N; j++) {
                System.out.printf("%7.2f ", a[i][j]);
            }
            System.out.println();
        }
        System.out.println("value = " + value());
        for (int i = 0; i < M; i++)
            if (basis[i] < N) System.out.println("x_" + basis[i] + " = " + a[i][M+N]);
        System.out.println();
    }


    public static void test(double[][] A, double[] b, double[] c) {
        Simplex lp = new Simplex(A, b, c);
        System.out.println("value = " + lp.value());
        double[] x = lp.primal();
        for (int i = 0; i < x.length; i++)
            System.out.println("x[" + i + "] = " + x[i]);
        double[] y = lp.dual();
        for (int j = 0; j < y.length; j++)
            System.out.println("y[" + j + "] = " + y[j]);
    }

	// football test
    public static void testFootball() {
//Points per player TODO
	double[] c = { 10.2, 8.1,  7.99,  7.0, 6.0, 3.0, 2.0 };
      

//Max roster salary and number of players at each position ordered
//by QB, RB, WR, TE, K, D
        //double[] b = {  60000.0, 1.0, 2.0, 3.0, 1.0, 1.0, 1.0 };
	double[] b = { 60000.0, 1.0, 2.0, 1.0};

//First row is salary per player, next rows have a 1 for each player from 
//the relevant position, 0 otherwise
        double[][] A = {
            { 40000.0, 30000.0, 20000.0, 10000.0, 5000.0, 10000.0, 5000.0 },

            
	    {  1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
	    {  0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0 },
            {  0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 },
/*
            {  1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
	    {  0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0 },
            {  0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 },
*/
        };
        test(A, b, c);
    }

	// football test
    public static void testBigFootball() {
//Points per player TODO
	double[] c = { 25.89, 24.44, 23.93, 21.11, 19.49, 19.47, 19.24, 18.31, 17.89, 17.86, 17.52, 17.20, 16.98, 16.62, 16.60,
	               21.76, 20.14, 19.83, 19.00, 16.64, 14.79, 14.45, 14.04, 13.98, 13.55, 12.45, 12.21, 12.15, 11.86, 11.02,
                       18.38, 18.25, 17.62, 17.37, 16.38, 16.04, 15.53, 14.82, 14.58, 14.41, 14.08, 13.39, 13.09, 12.91, 12.80,
                       15.39, 14.64, 13.46, 12.07, 11.56, 11.55, 10.63, 10.47,  8.24,  8.06,  7.81,  7.60,  7.50,  6.87,  6.80, 
                       10.00, 10.69 };
      

//Max roster salary and number of players at each position ordered
//by QB, RB, WR, TE, K, D
        //double[] b = {  60000.0, 1.0, 2.0, 3.0, 1.0, 1.0, 1.0 };
	double[] b = { 60000.0, 1.0, 2.0, 3.0, 1.0, 1.0, 1.0};

//First row is salary per player, next rows have a 1 for each player from 
//the relevant position, 0 otherwise
        double[][] A = {
            { 10300.0, 10250.0, 10200.0, 9900.0, 9800.0, 9700.0, 9600.0, 8200.0, 8100.0, 8050.0, 8000.0, 7500.0, 7300.0, 7200.0, 7000.0, 
               9000.0, 8900.0,   8850.0, 8800.0, 8500.0, 8400.0, 8300.0, 8200.0, 7700.0, 7600.0, 6600.0, 6500.0, 6300.0, 6100.0, 6000.0, 
               9000.0, 8800.0,   8700.0, 8600.0, 8500.0, 8400.0, 8200.0, 8100.0, 7900.0, 7700.0, 7400.0, 6900.0, 6700.0, 5000.0, 4500.0,  
               7900.0, 7500.0,   7100.0, 6300.0, 6100.0, 5900.0, 5500.0, 5400.0, 5300.0, 5200.0, 5100.0, 5000.0, 4700.0, 4500.0, 4000.0, 
               5000.0, 5000.0},

            
	    {  1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 0.0},

	    {  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 0.0},

	    {  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 0.0},

            {  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
		0.0, 0.0},

	    {  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		1.0, 0.0},

            {  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
               0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
		0.0, 1.0},

/*
            {  1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
	    {  0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0 },
            {  0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 },
*/
        };
        test(A, b, c);
    }


    public static void test1() {
        double[][] A = {
            { -1,  1,  0 },
            {  1,  4,  0 },
            {  2,  1,  0 },
            {  3, -4,  0 },
            {  0,  0,  1 },
        };
        double[] c = { 1, 1, 1 };
        double[] b = { 5, 45, 27, 24, 4 };
        test(A, b, c);
    }


    // x0 = 12, x1 = 28, opt = 800
    public static void test2() {
        double[] c = {  13.0,  23.0 };
        double[] b = { 480.0, 160.0, 1190.0 };
        double[][] A = {
            {  5.0, 15.0 },
            {  4.0,  4.0 },
            { 35.0, 20.0 },
        };
        test(A, b, c);
	
    }

    // unbounded
    public static void test3() {
        double[] c = { 2.0, 3.0, -1.0, -12.0 };
        double[] b = {  3.0,   2.0 };
        double[][] A = {
            { -2.0, -9.0,  1.0,  9.0 },
            {  1.0,  1.0, -1.0, -2.0 },
        };
        test(A, b, c);
    }

    // degenerate - cycles if you choose most positive objective function coefficient
    public static void test4() {
        double[] c = { 10.0, -57.0, -9.0, -24.0 };
        double[] b = {  0.0,   0.0,  1.0 };
        double[][] A = {
            { 0.5, -5.5, -2.5, 9.0 },
            { 0.5, -1.5, -0.5, 1.0 },
            { 1.0,  0.0,  0.0, 0.0 },
        };
        test(A, b, c);
    }



    // test client
    public static void main(String[] args) {

/*	
        try                           { test1();             }
        catch (ArithmeticException e) { e.printStackTrace(); }
        System.out.println("--------------------------------");
*/

        try                           { test2();             }
        catch (ArithmeticException e) { e.printStackTrace(); }
        System.out.println("--------------------------------");

/*
        try                           { test3();             }
        catch (ArithmeticException e) { e.printStackTrace(); }
        System.out.println("--------------------------------");

        try                           { test4();             }
        catch (ArithmeticException e) { e.printStackTrace(); }
        System.out.println("--------------------------------");


	try                           { 
		long startTime = System.currentTimeMillis();
		testBigFootball();
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	 }
        catch (ArithmeticException e) { e.printStackTrace(); }
        System.out.println("--------------------------------");




        int M = Integer.parseInt(args[0]);
        int N = Integer.parseInt(args[1]);
        double[] c = new double[N];
        double[] b = new double[M];
        double[][] A = new double[M][N];
        for (int j = 0; j < N; j++)
            c[j] = StdRandom.uniform(1000);
        for (int i = 0; i < M; i++)
            b[i] = StdRandom.uniform(1000);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A[i][j] = StdRandom.uniform(100);
        Simplex lp = new Simplex(A, b, c);
        System.out.println(lp.value());
*/
    }


}