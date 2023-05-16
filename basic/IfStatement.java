package basic;

public class IfStatement {
  public static void main(String args[]) {
    int tempNumber1, tempNumber2;

    // Try a positive value
    tempNumber1 = 5;

    if (tempNumber1 >= 0) {
      tempNumber2 = tempNumber1;
    } else {
      tempNumber2 = -tempNumber1;
    }

    System.out.println("The absolute value of " + tempNumber1 + " is " + tempNumber2);

    // Try a negetive value
    tempNumber1 = -3;

    if (tempNumber1 >= 0) {
      tempNumber2 = tempNumber1;
    } else {
      tempNumber2 = -tempNumber1;
    }

    System.out.println("The absolute value of " + tempNumber1 + " is " + tempNumber2);

    // Use a method/function for this purpose.
    tempNumber1 = 6;
    System.out.println("The absolute value of " + tempNumber1 + " is " + abs(tempNumber1));
    tempNumber1 = -8;
    System.out.println("The absolute value of " + tempNumber1 + " is " + abs(tempNumber1));
  }

  public static int abs(int paraValue) {
    if (paraValue >= 0) {
      return paraValue;
    } else {
      return -paraValue;
    }
  }
}
