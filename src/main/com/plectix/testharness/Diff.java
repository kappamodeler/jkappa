package com.plectix.testharness;
import java.util.List;

public class Diff {

    public static void diff(List<String> x, List<String> y) {
        // number of lines of each file
        int M = x.size();
        int N = y.size();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M+1][N+1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M-1; i >= 0; i--) {
            for (int j = N-1; j >= 0; j--) {
                if (x.get(i).equals(y.get(j)))
                    opt[i][j] = opt[i+1][j+1] + 1;
                else 
                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
            }
        }

        // recover LCS itself and print out non-matching lines to standard output
        int i = 0, j = 0;
        while(i < M && j < N) {
            if (x.get(i).equals(y.get(j))) {
                i++;
                j++;
            }
            else if (opt[i+1][j] >= opt[i][j+1]) System.out.println("< " + x.get(i++));
            else                                 System.out.println("> " + y.get(j++));
        }

        // dump out one remainder of one string if the other is exhausted
        while(i < M || j < N) {
            if      (i == M) System.out.println("> " + y.get(j++));
            else if (j == N) System.out.println("< " + x.get(i++));
        }
    }

}