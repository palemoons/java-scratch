package graph;

import datastructure.queue.CircleObjectQueue;
import datastructure.stack.ObjectStack;

public class AdjacencyList {
  // Inner class for adjacent node.
  class AdjacencyNode {
    // The column index.
    int column;
    // The next adjacent node.
    AdjacencyNode next;

    /*
     * The first constructor.
     * 
     * @param paraColumn. The column
     */
    public AdjacencyNode(int paraColumn) {
      column = paraColumn;
      next = null;
    }
  }

  // The number of nodes, equaling to headers.length.
  int numNodes;
  // The headers for each row.
  AdjacencyNode[] headers;

  /*
   * The first constructor.
   * 
   * @param paraMatrix. The matrix indicating the graph.
   */
  public AdjacencyList(int[][] paraMatrix) {
    numNodes = paraMatrix.length;
    // Step 1. Initialize with dummy head.
    AdjacencyNode tempPreviousNode, tempNode;

    headers = new AdjacencyNode[numNodes];
    for (int i = 0; i < numNodes; i++) {
      headers[i] = new AdjacencyNode(-1);
      tempPreviousNode = headers[i];
      for (int j = 0; j < numNodes; j++) {
        if (paraMatrix[i][j] == 0)
          continue;
        // Create a new node.
        tempNode = new AdjacencyNode(j);
        // Link.
        tempPreviousNode.next = tempNode;
        tempPreviousNode = tempNode;
      }
    }
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";
    AdjacencyNode tempNode;
    for (int i = 0; i < numNodes; i++) {
      tempNode = headers[i].next;
      while (tempNode != null) {
        resultString += " (" + i + ", " + tempNode.column + ")";
        tempNode = tempNode.next;
      }
      resultString += "\r\n";
    }
    return resultString;
  }

  /*
   * Breadth first traveral.
   * 
   * @param paraStartIndex. The start index.
   * 
   * @return The sequence of the visit.
   */
  public String breadthFirstTraversal(int paraStartIndex) {
    CircleObjectQueue tempQueue = new CircleObjectQueue();
    String resultString = "";
    boolean[] tempVisitedArray = new boolean[numNodes];
    tempVisitedArray[paraStartIndex] = true;

    // Initialize the queue.
    // Visit before enqueue.
    tempVisitedArray[paraStartIndex] = true;
    resultString += paraStartIndex;
    tempQueue.enqueue(Integer.valueOf(paraStartIndex));

    // Now visit the rest of the graph.
    int tempIndex;
    Integer tempInteger = (Integer) tempQueue.dequeue();
    AdjacencyNode tempNode;
    while (tempInteger != null) {
      tempIndex = tempInteger.intValue();

      // Enqueue all its unvisited neighbors. The neighbors are linked already.
      tempNode = headers[tempIndex].next;
      while (tempNode != null) {
        if (!tempVisitedArray[tempNode.column]) {
          // Visit before enqueue.
          tempVisitedArray[tempNode.column] = true;
          resultString += tempNode.column;
          tempQueue.enqueue(Integer.valueOf(tempNode.column));
        }
        tempNode = tempNode.next;
      }
      // Take out one from the head.
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
    boolean[] tempVisitedArray = new boolean[numNodes];

    // Push first node.
    int tempNode = paraStartIndex;
    tempStack.push(Integer.valueOf(paraStartIndex));
    System.out.println("Push " + paraStartIndex);
    resultString += paraStartIndex;
    tempVisitedArray[paraStartIndex] = true;

    // Depth first traverse.
    while (true) {
      // Find an unvisited node.
      int tempNext = -1;
      for (AdjacencyNode tempAdjNode = headers[tempNode].next; tempAdjNode != null; tempAdjNode = tempAdjNode.next) {
        if (tempVisitedArray[tempAdjNode.column] == false) {// unvisited
          tempNext = tempAdjNode.column;
          break;
        }
      }
      if (tempNext != -1) {
        // Visit node.
        tempVisitedArray[tempNext] = true;
        resultString += tempNext;
        // Push node.
        tempStack.push(Integer.valueOf(tempNext));
        System.out.println("Push " + tempNext);
        tempNode = tempNext;
      } else {
        // Reach leaf, pop element.
        Integer tempInteger = (Integer) tempStack.pop();
        System.out.println("Pop " + tempInteger);
        // Break if stack is empty.
        if (tempStack.isEmpty())
          break;
        else {
          // Backtrack.
          tempInteger = (Integer) tempStack.pop();
          tempNode = tempInteger.intValue();
          tempStack.push(Integer.valueOf(tempNode));
        }
      }
    }
    return resultString;
  }

  // Unit test for breadthFirstTraversal. The same as the one in class Graph.
  public static void breadthFirstTraversalTest() {
    // Test an undirected graph.
    int[][] tempMatrix = { { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 0, 1, 1, 0 } };
    Graph tempGraph = new Graph(tempMatrix);
    System.out.println(tempGraph);
    AdjacencyList tempAdjList = new AdjacencyList(tempMatrix);
    String tempSequence = "";
    try {
      tempSequence = tempAdjList.breadthFirstTraversal(2);
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("The breadth first order of visit: " + tempSequence);
  }

  public static void depthFirstTraversalTest() {
    // Test an undirected graph.
    int[][] tempMatrix = { { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 0, 1, 1, 0 } };
    Graph tempGraph = new Graph(tempMatrix);
    System.out.println(tempGraph);
    AdjacencyList tempAdjList = new AdjacencyList(tempMatrix);
    String tempSequence = "";
    try {
      tempSequence = tempAdjList.depthFirstTraversal(2);
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("The depth first order of visit: " + tempSequence);
  }

  public static void main(String args[]) {
    int[][] tempMatrix = { { 0, 1, 0 }, { 1, 0, 1 }, { 0, 1, 0 } };
    AdjacencyList tempTable = new AdjacencyList(tempMatrix);
    System.out.println("The data are:\r\n" + tempTable);
    breadthFirstTraversalTest();
    depthFirstTraversalTest();
  }
}
