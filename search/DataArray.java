package search;

public class DataArray {
  // Inner class for data nodes.
  class DataNode {
    // The key.
    int key;
    // The data content.
    String content;

    // The first constructor.
    DataNode(int paraKey, String paraContent) {
      key = paraKey;
      content = paraContent;
    }

    // Overrides toString method.
    public String toString() {
      return "(" + key + ", " + content + ")";
    }
  }

  // The data array.
  DataNode[] data;

  // The length of the data array.
  int length;

  /*
   * The first constructor.
   * 
   * @param paraKeyArray. The array of the keys.
   * 
   * @param paraContentArray. The array of contents.
   */
  public DataArray(int[] paraKeyArray, String[] paraContentArrary) {
    length = paraKeyArray.length;
    data = new DataNode[length];
    for (int i = 0; i < length; i++)
      data[i] = new DataNode(paraKeyArray[i], paraContentArrary[i]);
  }

  /*
   * The second constructor. For hash code only.
   * 
   * @param paraKeyArray. The array of the keys.
   * 
   * @param paraContentArray. The array of contents.
   * 
   * @param paraLength. The space of the Hash table.
   */
  public DataArray(int[] paraKeyArray, String[] paraContentArray, int paraLength) {
    // Step 1. Initialize.
    length = paraLength;
    data = new DataNode[length];

    for (int i = 0; i < length; i++)
      data[i] = null;

    // Step 2. Fill the data.
    int tempPosition;
    for (int i = 0; i < paraKeyArray.length; i++) {
      // Hash.
      tempPosition = paraKeyArray[i] % paraLength;

      // Find an empty position.
      while (data[tempPosition] != null) {
        tempPosition = (tempPosition + 1) % paraLength;
        System.out.println("Collision, move forward for key " + paraKeyArray[i]);
      }

      data[tempPosition] = new DataNode(paraKeyArray[i], paraContentArray[i]);
    }
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "I am a data array with " + length + " items.\r\n";
    for (int i = 0; i < length; i++)
      resultString += data[i] + " ";
    return resultString;
  }

  /*
   * Sequential search.
   * 
   * @param paraKey. The given key.
   * 
   * @return The content of the key.
   */
  public String sequentialSearch(int paraKey) {
    data[0].key = paraKey;
    int i;
    for (i = length - 1; data[i].key != paraKey; i--)
      ;
    return data[i].content;
  }

