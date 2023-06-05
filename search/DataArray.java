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
  }
}
