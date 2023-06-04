package datastructure.graph;

import java.util.Arrays;
import matrix.IntMatrix;

public class Net {
  // The maximal distance.
  public static final int MAX_DISTANCE = 10000;
  // The number of nodes.
  int numNodes;
  // The weight matrix. Use integer.
  IntMatrix weightMatrix;

  /*
   * The first constructor.
   * 
   * @param paraNumNodes. The number of nodes in the graph.
   */
  public Net(int paraNumNodes) {
    numNodes = paraNumNodes;
    weightMatrix = new IntMatrix(numNodes, numNodes);
    for (int i = 0; i < numNodes; i++) {
      Arrays.fill(weightMatrix.getData()[i], MAX_DISTANCE);
    }
  }

  /*
   * The second constructor.
   * 
   * @param paraMatrix. The data matrix.
   */
  public Net(int[][] paraMatrix) {
    weightMatrix = new IntMatrix(paraMatrix);
    numNodes = weightMatrix.getRows();
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "This is the weight matrix of the matrix.\r\n" + weightMatrix;
    return resultString;
  }

  /*
   * The Dijkstra algorithm: shortest path from the source to all nodes.
   * 
   * @param paraSource. The source node.
   * 
   * @return The distances to all nodes.
   */
  public int[] dijkstra(int paraSource) {
    // Step 1. Initialize.
    int[] tempDistanceArray = new int[numNodes];
    for (int i = 0; i < numNodes; i++)
      tempDistanceArray[i] = weightMatrix.getValue(paraSource, i);

    int[] tempParentArray = new int[numNodes];
    Arrays.fill(tempParentArray, paraSource);
    tempParentArray[paraSource] = -1;

    // Visited nodes will not be considered further.
    boolean[] tempVisitedArray = new boolean[numNodes];
    tempVisitedArray[paraSource] = true;

    // Step 2. Main loops.
    int tempMinDistance;
    int tempBestNode = -1;
    for (int i = 0; i < numNodes - 1; i++) {
      // Step 2.1. Find out the best next node.
      tempMinDistance = Integer.MAX_VALUE;
      for (int j = 0; j < numNodes; j++) {
        if (tempVisitedArray[j])
          continue;
        if (tempMinDistance > tempDistanceArray[j]) {
          tempMinDistance = tempDistanceArray[j];
          tempBestNode = j;
        }
      }
      // Visit the best node.
      tempVisitedArray[tempBestNode] = true;

      // Step 2.2. Prepare for the next round.
      for (int j = 0; j < numNodes; j++) {
        if (tempVisitedArray[j])
          // Visited.
          continue;
        if (weightMatrix.getValue(tempBestNode, j) >= MAX_DISTANCE)
          // Disconnected.
          continue;
        if (tempDistanceArray[j] > tempDistanceArray[tempBestNode] + weightMatrix.getValue(tempBestNode, j)) {
          // Update if a closer path found.
          tempDistanceArray[j] = tempDistanceArray[tempBestNode] + weightMatrix.getValue(tempBestNode, j);
          tempParentArray[j] = tempBestNode;
        }
      }

      System.out.println("The distance to each node: " + Arrays.toString(tempDistanceArray));
      System.out.println("The parent of each node: " + Arrays.toString(tempParentArray));
    }
    // Step 3. Output for debug.
    System.out.println("Finally.");
    System.out.println("The distance to each node: " + Arrays.toString(tempDistanceArray));
    System.out.println("The parent of each node: " + Arrays.toString(tempParentArray));
    return tempDistanceArray;
  }

  /*
   * The minimal spanning tree. Find the closest node each time.
   * 
   * @return The total cost of the tree.
   */
  public int prim() {
    // Step 1. Initialize.
    // Any node can be the source.
    int tempSource = 0;
    int[] tempDistanceArray = new int[numNodes];
    for (int i = 0; i < numNodes; i++)
      tempDistanceArray[i] = weightMatrix.getValue(tempSource, i);

    int[] tempParentArray = new int[numNodes];
    Arrays.fill(tempParentArray, tempSource);
    tempParentArray[tempSource] = -1;

    // Visited nodes will not be considered further.
    boolean[] tempVisitedArray = new boolean[numNodes];
    Arrays.fill(tempVisitedArray, false);
    tempVisitedArray[tempSource] = true;

    // Step 2. Main loops.
    int tempMinDistance;
    int tempBestNode = -1;
    for (int i = 0; i < numNodes - 1; i++) {
      // Step 2.1. Find out the best next node.
      tempMinDistance = Integer.MAX_VALUE;
      for (int j = 0; j < numNodes; j++) {
        if (tempVisitedArray[j])
          continue;
        if (tempMinDistance > tempDistanceArray[j]) {
          tempMinDistance = tempDistanceArray[j];
          tempBestNode = j;
        }
      }
      // Visit.
      tempVisitedArray[tempBestNode] = true;
      // Step 2.2. Prepare for the next round.
      for (int j = 0; j < numNodes; j++) {
        if (tempVisitedArray[j])
          continue;
        if (weightMatrix.getValue(tempBestNode, j) >= MAX_DISTANCE)
          continue;
        if (tempDistanceArray[j] > weightMatrix.getValue(tempBestNode, j)) {
          tempDistanceArray[j] = weightMatrix.getValue(tempBestNode, j);
          tempParentArray[j] = tempBestNode;
        }
      }

      System.out.println("The selected distance for each node: " + Arrays.toString(tempDistanceArray));
      System.out.println("The parent of each node: " + Arrays.toString(tempParentArray));
    }
    int resultCost = 0;
    for (int i = 0; i < numNodes; i++) {
      resultCost += tempDistanceArray[i];
    }

    // Step 3. Ouput for debug.
    System.out.println("Finally");
    System.out.println("The parent of each node: " + Arrays.toString(tempParentArray));
    System.out.println("The total cost: " + resultCost);

    return resultCost;
  }