  // Sequential search test.
  public static void sequentialSearchTest() {
    int[] tempUnsortedKeys = { -1, 5, 3, 6, 10, 7, 1, 9 };
    String[] tempContents = { "null", "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    System.out.println("Search result of 10 is: " + tempDataArray.sequentialSearch(10));
    System.out.println("Search result of 5 is: " + tempDataArray.sequentialSearch(5));
    System.out.println("Search result of 4 is: " + tempDataArray.sequentialSearch(4));
  }

  /*
   * Binary search.
   * 
   * @param paraKey. The given key.
   * 
   * @return The content of the key.
   */
  public String binarySearch(int paraKey) {
    int tempLeft = 0, tempRight = length - 1, tempMiddle = (tempLeft + tempRight) / 2;

    while (tempLeft <= tempRight) {
      tempMiddle = (tempLeft + tempRight) / 2;
      if (data[tempMiddle].key == paraKey) {
        return data[tempMiddle].content;
      }
      if (data[tempMiddle].key <= paraKey) {
        tempLeft = tempMiddle + 1;
      } else {
        tempRight = tempMiddle - 1;
      }
    }
    return "null";
  }

  // Binary search test.
  public static void binarySearchTest() {
    int[] tempUnsortedKeys = { 1, 3, 5, 6, 7, 9, 10 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    System.out.println("Search result of 10 is: " + tempDataArray.binarySearch(10));
    System.out.println("Search result of 5 is: " + tempDataArray.binarySearch(5));
    System.out.println("Search result of 4 is: " + tempDataArray.binarySearch(4));
  }

  /*
   * Hash search.
   * 
   * @param paraKey. The given key.
   * 
   * @return The content of the key.
   */
  public String hashSearch(int paraKey) {
    int tempPosition = paraKey % length;
    while (data[tempPosition] != null) {
      if (data[tempPosition].key == paraKey)
        return data[tempPosition].content;
      System.out.println("Not this one for: " + paraKey);
      tempPosition = (tempPosition + 1) % length;
    }
    // Not found.
    return "null";
  }

  // Hash search test.
  public static void hashSearchTest() {
    int[] tempUnsortedKeys = { 16, 33, 38, 69, 57, 95, 86 };
    String[] tempContents = { "if", "then", "else", "swtich", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents, 19);

    System.out.println(tempDataArray);

    System.out.println("Search result of 95 is: " + tempDataArray.hashSearch(95));
    System.out.println("Search result of 38 is: " + tempDataArray.hashSearch(38));
    System.out.println("Search result of 57 is: " + tempDataArray.hashSearch(57));
    System.out.println("Search result of 4 is: " + tempDataArray.hashSearch(4));
  }

  // Insertion sort. data[0] does not store a valid data.
  public void insertionSort() {
    DataNode tempNode;
    int j;
    for (int i = 2; i < length; i++) {
      tempNode = data[i];
      // Find the position to insert.
      // At the same time, move other nodes.
      for (j = i - 1; data[j].key > tempNode.key; j--)
        data[j + 1] = data[j];
      // Insert.
      data[j + 1] = tempNode;

      System.out.println("Round " + (i - 1));
      System.out.println(this);
    }
  }

  // Insertion sort test.
  public static void insertionSortTest() {
    int[] tempUnsortedKeys = { -100, 5, 3, 6, 10, 7, 1, 9 };
    String[] tempContents = { "null", "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.insertionSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  public void shellSort() {
    DataNode tempNode;
    int[] tempJumpArray = { 5, 3, 1 };
    int tempJump;
    int p;
    for (int i = 0; i < tempJumpArray.length; i++) {
      tempJump = tempJumpArray[i];
      for (int j = 0; j < tempJump; j++) {
        for (int k = j + tempJump; k < length; k += tempJump) {
          tempNode = data[k];
          // Find the position to insert.
          // At the same time, move other nodes.
          for (p = k - tempJump; p >= 0; p -= tempJump) {
            if (data[p].key > tempNode.key)
              data[p + tempJump] = data[p];
            else
              break;
          }

          // Insert
          data[p + tempJump] = tempNode;
        }
      }
      System.out.println("Round " + i);
      System.out.println(this);
    }
  }

  // Test the method.
  public static void shellSortTest() {
    int[] tempUnsortedKeys = { 5, 3, 6, 10, 7, 1, 9, 12, 8, 4 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while", "throw", "until", "do" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.shellSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  // Bubble sort.
  public void bubbleSort() {
    boolean tempSwapped;
    DataNode tempNode;
    for (int i = length - 1; i > 0; i--) {
      tempSwapped = false;
      for (int j = 0; j < i; j++) {
        if (data[j].key > data[j + 1].key) {
          // Swap.
          tempNode = data[j + 1];
          data[j + 1] = data[j];
          data[j] = tempNode;

          tempSwapped = true;
        }
      }

      if (!tempSwapped) {
        System.out.println("Premature.");
        break;
      }

      System.out.println("Round " + (length - i));
      System.out.println(this);
    }
  }

  // Bubble sort test.
  public static void bubbleSortTest() {
    int[] tempUnsortedKeys = { 1, 3, 6, 10, 7, 5, 9 };
    String[] tempStrings = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempStrings);

    System.out.println(tempDataArray);

    tempDataArray.bubbleSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  /*
   * Quick sort recursively.
   * 
   * @param paraStart. The start index.
   * 
   * @param paraEnd. The end index.
   */
  public void quickSortRecursive(int paraStart, int paraEnd) {
    // Nothing to sort.
    if (paraStart >= paraEnd)
      return;

    int tempPivot = data[paraEnd].key;
    DataNode tempNodeForSwap;
    int tempLeft = paraStart;
    int tempRight = paraEnd - 1;

    // Find the position of the pivot.
    while (true) {
      while ((data[tempLeft].key < tempPivot) && (tempLeft < tempRight))
        // Left side is ordered.
        tempLeft++;
      while ((data[tempRight].key > tempPivot) && (tempLeft < tempRight))
        // Right side is ordered.
        tempRight--;

      if (tempLeft < tempRight) {
        // Swap.
        System.out.println("Swapping " + tempLeft + " and " + tempRight);
        tempNodeForSwap = data[tempLeft];
        data[tempLeft] = data[tempRight];
        data[tempRight] = tempNodeForSwap;
      } else {
        break;
      }
    }

    // Swap.
    if (data[tempLeft].key > tempPivot) {
      tempNodeForSwap = data[paraEnd];
      data[paraEnd] = data[tempLeft];
      data[tempLeft] = tempNodeForSwap;
    } else {
      tempLeft++;
    }

    System.out.println("From " + paraStart + " to " + paraEnd + ": ");
    System.out.println(this);

    quickSortRecursive(paraStart, tempLeft - 1);
    quickSortRecursive(tempLeft + 1, paraEnd);
  }

  // Quick sort test.
  public static void quickSortTest() {
    int[] tempUnsortedKeys = { 1, 3, 12, 10, 5, 7, 9 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.quickSortRecursive(1, tempDataArray.length - 1);
    System.out.println("Result\r\n" + tempDataArray);
  }

  // Selection sort.
  public void selectionSort() {
    DataNode tempNode;
    int tempIndexForSmallest;

    for (int i = 0; i < length - 1; i++) {
      // Initialize.
      tempNode = data[i];
      tempIndexForSmallest = i;
      for (int j = i + 1; j < length; j++) {
        if (data[j].key < tempNode.key) {
          tempNode = data[j];
          tempIndexForSmallest = j;
        }
      }
      // Swap.
      data[tempIndexForSmallest] = data[i];
      data[i] = tempNode;
    }
  }

  // Selection sort test.
  public static void selectionSortTest() {
    int[] tempUnsortedKeys = { 5, 3, 6, 10, 7, 1, 9 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.selectionSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  // Heap sort.
  public void heapSort() {
    DataNode tempNode;
    // Step 1. Construct the initial heap.
    for (int i = length / 2 - 1; i >= 0; i--)
      adjustHeap(i, length);
    System.out.println("The initial heap: " + this + "\r\n");

    // Step 2. Swap and reconstruct.
    for (int i = length - 1; i > 0; i--) {
      tempNode = data[0];
      data[0] = data[i];
      data[i] = tempNode;

      adjustHeap(0, i);
      System.out.println("Round " + (length - i) + ": " + this);
    }
  }

  /*
   * Adjust the heap.
   * 
   * @param paraStart. The start of the index.
   * 
   * @param paraLength. The length of the adjust sequence.
   */
  public void adjustHeap(int paraStart, int paraLength) {
    DataNode tempNode = data[paraStart];
    int tempParent = paraStart;
    int tempKey = data[paraStart].key;

    for (int tempChild = paraStart * 2 + 1; tempChild < paraLength; tempChild = tempChild * 2 + 1) {
      // The right child is bigger.
      if (tempChild + 1 < paraLength && data[tempChild].key < data[tempChild + 1].key)
        tempChild++;

      System.out.println("The parent position is " + tempParent + " and the child is " + tempChild);
      if (tempKey < data[tempChild].key) {
        // The child is bigger.
        data[tempParent] = data[tempChild];
        System.out.println("Move " + data[tempChild].key + " to position " + tempParent);
        tempParent = tempChild;
      } else {
        break;
      }
    }
    data[tempParent] = tempNode;
    System.out.println("Adjust " + paraStart + " to " + paraLength + ": " + this);
  }

  // Heap sort test.
  public static void heapSortTest() {
    int[] tempUnsortedKeys = { 5, 3, 6, 10, 7, 1, 9 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.heapSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  // Merge sort. Results are stored in the member variable data.
  public void mergeSort() {
    // Step 1. Allocate space.
    int tempRow;// The current row
    int tempGroups;// Number of groups
    int tempActualRow; // Only 0 or 1.
    int tempNextRow = 0;
    int tempGroupNumber;
    int tempFirstStart, tempSecondStart, tempSecondEnd;
    int tempFirstIndex, tempSecondIndex;
    int tempNumCopied;
    for (int i = 0; i < length; i++)
      System.out.print(data[i]);
    System.out.println();

    DataNode[][] tempMatrix = new DataNode[2][length];

    // Step 2. Copy data.
    for (int i = 0; i < length; i++)
      tempMatrix[0][i] = data[i];

    // Step 3. Merge. log n rounds.
    tempRow = -1;
    for (int tempSize = 1; tempSize <= length; tempSize *= 2) {
      // Reuse the space of the two rows.
      tempRow++;
      System.out.println("Current row = " + tempRow);
      tempActualRow = tempRow % 2;
      tempNextRow = (tempRow + 1) % 2;

      tempGroups = length / (tempSize * 2);
      if (length % (tempSize * 2) != 0)
        tempGroups++;
      System.out.println("tempSize = " + tempSize + ", numGroups = " + tempGroups);

      for (tempGroupNumber = 0; tempGroupNumber < tempGroups; tempGroupNumber++) {
        tempFirstStart = tempGroupNumber * tempSize * 2;
        tempSecondStart = tempGroupNumber * tempSize * 2 + tempSize;
        if (tempSecondStart > length - 1) {
          // Copy the first part.
          for (int i = tempFirstStart; i < length; i++)
            tempMatrix[tempNextRow][i] = tempMatrix[tempActualRow][i];
          continue;
        }
        tempSecondEnd = tempGroupNumber * tempSize * 2 + tempSize * 2 - 1;
        if (tempSecondEnd > length - 1)
          tempSecondEnd = length - 1;

        System.out.println("Trying to merge [" + tempFirstStart + ", " + (tempSecondStart - 1) + "] with ["
            + tempSecondStart + ", " + tempSecondEnd + "]");

        tempFirstIndex = tempFirstStart;
        tempSecondIndex = tempSecondStart;
        tempNumCopied = 0;
        while ((tempFirstIndex <= tempSecondStart - 1) && (tempSecondIndex <= tempSecondEnd)) {
          if (tempMatrix[tempActualRow][tempFirstIndex].key <= tempMatrix[tempActualRow][tempSecondIndex].key) {
            tempMatrix[tempNextRow][tempFirstStart + tempNumCopied] = tempMatrix[tempActualRow][tempFirstIndex];
            tempFirstIndex++;
            System.out.println("Copying " + tempMatrix[tempActualRow][tempFirstIndex]);
          } else {
            tempMatrix[tempNextRow][tempFirstStart + tempNumCopied] = tempMatrix[tempActualRow][tempSecondIndex];
            System.out.println("Copying " + tempMatrix[tempActualRow][tempSecondIndex]);
            tempSecondIndex++;
          }
          tempNumCopied++;
        }

        while (tempFirstIndex <= tempSecondStart - 1) {
          tempMatrix[tempNextRow][tempFirstStart + tempNumCopied] = tempMatrix[tempActualRow][tempFirstIndex];
          tempFirstIndex++;
          tempNumCopied++;
        }

        while (tempSecondIndex < tempSecondEnd) {
          tempMatrix[tempNextRow][tempFirstStart + tempNumCopied] = tempMatrix[tempActualRow][tempSecondIndex];
          tempSecondIndex++;
          tempNumCopied++;
        }
      }
      System.out.println("Round " + tempRow);
      for (int i = 0; i < length; i++)
        System.out.println(tempMatrix[tempNextRow][i] + " ");
      System.out.println();
    }
    data = tempMatrix[tempNextRow];
  }

  // Merge sort test.
  public static void mergeSortTest(){
    int[] tempUnsortedKeys = { 5, 3, 6, 10, 7, 1, 9 };
    String[] tempContents = { "if", "then", "else", "switch", "case", "for", "while" };
    DataArray tempDataArray = new DataArray(tempUnsortedKeys, tempContents);

    System.out.println(tempDataArray);

    tempDataArray.mergeSort();
    System.out.println("Result\r\n" + tempDataArray);
  }

  public static void main(String args[]) {
    System.out.println("\r\n-------sequentialSearchTest-------");
    sequentialSearchTest();

    System.out.println("\r\n-------binarySearchTest-------");
    binarySearchTest();

    System.out.println("\r\n-------hashSearchTest-------");
    hashSearchTest();

    System.out.println("\r\n-------insertionSortTest--------");
    insertionSortTest();

    System.out.println("\r\n-------shellSort-------");
    shellSortTest();

    System.out.println("\r\n-------bubbleSort-------");
    bubbleSortTest();

    System.out.println("\r\n-------quickSort-------");
    quickSortTest();

    System.out.println("\r\n-------selectionSort-------");
    selectionSortTest();

    System.out.println("\r\n-------heapSort-------");
    heapSortTest();

    System.out.println("\r\n-------mergeSort-------");
    mergeSortTest();
  }
}
