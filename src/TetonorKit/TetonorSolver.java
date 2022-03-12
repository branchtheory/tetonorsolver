package TetonorKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TetonorSolver {

    final int[] GRID16;
    final int[] LINE16;
    final int PRODUCT_SIGNIFIER = 111;
    final int SUM_SIGNIFIER = 5;
    final int UNUSED = 0;
    final int DONE = 1;
    final int NUMBEROFQUADVALUES = 6;
    final int NUMBEROFQUADS = 8;
    final int NUMBEROFGRIDITEMS = 16;
    final int[] emptyQuad = {0,0,0,0,0,0};
    final int[][][] BROKENBRANCH = new int[16][8][6];
    final int NOT_FOUND = -1;
    boolean thereCouldBeAnotherSingleToTackle = true;
    boolean solutionFound = false;

    int[][][] potentialQuads = new int[16][8][6];

    HashMap<Integer, int[][][]> branches = new HashMap<>();
    int newBranchKey = 2;
    int currentBranch = 1;

    ArrayList<String> branchesExisting = new ArrayList<String>();

    public TetonorSolver(int[] grid, int[] line) {
        Arrays.sort(grid);
        GRID16 = grid;
        LINE16 = line;

                                    System.out.println("PREP\n");

        potentialQuads = findInitialQuads(GRID16);
                                    System.out.println("potentialQuads");
                                    System.out.println(Arrays.deepToString(potentialQuads)+"\n");
    }

    public int[][][] mainSolverLoop() {
        while(thereCouldBeAnotherSingleToTackle) {
            potentialQuads = singleQuads(potentialQuads);
                if(thereCouldBeAnotherSingleToTackle) {
                                    System.out.println("potentialQuads after singles");
                                    System.out.println(Arrays.deepToString(potentialQuads) + "\n");
                }
        }

        branches.put(1, potentialQuads);

        branchSplitter(branches.get(currentBranch));
        currentBranch++;

                                    System.out.println("MAIN LOOP\n");

        while (!solutionFound) {
            //problem : I have no idea where on earth the 1 valiue is coming from for the third gridItem. When it's split it's not there
            //but when we do the After split thing it randomly appears
                                    System.out.println("BRANCH " + currentBranch + "\n");
                                    System.out.println("Initial");
                                    System.out.println(Arrays.deepToString(branches.get(currentBranch)) + "\n");

            postSplit(branches.get(currentBranch));
                                    System.out.println("After split");
                                    System.out.println(Arrays.deepToString(branches.get(currentBranch)) + "\n");

            do {
                if (branchIsBroken(branches.get(currentBranch))) {
                    break;
                }
                singleQuads(branches.get(currentBranch));
                if (thereCouldBeAnotherSingleToTackle) {
                                    System.out.println("After singles");
                                    System.out.println(Arrays.deepToString(branches.get(currentBranch)) + "\n");
                }
            } while (thereCouldBeAnotherSingleToTackle);
            if (branchIsBroken(branches.get(currentBranch))) {
                                    System.out.println("<>Branch broken<>\n");
                branches.remove(currentBranch);
                currentBranch++;
                                    printExisitingBranches();
                continue;
            }

            if(solutionChecker(branches.get(currentBranch))) return branches.get(currentBranch);

            branchSplitter(branches.get(currentBranch));
            currentBranch++;

                                    printExisitingBranches();
        }
        return branches.get(currentBranch);
    }

    public int[][][] findInitialQuads(int[] grid16) {
        int gridPair1;
        int gridPair2;
        int linePair2;
        int addOrMultiply;
        double linePairBreakEvenPoint;
        boolean linePair2IsAWholeNumber;
        boolean gridPair2ActuallyExistsInGrid16;

        int quadNumber;

        for (int gridPair1Index = 0; gridPair1Index < grid16.length; gridPair1Index++) {
            quadNumber = 0;
            gridPair1 = grid16[gridPair1Index];

            addOrMultiply = PRODUCT_SIGNIFIER;

            linePairBreakEvenPoint = Math.sqrt(grid16[gridPair1Index]) + 1;

            for (int linePair1 = 1; linePair1 < linePairBreakEvenPoint; linePair1++) {
                linePair2 = gridPair1 / linePair1;
                gridPair2 = linePair1 + linePair2;

                linePair2IsAWholeNumber = isLinePair2AWholeNumber(gridPair1, linePair1);
                gridPair2ActuallyExistsInGrid16 = doesGridPair2ActuallyExist(grid16, gridPair2,
                                                                 false, gridPair1Index);

                if (linePair2IsAWholeNumber && gridPair2ActuallyExistsInGrid16) {
                    setQuadValues(linePair1, linePair2, gridPair1, gridPair2, quadNumber, gridPair1Index, addOrMultiply);
                    quadNumber++;
                }
            }

            addOrMultiply = SUM_SIGNIFIER;

            linePairBreakEvenPoint = (double) (gridPair1 / 2) + 1;

            for (int linePair1 = 1; linePair1 < linePairBreakEvenPoint; linePair1++) {

                linePair2 = gridPair1 - linePair1;
                gridPair2 = linePair1 * linePair2;

                gridPair2ActuallyExistsInGrid16 = doesGridPair2ActuallyExist(grid16, gridPair2, false, gridPair1Index);

                if (gridPair2ActuallyExistsInGrid16) {
                    setQuadValues(linePair1, linePair2, gridPair1, gridPair2, quadNumber, gridPair1Index, addOrMultiply);
                    quadNumber++;
                }
            }
        }
        return potentialQuads;
    }

    public void branchSplitter(int[][][] branchToSplit){
        int gridItemToSplit = findGridItemToSplit(branchToSplit);

        //create a new branch with of the gridItems
        for(int quad = 0; quad < NUMBEROFQUADS; quad++) {
            if(isAnEmptyQuad(branchToSplit[gridItemToSplit][quad])) break;

            int[][][] branchCopy = new int[16][8][6];

            // copy potQuads
            for (int gridItem = 0; gridItem < NUMBEROFGRIDITEMS; gridItem++) {
                for (int quadI = 0; quadI < NUMBEROFQUADS; quadI++) {
                    for (int quadValue = 0; quadValue < NUMBEROFQUADVALUES; quadValue++)
                        branchCopy[gridItem][quadI][quadValue] = branchToSplit[gridItem][quadI][quadValue];
                }
            }

            for (int quadValue = 0; quadValue < NUMBEROFQUADVALUES; quadValue++)
                branchCopy[gridItemToSplit][0][quadValue] = branchCopy[gridItemToSplit][quad][quadValue];

            for (int quadI = 1; quadI < NUMBEROFQUADS; quadI++)
                for (int quadValue = 0; quadValue < NUMBEROFQUADVALUES; quadValue++)
                    branchCopy[gridItemToSplit][quadI][quadValue] = 0;

            branches.put(newBranchKey, branchCopy);
                                    System.out.println("New branch " + newBranchKey + " created from branch " + currentBranch);
                                    System.out.println(Arrays.deepToString(branchCopy) + "\n");
            newBranchKey++;
        }
        branches.remove(currentBranch);
    }

    private int findGridItemToSplit(int[][][] branchToSplit) {
        for (int gridItem = 0; gridItem < NUMBEROFGRIDITEMS; gridItem++) {
            if (branchToSplit[gridItem][0][5] == UNUSED) {
                return gridItem;
            }
        }
        return NOT_FOUND;
    }

    public int[][][] quadSorter (int[][][] potQ, int gridItem){
        int quad;
        boolean currentQuadIsEmpty;
        boolean nextQuadIsLive;
        boolean nextQuadIsEmpty;
        boolean nextNextQuadIsLive;
        int[] theNextLiveQuad;
        int[] theNextestLiveQuad;

        for (quad = 0; quad < 5; quad++) {
            currentQuadIsEmpty = potQ[gridItem][quad][0] == 0;
            nextQuadIsLive = potQ[gridItem][quad+1][0] != 0;
            nextQuadIsEmpty = potQ[gridItem][quad+1][0] == 0;
            nextNextQuadIsLive = potQ[gridItem][quad+2][0] != 0;
            theNextLiveQuad = potQ[gridItem][quad+1];
            theNextestLiveQuad = potQ[gridItem][quad+2];

            if (currentQuadIsEmpty
                    && nextQuadIsLive) {
                potQ[gridItem][quad] = theNextLiveQuad;
                potQ[gridItem][quad+1] = emptyQuad;
            } else if (quad < 4 &&
                    currentQuadIsEmpty
                    && nextQuadIsEmpty
                    && nextNextQuadIsLive) {
                potQ[gridItem][quad] = theNextestLiveQuad;
                potQ[gridItem][quad+2] = emptyQuad;
            }
        }
        return potQ;
    }

    public int[][][] postSplit(int[][][] potQuads) {
        int singleQuadItemGrid16Value;
        int singleQuadItemIndex;
        int correspondingItemGrid16Value;
        int correspondingItemIndex;

        //find item and assign values to find the other

        singleQuadItemIndex = findTheSingleQuadAndGetItsIndex(potQuads);
        singleQuadItemGrid16Value = GRID16[singleQuadItemIndex];

        correspondingItemGrid16Value = getCorrespondingItemGrid16Value(potQuads, singleQuadItemGrid16Value, singleQuadItemIndex);
        correspondingItemIndex = getCorrespondingItemIndex(correspondingItemGrid16Value);

        if (correspondingItemIndex == NOT_FOUND) {
            potQuads = BROKENBRANCH;
            return potQuads;
        }

        potQuads = removeAllQuadsInTheCorrespondingGridItemExceptTheOneThatLinksWithTheSingle(potQuads, singleQuadItemIndex, correspondingItemIndex);
        potQuads = removeAllLinksToAGridItem(potQuads, correspondingItemGrid16Value, correspondingItemIndex);
        potQuads = removeAllLinksToAGridItem(potQuads, singleQuadItemGrid16Value, singleQuadItemIndex);

        return potQuads;
    }

    public int[][][] singleQuads(int[][][] potQuads) {
        int singleQuadItemGrid16Value;
        int singleQuadItemIndex;
        int correspondingItemGrid16Value;
        int correspondingItemIndex;

        singleQuadItemIndex = findTheSingleQuadAndGetItsIndex(potQuads);

        if (singleQuadItemIndex == NOT_FOUND) {
            thereCouldBeAnotherSingleToTackle = false;
            return potQuads;
        }
        else {
            thereCouldBeAnotherSingleToTackle = true;
        }

        singleQuadItemGrid16Value = GRID16[singleQuadItemIndex];

        correspondingItemGrid16Value = getCorrespondingItemGrid16Value(potQuads, singleQuadItemGrid16Value, singleQuadItemIndex);
        correspondingItemIndex = getCorrespondingItemIndex(correspondingItemGrid16Value);

        potQuads = removeAllQuadsInTheCorrespondingGridItemExceptTheOneThatLinksWithTheSingle(potQuads, singleQuadItemIndex, correspondingItemIndex);
        potQuads = removeAllLinksToAGridItem(potQuads, correspondingItemGrid16Value, correspondingItemIndex);
        return potQuads;
    }

    private int[][][] removeAllLinksToAGridItem(int[][][] potQuads, int grid16Value, int itemIndex) {
        if (itemIndex == 15 || grid16Value != GRID16[itemIndex +1]) {
            for (int gridItem = 0; gridItem < GRID16.length; gridItem++) {
                for (int quad = 0; quad < NUMBEROFQUADS; quad++) {
                    if (potQuads[gridItem][quad][5] == UNUSED && !isAnEmptyQuad(potQuads[gridItem][quad])) {
                        if (potQuads[gridItem][quad][1] == grid16Value
                                || potQuads[gridItem][quad][2] == grid16Value) {
                            potQuads[gridItem][quad] = emptyQuad;
                            quadSorter(potQuads, gridItem);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return potQuads;
    }

    private int[][][] removeAllQuadsInTheCorrespondingGridItemExceptTheOneThatLinksWithTheSingle(int[][][] potQuads, int singleQuadItemIndex, int correspondingItemIndex) {
        potQuads[singleQuadItemIndex][0][5] = DONE;
        potQuads[correspondingItemIndex][0] = Arrays.copyOf(potQuads[singleQuadItemIndex][0], NUMBEROFQUADVALUES);
        potQuads[correspondingItemIndex][0][0] = potQuads[singleQuadItemIndex][0][0] == SUM_SIGNIFIER ? PRODUCT_SIGNIFIER : SUM_SIGNIFIER;
        for (int quad = 1; quad < NUMBEROFQUADS; quad++) {
            potQuads[correspondingItemIndex][quad] = emptyQuad;
        }
        return potQuads;
    }

    private int getCorrespondingItemGrid16Value(int[][][] potQuads, int singleQuadItemGrid16Value, int singleQuadItemIndex) {
        return singleQuadItemGrid16Value ==
                potQuads[singleQuadItemIndex][0][1] ? potQuads[singleQuadItemIndex][0][2] : potQuads[singleQuadItemIndex][0][1];
    }

    private int getCorrespondingItemIndex(int correspondingItemGrid16Value) {
        for (int gridItem = 0; gridItem < GRID16.length; gridItem++) {
            if (GRID16[gridItem] == correspondingItemGrid16Value) {
                return gridItem;
            }
        }
        return NOT_FOUND;
    }

    private int findTheSingleQuadAndGetItsIndex(int[][][] potQuads) {
        for (int gridItem = 0; gridItem < GRID16.length; gridItem++) {
            if (potQuads[gridItem][0][5] == UNUSED && isAnEmptyQuad(potQuads[gridItem][1])) {
                                  System.out.println("Single found at " + gridItem + ": " + Arrays.deepToString(potQuads[gridItem]) + "\n");
                return gridItem;
            }
        }
        return NOT_FOUND;
    }

    public boolean branchIsBroken(int[][][] branch) {
        for(int gridItem = 0; gridItem < NUMBEROFGRIDITEMS; gridItem++) {
            if (isAnEmptyGridItem(branch[gridItem])) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnEmptyGridItem(int[][] gridItem) {
        return gridItem[0][0] == 0;
    }

    public boolean solutionChecker(int[][][] branch) {
        for (int gridItem = 0; gridItem < NUMBEROFGRIDITEMS; gridItem++) {
            if (branch[gridItem][0][5] != DONE) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnEmptyQuad(int[] quad) {
        return quad[0] == 0;
    }

    private boolean isLinePair2AWholeNumber(int gridPair1, int linePair1) {
        return gridPair1 % linePair1 == 0;
    }

    private boolean doesGridPair2ActuallyExist(int[] grid16, int gridPair2, boolean gridPair2IsReal, int gridPair1Index) {
        boolean gridPair2ActuallyAppearsInGrid16;
        boolean gridPair1and2AreDifferentGridItems;
        for (int gridItemIndex = 0; gridItemIndex < grid16.length; gridItemIndex++) {
            gridPair2ActuallyAppearsInGrid16 = grid16[gridItemIndex] == gridPair2;
            gridPair1and2AreDifferentGridItems = gridItemIndex != gridPair1Index;

            if (gridPair2ActuallyAppearsInGrid16 &&
                    gridPair1and2AreDifferentGridItems) {
                gridPair2IsReal = true;
                break;
            }
        }
        return gridPair2IsReal;
    }

    private void setQuadValues(int linePair1, int linePair2, int gridPair1, int gridPair2,
                               int quadNumber, int gridPair1Index, int operation) {
        potentialQuads[gridPair1Index][quadNumber][0] = operation;
        potentialQuads[gridPair1Index][quadNumber][1] = Math.max(gridPair1, gridPair2);
        potentialQuads[gridPair1Index][quadNumber][2] = Math.min(gridPair1, gridPair2);
        potentialQuads[gridPair1Index][quadNumber][3] = linePair1;
        potentialQuads[gridPair1Index][quadNumber][4] = linePair2;
    }

    private void printExisitingBranches() {
        branchesExisting.clear();
        for (int branchValue = 1; branchValue <= newBranchKey; branchValue++) {
            if (branches.get(branchValue) != null) {
                branchesExisting.add(""+branchValue);
            }
        }
        System.out.println("Current branches");
        System.out.println(String.join(", ", branchesExisting) + "\n");
    }
}