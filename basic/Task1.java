package basic;

import java.util.Arrays;
import java.util.Random;

public class Task1 {
  public static void main(String args[]) {
    task1();
  }

  public static void task1() {
    // Step 1. Generate the data with n students and m courses.
    // Set these values.
    int n = 10;
    int m = 3;
    int lowerBound = 50;
    int upperBound = 100;
    int threshold = 60;

    // Generate random scores.
    Random tempRandom = new Random();
    int[][] data = new int[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        data[i][j] = lowerBound + tempRandom.nextInt(upperBound - lowerBound);
      }
    }

    System.out.println("The data is: \r\n" + Arrays.deepToString(data));

    // Step 2. Compute the total score of each student.
    int[] totalScores = new int[n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (data[i][j] < threshold) {
          totalScores[i] = 0;
          break;
        }

        totalScores[i] += data[i][j];
      }
    }

    System.out.println("The total scores are:\r\n" + Arrays.toString(totalScores));

    // Step 3. Find the best and worst student.
    // Typical initialization for index: invalid value.
    int tempBestIndex = -1;
    int tempWorstIndex = -1;
    // Typical initialization for best and worst values.
    // They must be replaced by valid values.
    int tempBestScore = 0;
    int tempWorstScore = m * upperBound + 1;
    for (int i = 0; i < n; i++) {
      // Ignore failed students.
      if (totalScores[i] == 0)
        continue;
      if (tempBestScore < totalScores[i]) {
        tempBestScore = totalScores[i];
        tempBestIndex = i;
      }
      if (tempWorstScore > totalScores[i]) {
        tempWorstScore = totalScores[i];
        tempWorstIndex = i;
      }
    }

    // Step 4. Output the student number and score.
    if (tempBestIndex == -1) {
      System.out.println("Cannot find best students. All students have failed.");
    } else {
      System.out
          .println("The best student is No." + tempBestIndex + " with scores: " + Arrays.toString(data[tempBestIndex]));
    }

    if (tempWorstIndex == -1) {
      System.out.println("Cannot find worst students. All students have failed.");
    } else {
      System.out
          .println(
              "The worst student is No." + tempWorstIndex + " with scores: " + Arrays.toString(data[tempWorstIndex]));
    }
  }
}
