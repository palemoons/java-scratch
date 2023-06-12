package machinelearning.knn;

import java.io.*;

/* 
 * Recommendation with M-distance.
 */
public class MBR {
  // Default rating for 1-5 points.
  public static final double DEFAULT_RATING = 3.0;
  // The total number of users.
  private int numUsers;
  // The total number of items.
  private int numItems;
  // The total number of ratings (non-zero values)
  private int numRatings;
  // The predictions.
  private double[] predictions;
  // Compressed rating matrix. User-item-rating triples.
  private int[][] compressedRatingMatrix;
  // The degree of users (how many item he has rated).
  private int[] userDegrees;
  // The average rating of the current user.
  private double[] userAverageRatings;
  // The degrees of users.
  private int[] itemDegrees;
  // The average rating of the current item.
  private double[] itemAverageRatings;
  // The first user start from 0.
  private int[] userStartingIndices;
  // Number of non-neighbor objects.
  private int numNonNeighbors;
  // The radius (delta) for determining the neighborhood.
  private double radius;

  /*
   * Rating matrix Constructor.
   * 
   * @param paraRatingFilename. The rating filename.
   * 
   * @param paraNumUsers. Number of users.
   * 
   * @param paraNumItems. Number of items.
   * 
   * @param paraNumRatings. Number of ratings.
   */
  public MBR(String paraFilename, int paraNumUsers, int paraNumItems, int paraNumRatings) throws Exception {
    // Step 1. Initialize.
    numItems = paraNumItems;
    numUsers = paraNumUsers;
    numRatings = paraNumRatings;

    userDegrees = new int[numUsers];
    userStartingIndices = new int[numUsers + 1];
    userAverageRatings = new double[numUsers];
    itemDegrees = new int[numItems];
    compressedRatingMatrix = new int[numRatings][3];
    itemAverageRatings = new double[numItems];

    predictions = new double[numRatings];

    System.out.println("Reading " + paraFilename);

    // Step 2. Read the data file.
    File tempFile = new File(paraFilename);
    if (!tempFile.exists()) {
      System.out.println("File " + paraFilename + " does not exists.");
      System.exit(0);
    }
    BufferedReader tempBufReader = new BufferedReader(new FileReader(tempFile));
    String tempString;
    String[] tempStrArray;
    int tempIndex = 0;
    userStartingIndices[0] = 0;
    userStartingIndices[numUsers] = numRatings;
    while ((tempString = tempBufReader.readLine()) != null) {
      // Each line has three values.
      tempStrArray = tempString.split(",");
      compressedRatingMatrix[tempIndex][0] = Integer.parseInt(tempStrArray[0]);// User id.
      compressedRatingMatrix[tempIndex][1] = Integer.parseInt(tempStrArray[1]);// Item id.
      compressedRatingMatrix[tempIndex][2] = Integer.parseInt(tempStrArray[2]);// Rating.

      userDegrees[compressedRatingMatrix[tempIndex][0]]++;
      itemDegrees[compressedRatingMatrix[tempIndex][1]]++;

      if (tempIndex > 0) {
        // Starting to read the data of a new user.
        if (compressedRatingMatrix[tempIndex][0] != compressedRatingMatrix[tempIndex - 1][0])
          userStartingIndices[compressedRatingMatrix[tempIndex][0]] = tempIndex;
      }
      tempIndex++;
    }
    tempBufReader.close();

    double[] tempUserTotalScore = new double[numUsers];
    double[] tempItemTotalScore = new double[numItems];
    for (int i = 0; i < numRatings; i++) {
      tempUserTotalScore[compressedRatingMatrix[i][0]] += compressedRatingMatrix[i][2];
      tempItemTotalScore[compressedRatingMatrix[i][1]] += compressedRatingMatrix[i][2];
    }
    for (int i = 0; i < numUsers; i++)
      userAverageRatings[i] = tempUserTotalScore[i] / userDegrees[i];
    for (int i = 0; i < numItems; i++)
      itemAverageRatings[i] = tempItemTotalScore[i] / itemDegrees[i];
  }

  /*
   * Set the radius (delta).
   * 
   * @param paraRadius. The given radius.
   */
  public void setRadius(double paraRadius) {
    if (paraRadius > 0)
      radius = paraRadius;
    else
      radius = 0.1;
  }

