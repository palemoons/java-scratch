package graph;

import java.util.Arrays;

import datastructure.queue.CircleObjectQueue;
import datastructure.stack.ObjectStack;
import matrix.IntMatrix;

public class Graph {
  // The connectivity matrix.
  IntMatrix connectivityMatrix;

  /*
   * The first constructor.
   * 
   * @param paraNumNodes. The number of nodes in the graph.
   */
  public Graph(int paraNumNodes) {
    connectivityMatrix = new IntMatrix(paraNumNodes, paraNumNodes);
  }

  /*
   * The second constructor.
   * 
   * @param paraMatrix. The data matrix.
   */
  public Graph(int[][] paraMatrix) {
    connectivityMatrix = new IntMatrix(paraMatrix);
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "This is the connectivity matrix of the graph." + connectivityMatrix;
    return resultString;
  }

  /*
   * Get the connectivity of the graph.
   * 
   * @throws Exception for internal error.
   */
  public boolean getConnectivity() throws Exception {
    // Step 1. Initialize accumlated matrix: M_a = I (unit matrix).
    IntMatrix tempConnectivityMatrix = IntMatrix.getIdentityMatrix(connectivityMatrix.getData().length);

    // Step 2. Initialize M^1.
    IntMatrix tempMultipliedMatrix = new IntMatrix(tempConnectivityMatrix);

    // Step 3. Determine the actual connectivity.
    // 循环过程中，累积矩阵不断与临时乘积矩阵相加，使得累积矩阵中的元素表示了当前节点到其他节点的路径数量。
    for (int i = 0; i < connectivityMatrix.getData().length - 1; i++) {
      // M_a = M_a + M^k
      tempConnectivityMatrix.add(tempMultipliedMatrix);

      // M^k
      tempMultipliedMatrix = IntMatrix.multiply(tempConnectivityMatrix, connectivityMatrix);
    }

    // Step 4. Check the connectivity.
    System.out.println("The connectivity matrix is: " + tempConnectivityMatrix);
    int[][] tempData = tempConnectivityMatrix.getData();
    for (int i = 0; i < tempData.length; i++) {
      for (int j = 0; j < tempData.length; j++) {
        if (tempData[i][j] == 0) {
          System.out.println("Node " + i + " cannot reach " + j);
          return false;
        }
      }
    }
    return true;
  }

  /*
   * Breadth first traversal.
   * 
   * @param paraStartIndex. The start index.
   * 
   * @return The sequence of the visit.
   */
  public String breadthFirstTraversal(int paraStartIndex) {
    CircleObjectQueue tempQueue = new CircleObjectQueue();
    String resultString = "";

    int tempNumNodes = connectivityMatrix.getRows();
    boolean[] tempVisitiedArray = new boolean[tempNumNodes];

    // Initialize the queue.
    // Visit before enqueue.
    tempVisitiedArray[paraStartIndex] = true;
    resultString += paraStartIndex;
    tempQueue.enqueue(Integer.valueOf(paraStartIndex));

    // Now visit the rest of the graph.
    int tempIndex;
    Integer tempInteger = (Integer) tempQueue.dequeue();
    while (tempInteger != null) {
      tempIndex = tempInteger.intValue();

      // Enqueue its unvisited neighbors.
      for (int i = 0; i < tempNumNodes; i++) {
        if (tempVisitiedArray[i])
          continue;// Already visited.
        if (connectivityMatrix.getData()[tempIndex][i] == 0)
          continue;// Not directed connected.
        // Visit before enqueue.
        tempVisitiedArray[i] = true;
        resultString += i;
        tempQueue.enqueue(Integer.valueOf(i));
      }

      // Take out one from head.
      tempInteger = (Integer) tempQueue.dequeue();
    }
    return resultString;
  }

  /*
   * Depth first traversal.
   * 
   * @param paraStartIndex. The start index.
   * 
   * @return The sequence of the visit.
   */
  public String depthFirstTraversal(int paraStartIndex) {
    ObjectStack tempStack = new ObjectStack();
    String resultString = "";

    int tempNumNodes = connectivityMatrix.getRows();
    boolean[] tempVisitedArray = new boolean[tempNumNodes];

    // Initialize the stack.
    // Visit before push.
    tempVisitedArray[paraStartIndex] = true;
    resultString += paraStartIndex;
    tempStack.push(Integer.valueOf(paraStartIndex));
    System.out.println("Push " + paraStartIndex);
    System.out.println("Visited " + resultString);

    // Visit the rest of the graph.
    int tempIndex = paraStartIndex;
    int tempNext;
    Integer tempInteger;
    while (true) {
      // Find an unvisited neighbor.
      tempNext = -1;
      for (int i = 0; i < tempNumNodes; i++) {
        if (tempVisitedArray[i])
          continue;// Already visited.
        if (connectivityMatrix.getData()[tempIndex][i] == 0)
          continue;// Disconnected.

        // Visit this one.
        tempVisitedArray[i] = true;
        resultString += i;
        tempStack.push(Integer.valueOf(i));
        System.out.println("Push " + i);
        tempNext = i;

        // One is enough.
        break;
      }

      if (tempNext == -1) {
        tempInteger = (Integer) tempStack.pop();
        System.out.println("Pop " + tempInteger);
        if (tempStack.isEmpty())
          // No unvisited neighbor. Backtracking to the last one stored in the stack.
          break;
        else {
          // Back to the previous node, however do not remove it.
          tempInteger = (Integer) tempStack.pop();
          tempIndex = tempInteger.intValue();
          tempStack.push(tempInteger);
        }
      } else {
        tempIndex = tempNext;
      }
    }
    return resultString;
  }

