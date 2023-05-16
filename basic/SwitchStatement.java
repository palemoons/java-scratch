package basic;

public class SwitchStatement {
  public static void main(String args[]) {
    scoreToLevelTest();
  }

  public static char scoreToLevel(int paraScore) {
    // E stands for error, and F stands for fail.
    char resultLevel = 'E';

    // Divided by 10, the result ranges from 0 to 10
    int tempDigitalLevel = paraScore / 10;

    // The use of break is important.
    switch (tempDigitalLevel) {
      case 10:
      case 9:
        resultLevel = 'A';
        break;
      case 8:
        resultLevel = 'B';
        break;
      case 7:
        resultLevel = 'C';
        break;
      case 6:
        resultLevel = 'D';
        break;
      case 5:
      case 4:
      case 3:
      case 2:
      case 1:
      case 0:
        resultLevel = 'F';
        break;
      default:
        resultLevel = 'E';
    }
    return resultLevel;
  }

  public static void scoreToLevelTest() {
    int tempScore = 100;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 91;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 82;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 75;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 66;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 52;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 8;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));

    tempScore = 120;
    System.out.println("Score " + tempScore + " to level is: " + scoreToLevel(tempScore));
  }
}