  /*
   * Critical path. Net validity checks such as loop check not implemented. The
   * source should be 0 and the destination shoule be n-1.
   * 
   * @return The node sequence of the path.
   */
  public boolean[] criticalPath() {
    // One more value to save simple computation.
    int tempValue;

    // Step 1. The in-degree of each node.
    int[] tempInDegrees = new int[numNodes];
    for (int i = 0; i < numNodes; i++) {
      for (int j = 0; j < numNodes; j++) {
        if (weightMatrix.getValue(i, j) != -1) {
          tempInDegrees[j]++;
        }
      }
    }
    System.out.println("In-degree of nodes: " + Arrays.toString(tempInDegrees));

    // Step 2. Topology sorting.
    int[] tempEarliestTimeArray = new int[numNodes];
    for (int i = 0; i < numNodes; i++) {
      if (tempInDegrees[i] > 0)
        continue;
      System.out.println("Removing " + i);

      for (int j = 0; j < numNodes; j++) {
        if (weightMatrix.getValue(i, j) != -1) {
          // Update neighbors' time.
          tempValue = tempEarliestTimeArray[i] + weightMatrix.getValue(i, j);
          if (tempEarliestTimeArray[j] < tempValue)
            // More necessary time needed.
            tempEarliestTimeArray[j] = tempValue;
          tempInDegrees[j]--;
        }
      }
    }

    System.out.println("Earlist start time: " + Arrays.toString(tempEarliestTimeArray));

    // Step 3. The out-degree of each node.
    int[] tempOutDegrees = new int[numNodes];
    for (int i = 0; i < numNodes; i++) {
      for (int j = 0; j < numNodes; j++) {
        if (weightMatrix.getValue(i, j) != -1)
          tempOutDegrees[i]++;
      }
    }
    System.out.println("Out-degree of nodes: " + Arrays.toString(tempOutDegrees));

    // Step 4. Reverse topology sorting.
    int[] tempLatestTimeArray = new int[numNodes];
    for (int i = 0; i < numNodes; i++) {
      tempLatestTimeArray[i] = tempEarliestTimeArray[numNodes - 1];
    }
    for (int i = numNodes - 1; i >= 0; i--) {
      if (tempOutDegrees[i] > 0)
        continue;
      System.out.println("Removing " + i);

      for (int j = 0; j < numNodes; j++) {
        if (weightMatrix.getValue(j, i) != -1) {
          tempValue = tempLatestTimeArray[i] - weightMatrix.getValue(j, i);
          if (tempLatestTimeArray[j] > tempValue)
            tempLatestTimeArray[j] = tempValue;
          tempOutDegrees[j]--;
          System.out.println("The out-degree of " + j + " decreases by 1");
        }
      }
    }

    System.out.println("Latest start time: " + Arrays.toString(tempLatestTimeArray));

    boolean[] resultCritialArray = new boolean[numNodes];
    for (int i = 0; i < numNodes; i++) {
      if (tempEarliestTimeArray[i] == tempLatestTimeArray[i])
        resultCritialArray[i] = true;
    }

    System.out.println("Critical array: " + Arrays.toString(resultCritialArray));
    System.out.println("Critical nodes: ");
    for (int i = 0; i < numNodes; i++) {
      if (resultCritialArray[i]) {
        System.out.println(" " + i);
      }
    }
    System.out.println();

    return resultCritialArray;
  }

  public static void main(String args[]) {
    Net tempNet0 = new Net(3);
    System.out.println(tempNet0);

    int[][] tempMatrix1 = { { 0, 9, 3, 6 }, { 5, 0, 2, 4 }, { 3, 2, 0, 1 }, { 2, 8, 7, 0 } };
    Net tempNet1 = new Net(tempMatrix1);
    System.out.println(tempNet1);

    // Dijkstra.
    tempNet1.dijkstra(1);
    System.out.println();

    // Unidirected net.
    int[][] tempMatrix2 = { { 0, 7, MAX_DISTANCE, 5, MAX_DISTANCE }, { 7, 0, 8, 9, 7 },
        { MAX_DISTANCE, 8, 0, MAX_DISTANCE, 5 }, { 5, 9, MAX_DISTANCE, 0, 15 }, { MAX_DISTANCE, 7, 5, 15, 0 } };
    Net tempNet2 = new Net(tempMatrix2);
    tempNet2.prim();

    // A directed net without loop is required.
    // Node cannot reach itself. It's indicated by -1.
    int[][] tempMatrix3 = { { -1, 3, 2, -1, -1, -1 }, { -1, -1, -1, 2, 3, -1 }, { -1, -1, -1, 4, -1, 3 },
        { -1, -1, -1, -1, -1, 2 }, { -1, -1, -1, -1, -1, 1 }, { -1, -1, -1, -1, -1, -1 } };
    Net tempNet3 = new Net(tempMatrix3);
    System.out.println("------critical path");
    tempNet3.criticalPath();
  }
}