  // Unit test for getConnectivity.
  public static void getConnectivityTest() {
    // Test an undirected graph.
    int[][] tempMatrix = { { 0, 1, 1 }, { 1, 0, 1 }, { 1, 1, 0 } };
    Graph tempGraph2 = new Graph(tempMatrix);
    System.out.println(tempGraph2);

    boolean tempConnected = false;
    try {
      tempConnected = tempGraph2.getConnectivity();
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("Is the graph connected? " + tempConnected);

    // Test a directed graph.
    // Remove one arc to form a directed graph.
    tempGraph2.connectivityMatrix.setValue(1, 0, 0);

    tempConnected = false;
    try {
      tempConnected = tempGraph2.getConnectivity();
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("Is the graph connected? " + tempConnected);
  }

  // Unit test for breadthFirstTraversal.
  public static void breadthFirstTraversalTest() {
    // Test an undirected graph.
    int[][] tempMatrix = { { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 0, 1, 1, 0 } };
    Graph tempGraph = new Graph(tempMatrix);
    System.out.println(tempGraph);

    String tempSequence = "";
    try {
      tempSequence = tempGraph.breadthFirstTraversal(2);
    } catch (Exception ee) {
      System.out.println(ee);
    }

    System.out.println("The breadth first order of visit: " + tempSequence);
  }

  // Unit test for depthFirstTraversal.
  public static void depthFirstTraversalTest() {
    // Test an undirected graph.
    int[][] tempMatrix = { { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 0 }, { 0, 1, 0, 0 } };
    Graph tempGraph = new Graph(tempMatrix);
    System.out.println(tempGraph);

    String tempSequence = "";
    try {
      tempSequence = tempGraph.depthFirstTraversal(0);
    } catch (Exception ee) {
      System.out.println(ee);
    }

    System.out.println("The depth first order of visit: " + tempSequence);
  }

  /*
   * Coloring. Ouput all possible schemes.
   * 
   * @param paraNumColors. The number of colors.
   */
  public void coloring(int paraNumColors) {
    // Step 1. Initialize.
    int tempNumNodes = connectivityMatrix.getRows();
    int[] tempColorScheme = new int[tempNumNodes];
    Arrays.fill(tempColorScheme, -1);

    coloring(paraNumColors, 0, tempColorScheme);
  }

  /*
   * Coloring. Output all possible schemes.
   * 
   * @param paraNumColors. The number of colors.
   * 
   * @param paraCurrentNumNodes. The number of nodes that have been colored.
   * 
   * @param paraCurrentColoring. The array recording the coloring scheme.
   */
  public void coloring(int paraNumColors, int paraCurrentNumNodes, int[] paraCurrentColoring) {
    // Step 1. Initialize.
    int tempNumNodes = connectivityMatrix.getRows();
    System.out.println("coloring: paraNumColors = " + paraNumColors + ", paraCurrentNumNodes = " + paraCurrentNumNodes
        + ", paraCurrentColoring" + Arrays.toString(paraCurrentColoring));
    // A complete scheme.
    if (paraCurrentNumNodes >= tempNumNodes) {
      System.out.println("Find one:" + Arrays.toString(paraCurrentColoring));
      return;
    }

    // Try all possible colors.
    for (int i = 0; i < paraNumColors; i++) {
      paraCurrentColoring[paraCurrentNumNodes] = i;
      if (!colorConflict(paraCurrentNumNodes + 1, paraCurrentColoring))
        coloring(paraNumColors, paraCurrentNumNodes + 1, paraCurrentColoring);
    }
  }

  /*
   * Coloring conflict or not. Only compare the current last node with previous
   * ones.
   * 
   * @param paraCurrentNumNodes. The current number of nodes.
   * 
   * @param paraColoring. The current coloring scheme.
   * 
   * @return Conflict or not.
   */
  public boolean colorConflict(int paraCurrentNumNodes, int[] paraColoring) {
    for (int i = 0; i < paraCurrentNumNodes - 1; i++) {
      // No direct connection.
      if (connectivityMatrix.getValue(paraCurrentNumNodes - 1, i) == 0)
        continue;
      if (paraColoring[paraCurrentNumNodes - 1] == paraColoring[i])
        return true;
    }
    return false;
  }

  // Coloring test.
  public static void coloringTest() {
    // Temp adjacent matrix.
    int[][] tempMatrix = { { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 0 }, { 0, 1, 0, 0 } };
    Graph tempGraph = new Graph(tempMatrix);
    tempGraph.coloring(3);
  }

  public static void main(String args[]) {
    System.out.println("Hello!");
    // Graph tempGraph = new Graph(3);
    // System.out.println(tempGraph);

    // Unit test
    // getConnectivityTest();
    // breadthFirstTraversalTest();
    // depthFirstTraversalTest();
    coloringTest();
  }
}
