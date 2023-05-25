package datastructure.stack;

public class ObjectStack {
  // The depth.
  public static final int MAX_DEPTH = 10;
  // The actual depth.
  int depth;
  // The data.
  Object[] data;

  // Contructor.
  public ObjectStack() {
    depth = 0;
    data = new Object[MAX_DEPTH];
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
   * @param paraObject The given object.
   * 
   * @return Success or not.
   */
  public boolean push(Object paraObject) {
    if (depth == MAX_DEPTH) {
      System.out.println("Stack full.");
      return false;
    }
    data[depth++] = paraObject;
    return true;
  }

  /*
   * Pop an element.
   * 
   * @return The object at the top of the stack.
   */
  public Object pop() {
    if (depth == 0) {
      System.out.println("Nothing to pop.");
      return '\0';
    }
    Object resultObject = data[--depth];
    return resultObject;
  }

  /*
   * Check if the stack is empty.
   * 
   * @return True if empty.
   */
  public boolean isEmpty() {
    if (depth == 0)
      return true;
    else
      return false;
  }

  public static void main(String args[]) {
    ObjectStack tempStack = new ObjectStack();
    for (char ch = 'a'; ch < 'm'; ch++) {
      tempStack.push(new Character(ch));
      System.out.println("The current stack is: " + tempStack);
    }
    char tempChar;
    for (int i = 0; i < 12; i++) {
      tempChar = ((Character) tempStack.pop()).charValue();
      System.out.println("Poped: " + tempChar);
      System.out.println("The current stack is: " + tempStack);
    }
  }
}
