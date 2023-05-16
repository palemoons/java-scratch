package basic;
public class ForStatement {
  public static void main(String args[]) {
    ForStatementTest();
  }

  public static void ForStatementTest() {
    int tempN = 10;
    System.out.println("1 add to " + tempN + " is: " + addToN(tempN));

    tempN = 0;
    System.out.println("1 add to " + tempN + " is: " + addToN(tempN));

    int tempStepLength = 1;
    tempN = 10;
    System.out.println("1 add to " + tempN + " with step length " + tempStepLength + " is: "
        + addToNWithStepLength(tempN, tempStepLength));

    tempStepLength = 2;
    System.out.println("1 add to " + tempN + " with step length " + tempStepLength + " is: "
        + addToNWithStepLength(tempN, tempStepLength));
  }

  public static int addToN(int paraN) {
    int resultSum = 0;
    for (int i = 1; i <= paraN; i++) {
      resultSum += i;
    }
    return resultSum;
  }

  public static int addToNWithStepLength(int paraN, int paraStepLength) {
    int resultSum = 0;
    for (int i = 1; i <= paraN; i += paraStepLength) {
      resultSum += i;
    }
    return resultSum;
  }
}