  /*
   * Item-based leave-one-out prediction. The predicted values are stored in
   * predictions.
   * 
   * @see predictions.
   */
  public void itemBasedLeaveOneOutPrediction() {
    double tempItemAverageRating;
    int tempUser, tempItem, tempRating;
    System.out.println("\r\nItem-based leaveOntOutPrediction for radius " + String.format("%3.2f", radius));

    numNonNeighbors = 0;
    for (int i = 0; i < numRatings; i++) {
      tempUser = compressedRatingMatrix[i][0];
      tempItem = compressedRatingMatrix[i][1];
      tempRating = compressedRatingMatrix[i][2];

      // Step 1. Recompute average rating of the current item.
      tempItemAverageRating = (itemAverageRatings[tempItem] * itemDegrees[tempItem] - tempRating)
          / (itemDegrees[tempItem] - 1);

      // Step 2. Recompute neightbors, at the same time obtain the ratings of
      // neighbors.
      int tempNeighbors = 0;
      double tempTotal = 0;
      int tempComparedItem;
      for (int j = userStartingIndices[tempUser]; j < userStartingIndices[tempUser + 1]; j++) {
        tempComparedItem = compressedRatingMatrix[j][1];
        if (tempItem == tempComparedItem)
          continue;
        // Item-based M-distance
        if (Math.abs(tempItemAverageRating - itemAverageRatings[tempComparedItem]) < radius) {
          tempTotal += compressedRatingMatrix[j][2];
          tempNeighbors++;
        }
      }

      // Step 3. Predict as the average value of neighbors.
      if (tempNeighbors > 0)
        predictions[i] = tempTotal / tempNeighbors;
      else {
        predictions[i] = DEFAULT_RATING;
        numNonNeighbors++;
      }
    }
  }

  /*
   * User-based leave-one-out prediction.
   */
  public void userBasedLeaveOneOutPrediction() {
    double tempUserAverageRating;
    int tempUser, tempItem, tempRating;
    System.out.println("\r\nUser-based leaveOntOutPrediction for radius " + String.format("%3.2f", radius));

    // Create arrays for user-based recommendation.
    int[][] tempCompressedRatingMatrix = new int[numRatings][3];
    for (int i = 0; i < numRatings; i++) {
      // Copy.
      tempCompressedRatingMatrix[i][0] = compressedRatingMatrix[i][0];
      tempCompressedRatingMatrix[i][1] = compressedRatingMatrix[i][1];
      tempCompressedRatingMatrix[i][2] = compressedRatingMatrix[i][2];
    }
    // Sort by item id.
    mergeSort(0, numRatings - 1, tempCompressedRatingMatrix);

    // Create array for item Indices.
    int[] itemStartingIndices = new int[numRatings + 1];
    itemStartingIndices[0] = 0;
    itemStartingIndices[numItems] = numRatings;
    for (int i = 1; i < numRatings; i++) {
      if (tempCompressedRatingMatrix[i][1] != tempCompressedRatingMatrix[i - 1][1])
        itemStartingIndices[tempCompressedRatingMatrix[i][1]] = i;
    }

    numNonNeighbors = 0;
    for (int i = 0; i < numRatings; i++) {
      tempUser = tempCompressedRatingMatrix[i][0];
      tempItem = tempCompressedRatingMatrix[i][1];
      tempRating = tempCompressedRatingMatrix[i][2];

      // Step 1. Recompute average rating for current user.
      tempUserAverageRating = (userAverageRatings[tempUser] * userDegrees[tempUser] - tempRating)
          / (userDegrees[tempUser] - 1);

      // Step 2. Recompute neighbors.
      int tempNeighbors = 0;
      double tempTotal = 0;
      int tempComparedUser;
      for (int j = itemStartingIndices[tempItem]; j < itemStartingIndices[tempItem
          + 1]; j++) {
        tempComparedUser = tempCompressedRatingMatrix[j][0];
        if (tempUser == tempComparedUser)
          continue;
        // User-based M-distance
        if (Math.abs(tempUserAverageRating - userAverageRatings[tempComparedUser]) < radius) {
          tempTotal += tempCompressedRatingMatrix[j][2];
          tempNeighbors++;
        }
      }

      // Step 3. Predict as the average value of neighbors.
      if (tempNeighbors > 0)
        predictions[i] = tempTotal / tempNeighbors;
      else {
        predictions[i] = DEFAULT_RATING;
        numNonNeighbors++;
      }
    }
  }

