package TetonorKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class testingShit {
    public static void main (String args[]) {
        TetonorSolver solver = new TetonorSolver(new int[]{10, 25, 46, 999, 999, 999, 999,999,999,999,999,999,999,999,999,999},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        System.out.println(Arrays.deepToString(solver.potentialQuads));
        solver.singleQuads(solver.potentialQuads);
        System.out.println(Arrays.deepToString(solver.potentialQuads));
        TetonorSolver solver2 = new TetonorSolver(new int[]{7, 10, 12, 999, 999, 999, 999,999,999,999,999,999,999,999,999,999},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }
}
