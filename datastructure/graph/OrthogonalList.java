package datastructure.graph;

public class OrthogonalList {
  // Inner class for adjacent node.
  class OrthogonalNode {
    // The row index.
    int row;
    // The column index.
    int column;
    // The next out node.
    OrthogonalNode nextOut;
    // The next in node.
    OrthogonalNode nextIn;

    /*
     * The first constructor.
     * 
     * @param paraRow The row.
     * 
     * @param paraColumn The column.
     */
    public OrthogonalNode(int paraRow, int paraColumn) {
      row = paraRow;
      column = paraColumn;
      nextOut = null;
      nextIn = null;
    }
  }

  // The number of nodes.
  int numNodes;

  // The headers for each row.
  OrthogonalNode[] headers;

  /*
   * The first constructor.
   * 
   * @param paraMatrix. The matrix indicating the graph.
   */
  public OrthogonalList(int[][] paraMatrix) {
    numNodes = paraMatrix.length;

    // Step 1. Initialize with dummy head.
    OrthogonalNode tempPreviousNode, tempNode;

    headers = new OrthogonalNode[numNodes];

    // Step 2. Link to its out nodes.
    for (int i = 0; i < numNodes; i++) {// Row
      headers[i] = new OrthogonalNode(i, -1);
      tempPreviousNode = headers[i];
      for (int j = 0; j < numNodes; j++) {// Column
        if (paraMatrix[i][j] == 0)
          // disconnected.
          continue;
        // Create a new node.
        tempNode = new OrthogonalNode(i, j);
        // Link
        tempPreviousNode.nextOut = tempNode;
        tempPreviousNode = tempNode;
      }
    }

    // Step 3. Link to its in nodes.
    OrthogonalNode[] tempColumnNodes = new OrthogonalNode[numNodes];
    for (int i = 0; i < numNodes; i++)
      // Store last node of each column.
      tempColumnNodes[i] = headers[i];
    for (int i = 0; i < numNodes; i++) {// Row
      tempNode = headers[i].nextOut;
      while (tempNode != null) {// Column
        // Update nextIn of node at row i.
        tempColumnNodes[tempNode.column].nextIn = tempNode;
        tempColumnNodes[tempNode.column] = tempNode;
        tempNode = tempNode.nextOut;
      }
    }
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";
    OrthogonalNode tempNode;

    for (int i = 0; i < numNodes; i++) {
      tempNode = headers[i].nextOut;
      while (tempNode != null) {
        resultString += " (" + tempNode.row + ", " + tempNode.column + ")";
        tempNode = tempNode.nextOut;
      }
      resultString += "\r\n";
    }
    resultString += "\r\nIn arcs: ";
    for (int i = 0; i < numNodes; i++) {
      tempNode = headers[i].nextIn;

      while (tempNode != null) {
        resultString += " (" + tempNode.row + ", " + tempNode.column + ")";
        tempNode = tempNode.nextIn;
      }
      resultString += "\r\n";
    }
    return resultString;
  }

  public static void main(String args[]) {
    int[][] tempMatrix = { { 0, 1, 0, 0 }, { 0, 0, 0, 1 }, { 1, 0, 0, 0 }, { 0, 1, 1, 0 } };
    OrthogonalList tempList = new OrthogonalList(tempMatrix);
    System.out.println("The data are:\r\n" + tempList);
  }
}