  /*
   * recursive merge sort for user-based recommendation.
   * 
   * @param paraLeft.
   * 
   * @param paraRight.
   * 
   * @param paraArray.
   */
  public void mergeSort(int paraLeft, int paraRight, int[][] paraArray) {
    if (paraLeft >= paraRight)
      return;

    int mid = (paraLeft + paraRight) / 2;
    mergeSort(paraLeft, mid, paraArray);
    mergeSort(mid + 1, paraRight, paraArray);

    int[][] tempArray = new int[paraRight - paraLeft + 1][3];
    for (int i = 0; i < paraRight - paraLeft + 1; i++) {
      tempArray[i][0] = paraArray[i + paraLeft][0];
      tempArray[i][1] = paraArray[i + paraLeft][1];
      tempArray[i][2] = paraArray[i + paraLeft][2];
    }
    int tempIndex = paraLeft;
    int tempLeft = paraLeft, tempRight = mid + 1;
    while (tempLeft < mid + 1 && tempRight < paraRight + 1) {
      if (tempArray[tempLeft - paraLeft][1] < tempArray[tempRight - paraLeft][1]) {
        paraArray[tempIndex][0] = tempArray[tempLeft - paraLeft][0];
        paraArray[tempIndex][1] = tempArray[tempLeft - paraLeft][1];
        paraArray[tempIndex++][2] = tempArray[tempLeft++ - paraLeft][2];
      } else {
        paraArray[tempIndex][0] = tempArray[tempRight - paraLeft][0];
        paraArray[tempIndex][1] = tempArray[tempRight - paraLeft][1];
        paraArray[tempIndex++] = tempArray[tempRight++ - paraLeft];
      }
    }
    while (tempLeft < mid + 1) {
      paraArray[tempIndex][0] = tempArray[tempLeft - paraLeft][0];
      paraArray[tempIndex][1] = tempArray[tempLeft - paraLeft][1];
      paraArray[tempIndex++][2] = tempArray[tempLeft++ - paraLeft][2];
    }
    while (tempRight < paraRight + 1) {
      paraArray[tempIndex][0] = tempArray[tempRight - paraLeft][0];
      paraArray[tempIndex][1] = tempArray[tempRight - paraLeft][1];
      paraArray[tempIndex++][2] = tempArray[tempRight++ - paraLeft][2];
    }
  }

  /*
   * Compute the MAE based on the deviation of each leave-one-out.
   */
  public double computeMAE() throws Exception {
    double tempTotalError = 0;
    for (int i = 0; i < predictions.length; i++) {
      tempTotalError += Math.abs(predictions[i] - compressedRatingMatrix[i][2]);
    }
    return tempTotalError / predictions.length;
  }

  /*
   * Compute the RMSE based on the deviation of each leave-one-out.
   */
  public double computeRMSE() throws Exception {
    double tempTotalError = 0;
    for (int i = 0; i < predictions.length; i++) {
      tempTotalError += (predictions[i] - compressedRatingMatrix[i][2])
          * (predictions[i] - compressedRatingMatrix[i][2]);
    }
    double tempAverage = tempTotalError / predictions.length;

    return Math.sqrt(tempAverage);
  }

  public static void main(String[] args) {
    try {
      MBR tempRecommender = new MBR("./sample-data/movielens-943u1682m.txt", 943, 1682, 100000);

      for (double tempRadius = 0.2; tempRadius < 0.6; tempRadius += 0.1) {
        tempRecommender.setRadius(tempRadius);

        // Get neighboring items.
        // tempRecommender.itemBasedLeaveOneOutPrediction();

        // Get neighboring users with similar interests.
        tempRecommender.userBasedLeaveOneOutPrediction();
        double tempMAE = tempRecommender.computeMAE();
        double tempRMSE = tempRecommender.computeRMSE();

        System.out.println("Radius = " + String.format("%3.2f", tempRadius) + ", MAE = "
            + String.format("%4.3f", tempMAE) + ", RMSE = " + String.format("%4.3f", tempRMSE)
            + ", numNonNeighbors = " + tempRecommender.numNonNeighbors);
      }
    } catch (Exception ee) {
      System.out.println(ee);
    }
  }
}