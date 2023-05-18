package datastructure;

public class SequentialList {
  // The maximal length of the list. Constant.
  public static final int MAX_LENGTH = 10;
  // The acutal length not exceeding MAX_LENGTH.
  int length;
  // The data stored in an array.
  int[] data;

  // Constructor function
  public SequentialList() {
    length = 0;
    data = new int[MAX_LENGTH];
  }

  // Copy Constructor function
  public SequentialList(int[] paraArray) {
    data = new int[MAX_LENGTH];
    length = paraArray.length;

    // Copy data.
    for (int i = 0; i < paraArray.length; i++)
      data[i] = paraArray[i];
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";

    if (length == 0)
      return "empty";

    for (int i = 0; i < length - 1; i++)
      resultString += data[i] + ", ";

    resultString += data[length - 1];

    return resultString;
  }

  // Reset to empty.
  public void reset() {
    length = 0;
  }

  // Find the index of the given value.
  public int indexOf(int paraValue) {
    int tempPosistion = -1;
    for (int i = 0; i < length; i++) {
      if (data[i] == paraValue) {
        tempPosistion = i;
        break;
      }
    }
    return tempPosistion;
  }

  /*
   * Insert a value to a position.
   * 
   * @param paraPosisition The given position.
   * 
   * @param paraValue The given value.
   * 
   * @return Success or not.
   */
  public boolean insert(int paraPosition, int paraValue) {
    if (length == MAX_LENGTH) {
      System.out.println("List full.");
      return false;
    }
    if (paraPosition < 0 || paraPosition > length) {
      System.out.println("The position " + paraPosition + " is out of bounds.");
      return false;
    }

    for (int i = length; i > paraPosition; i--)
      data[i] = data[i - 1];
    data[paraPosition] = paraValue;
    length++;
    return true;
  }

  /*
   * Delete a value at a position.
   * 
   * @param paraPosition The given position.
   * 
   * @return Success or not.
   */
  public boolean delete(int paraPosition) {
    if (paraPosition < 0 || paraPosition >= length) {
      System.out.println("The position " + paraPosition + " is out of bounds.");
      return false;
    }

    for (int i = paraPosition; i < length - 1; i++)
      data[i] = data[i + 1];
    length--;
    return true;
  }

  public static void main(String args[]) {
    // day11Main();
    day12Main();
  }

  public static void day11Main() {
    int[] tempArray = { 1, 4, 6, 9 };
    SequentialList tempFirstList = new SequentialList(tempArray);
    System.out.println("Initialized, the list is: " + tempFirstList.toString());
    System.out.println("Again, the list is: " + tempFirstList);

    tempFirstList.reset();
    System.out.println("After reset, the list is: " + tempFirstList);
  }

  public static void day12Main() {
    int[] tempArray = { 1, 4, 6, 9 };
    SequentialList tempFirstList = new SequentialList(tempArray);
    System.out.println("Initialized, the list is: " + tempFirstList.toString());
    System.out.println("Again, the list is: " + tempFirstList);

    int tempValue = 4;
    int tempPosition = tempFirstList.indexOf(tempValue);
    System.out.println("The position of " + tempValue + " is " + tempPosition);

    tempValue = 5;
    tempPosition = tempFirstList.indexOf(tempValue);
    System.out.println("The position of " + tempValue + " is " + tempPosition);

    tempPosition = 2;
    tempValue = 5;
    tempFirstList.insert(tempValue, tempPosition);
    System.out
        .println("After inserting " + tempValue + " to position " + tempPosition + ", the list is: " + tempFirstList);

    tempPosition = 8;
    tempValue = 10;
    tempFirstList.insert(tempValue, tempPosition);
    System.out
        .println("After inserting " + tempValue + " to position " + tempPosition + ", the list is: " + tempFirstList);

    tempPosition = 3;
    tempFirstList.delete(tempPosition);
    System.out.println("After deleting data at position " + tempPosition + ", the list is: " + tempFirstList);

    for (int i = 0; i < 8; i++) {
      tempFirstList.insert(i, i);
      System.out.println("After inserting " + i + ", the list is: " + tempFirstList);
    }

    tempFirstList.reset();
    System.out.println("After reseting, the list is: " + tempFirstList);
  }
}
