package datastructure;

public class CharStack {
  // The max depth.
  public static final int MAX_DEPTH = 10;
  // The actual depth.
  int depth;
  // The data.
  char[] data;

  // Contructor
  public CharStack() {
    depth = 0;
    data = new char[MAX_DEPTH];
  }

  // Overrides toString method.
  public String toString() {
    String resultString = "";
    for (int i = 0; i < depth; i++)
      resultString += data[i];
    return resultString;
  }

  /*
   * Push an element.
   * 
   * @param paraChar The given char.
   * 
   * @return Success or not.
   */
  public boolean push(char paraChar) {
    if (depth == MAX_DEPTH) {
      System.out.println("Stack full.");
      return false;
    }
    data[depth++] = paraChar;
    return true;
  }

  /*
   * Pop an element.
   * 
   * @return The popped char.
   */
  public char pop() {
    if (depth == 0) {
      System.out.println("Nothing to pop.");
      return '\0';
    }
    char resultChar = data[--depth];
    return resultChar;
  }

  /*
   * Test whether the brackets match or not.
   * 
   * @param paraString The given expression
   * 
   * @return Match or not.
   */
  public static boolean bracketMatching(String paraString) {
    // Step 1. Initalize the stack through push a '#' at the bottom.
    CharStack tempStack = new CharStack();
    tempStack.push('#');
    char tempChar, tempPopedChar;

    // Step 2. Process the string.
    for (int i = 0; i < paraString.length(); i++) {
      tempChar = paraString.charAt(i);

      switch (tempChar) {
        case '(':
        case '[':
        case '{':
          tempStack.push(tempChar);
          break;
        case ')':
          tempPopedChar = tempStack.pop();
          if (tempPopedChar != '(') {
            return false;
          }
          break;
        case '}':
          tempPopedChar = tempStack.pop();
          if (tempPopedChar != '{') {
            return false;
          }
          break;
        case ']':
          tempPopedChar = tempStack.pop();
          if (tempPopedChar != '[') {
            return false;
          }
          break;
        default:
          // continue
      }
    }
    return true;
  }

  public static void main(String args[]) {
    CharStack tempStack = new CharStack();
    for (char ch = 'a'; ch < 'm'; ch++) {
      tempStack.push(ch);
      System.out.println("The current stack is " + tempStack);
    }
    char tempChar;
    for (int i = 0; i < 12; i++) {
      tempChar = tempStack.pop();
      System.out.println("Popped: " + tempChar);
      System.out.println("The current stack is " + tempStack);
    }

    boolean tempMatch;
    String tempExpression = "[2 + (1 - 3)] * 4";
    tempMatch = bracketMatching(tempExpression);
    System.out.println("Is the expression " + tempExpression + " bracket matching?" + tempMatch);

    tempExpression = "( )  )";
    tempMatch = bracketMatching(tempExpression);
    System.out.println("Is the expression " + tempExpression + " bracket matching?" + tempMatch);

    tempExpression = "()()(())";
    tempMatch = bracketMatching(tempExpression);
    System.out.println("Is the expression " + tempExpression + " bracket matching?" + tempMatch);

    tempExpression = "({}[])";
    tempMatch = bracketMatching(tempExpression);
    System.out.println("Is the expression " + tempExpression + " bracket matching?" + tempMatch);

    tempExpression = ")(";
    tempMatch = bracketMatching(tempExpression);
    System.out.println("Is the expression " + tempExpression + " bracket matching?" + tempMatch);
  }
}
