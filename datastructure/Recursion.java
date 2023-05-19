package datastructure;

public class Recursion {
  /*
   * Sum to N. Not loop but stack used.
   * 
   * @param paraN The given value
   * 
   * @return The sum.
   */
  public static int sumToN(int paraN) {
    if (paraN <= 0)
      return 0;
    return sumToN(paraN - 1) + paraN;
  }

  /*
   * Fibonacci sequence.
   * 
   * @param paraN The given value.
   * 
   * @return The sum.
   */
  public static int fibonacci(int paraN) {
    if (paraN <= 0)
      return 0;
    if (paraN == 1)
      return 1;
    return fibonacci(paraN - 1) + fibonacci(paraN - 2);
  }

  public static void main(String args[]) {
    int tempValue = 5;
    System.out.println("0 sum to " + tempValue + " = " + sumToN(tempValue));
    tempValue = -1;
    System.out.println("0 sum to " + tempValue + " = " + sumToN(tempValue));

    for (int i = 0; i < 10; i++) {
      System.out.println("Fibonacci " + i + ": " + fibonacci(i));
    }
  }
}
