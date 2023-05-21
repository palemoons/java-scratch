package datastructure;

public class MyString {
  // Constant. Max length of string.
  public static final int MAX_LENGTH = 10;
  // Actual length.
  int length;
  // The data.
  char[] data;

  // Constuctor.
  public MyString() {
    length = 0;
    data = new char[MAX_LENGTH];
  }

  // Copy Constructor.
  public MyString(String paraString) {
    data = new char[MAX_LENGTH];
    length = paraString.length();
    for (int i = 0; i < length; i++)
      data[i] = paraString.charAt(i);
  }

  // Overrides toString method.
  public String toString() {
    String resulString = "";
    for (int i = 0; i < length; i++)
      resulString += data[i];
    return resulString;
  }

  /*
   * Locate the position of a substring.
   * 
   * @param paraString The given substring.
   * 
   * @return The first position or -1 if no matching.
   */
  public int locate(MyString paraMyString) {
    boolean tempMatch = false;
    for (int i = 0; i < length - paraMyString.length + 1; i++) {
      // Initalize.
      tempMatch = true;
      for (int j = 0; j < paraMyString.length; j++) {
        if (data[i + j] != paraMyString.data[j]) {
          tempMatch = false;
          break;
        }
      }
      if (tempMatch)
        return i;
    }
    return -1;
  }

  /*
   * Get a substring.
   * 
   * @param paraString The given substring.
   * 
   * @param paraStartPosition The start position in the original string.
   * 
   * @param paraLength The length of the new string.
   * 
   * @return The first position or -1 if no matching.
   */
  public MyString substring(int paraStartPosition, int paraLength) {
    if (paraStartPosition + paraLength > length) {
      System.out.println("the bound is exceeded.");
      return null;
    }
    MyString resultMyString = new MyString();
    resultMyString.length = paraLength;
    for (int i = 0; i < paraLength; i++)
      resultMyString.data[i] = data[paraStartPosition + i];
    return resultMyString;
  }

  public static void main(String args[]) {
    MyString tempFirstString = new MyString("I like ik.");
    MyString tempSecondString = new MyString("ik");
    int tempPosition = tempFirstString.locate(tempSecondString);
    System.out
        .println("The position of\"" + tempSecondString + "\" in \"" + tempFirstString + "\" is: " + tempPosition);
    MyString tempThirdSrting = new MyString("ki");
    tempPosition = tempFirstString.locate(tempThirdSrting);
    System.out
        .println("The position of\"" + tempThirdSrting + "\" in \"" + tempFirstString + "\" is: " + tempPosition);

    tempThirdSrting = tempFirstString.substring(1, 2);
    System.out.println("The substring is: \"" + tempThirdSrting + "\"");

    tempThirdSrting = tempFirstString.substring(5, 5);
    System.out.println("The substring is: \"" + tempThirdSrting + "\"");

    tempThirdSrting = tempFirstString.substring(5, 6);
    System.out.println("The substring is: \"" + tempThirdSrting + "\"");
  }
}