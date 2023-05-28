package matrix;

import java.util.Arrays;

public class IntMatrix {
  // The data.
  int[][] data;

  /*
   * First constructor.
   * 
   * @param paraRows The number of rows.
   * 
   * @param paraColumns The number of columns.
   */
  public IntMatrix(int paraRows, int paraColumns) {
    data = new int[paraRows][paraColumns];
  }

  /*
   * Second constructor.
   * 
   * @param paraMatrix The given matrix.
   */
  public IntMatrix(int[][] paraMatrix) {
    data = new int[paraMatrix.length][paraMatrix[0].length];
    // Copy elements.
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        data[i][j] = paraMatrix[i][j];
      }
    }
  }

  /*
   * Third constructor.
   * 
   * @param paraMatrix The given matrix.
   */
  public IntMatrix(IntMatrix paraMatrix) {
    this(paraMatrix.getData());
  }

  /*
   * Get identity matrix. The values at the diagonal are all 1.
   * 
   * @param paraRows The given rows.
   */
  public static IntMatrix getIdentityMatrix(int paraRows) {
    IntMatrix resultMatrix = new IntMatrix(paraRows, paraRows);
    for (int i = 0; i < paraRows; i++)
      resultMatrix.data[i][i] = 1;
    return resultMatrix;
  }

  // Overrides toString method.
  public String toString() {
    return Arrays.deepToString(data);
  }

  /*
   * Get data. The reference to the data instead of a copy of data is returned.
   * 
   * @return The data matrix.
   */
  public int[][] getData() {
    return data;
  }

  /*
   * Getter.
   * 
   * @return The number of rows.
   */
  public int getRows() {
    return data.length;
  }

  /*
   * Getter.
   * 
   * @return The number of columns.
   */
  public int getColumns() {
    return data[0].length;
  }

  /*
   * Set one the value of one element.
   * 
   * @param paraRow The row of the element.
   * 
   * @param paraColumn The column of the element.
   * 
   * @param paraValue The new value.
   */
  public void setValue(int paraRow, int paraColumn, int paraValue) {
    data[paraRow][paraColumn] = paraValue;
  }

  /*
   * Get the value of one element.
   * 
   * @param paraRow The row of the element.
   * 
   * @param paraColumn The column of the element.
   */
  public int getValue(int paraRow, int paraColumn) {
    return data[paraRow][paraColumn];
  }

  /*
   * Add another matrix to me.
   * 
   * @param paraMatrix The other matrix.
   */
  public void add(IntMatrix paraMatrix) throws Exception {
    // Step 1. Get the data of the given matrix.
    int[][] tempData = paraMatrix.getData();

    // Step 2. Size check.
    if (data.length != tempData.length) {
      throw new Exception("Cannot add matrices. Rows not match: " + data.length + " vs. " + tempData.length + ".");
    }
    if (data[0].length != tempData[0].length) {
      throw new Exception(
          "Cannot add matrices. Columns not match: " + data[0].length + " vs. " + tempData[0].length + ".");
    }

    // Step 3. Add to me.
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        data[i][j] += tempData[i][j];
      }
    }
  }

  /*
   * Add two existing matrices.
   * 
   * @param paraMatrix1 The first matrix.
   * 
   * @param paraMatrix2 The second matrix.
   * 
   * @return A new matrix.
   */
  public static IntMatrix add(IntMatrix paraMatrix1, IntMatrix paraMatrix2) throws Exception {
    // Step 1. Clone the first matrix.
    IntMatrix resultMatrix = new IntMatrix(paraMatrix1);

    // Step 2. Add the second one.
    resultMatrix.add(paraMatrix2);

    return resultMatrix;
  }

  /*
   * Multiply two existing matrices.
   * 
   * @param paraMatrix1 The first matrix.
   * 
   * @param paraMatrix2 The second matrix.
   * 
   * @return A new matrix.
   */
  public static IntMatrix multiply(IntMatrix paraMatrix1, IntMatrix paraMatrix2) throws Exception {
    // Step 1. Check size.
    int[][] tempData1 = paraMatrix1.getData();
    int[][] tempData2 = paraMatrix2.getData();
    if (tempData1[0].length != tempData2[0].length) {
      throw new Exception("Cannot multiply matrices: " + tempData1[0].length + " vs. " + tempData2[0].length + ".");
    }

    // Step 2. Allocate space.
    int[][] resultData = new int[tempData1.length][tempData2[0].length];

    // Step 3. Multiply.
    for (int i = 0; i < tempData1.length; i++) {
      for (int j = 0; j < tempData2[0].length; j++) {
        for (int k = 0; k < tempData1[0].length; k++) {
          resultData[i][j] += tempData1[i][k] * tempData2[k][j];
        }
      }
    }

    // Step 4. Construct the matrix object.
    IntMatrix resultMatrix = new IntMatrix(resultData);

    return resultMatrix;
  }

  public static void main(String args[]) {
    IntMatrix tempMatrix1 = new IntMatrix(3, 3);
    tempMatrix1.setValue(0, 1, 1);
    tempMatrix1.setValue(1, 0, 1);
    tempMatrix1.setValue(1, 2, 1);
    tempMatrix1.setValue(2, 1, 1);
    System.out.println("The original matrix is: " + tempMatrix1);

    IntMatrix tempMatrix2 = null;
    try {
      tempMatrix2 = IntMatrix.multiply(tempMatrix1, tempMatrix1);
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("The square matrix is: " + tempMatrix2);

    IntMatrix tempMatrix3 = new IntMatrix(tempMatrix2);
    try {
      tempMatrix3.add(tempMatrix1);
    } catch (Exception ee) {
      System.out.println(ee);
    }
    System.out.println("The conncetivity matrix is: " + tempMatrix3);
  }
}