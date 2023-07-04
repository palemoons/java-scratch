package machinelearning.recommendersystem;

import java.io.*;
import java.util.*;

public class MatrixFactorization {
  public class Triple {
    public int user;
    public int item;
    public double rating;

    /*
     * The constructor.
     */
    public Triple() {
      user = -1;
      item = -1;
      rating = -1;
    }

    /*
     * The copy constructor.
     */
    public Triple(int paraUser, int paraItem, double paraRating) {
      user = paraUser;
      item = paraItem;
      rating = paraRating;
    }

    public String toString() {
      return "" + user + ", " + item + ", " + rating;
    }
  }

  // Used to generate random numbers.
  Random rand = new Random();
  // Number of users.
  int numUsers;
  // Number of items.
  int numItems;
  // Number of ratings.
  int numRatings;
  // Training data.
  Triple[] dataset;
  // A parameter for controlling learning regular.
  double alpha;
  // A parameter for controlling the learning speed.
  double lambda;
  // The low rank of the small matrices.
  int rank;
  // The user matrix U.
  double[][] userSubspace;
  // The item matrix V.
  double[][] itemSubspace;
  // The lower bound of the rating value.
  double ratingLowerBound;
  // The upper bound of the rating value.
  double ratingUpperBound;

  /**
   ************************ 
   * The first constructor.
   * 
   * @param paraFilename   The data filename.
   * @param paraNumUsers   The number of users.
   * @param paraNumItems   The number of items.
   * @param paraNumRatings The number of ratings.
   ************************ 
   */
  public MatrixFactorization(String paraFilename, int paraNumUsers, int paraNumItems, int paraNumRatings,
      double paraRatingLowerBound, double paraRatingUpperBound) {
    numUsers = paraNumUsers;
    numItems = paraNumItems;
    numRatings = paraNumRatings;
    ratingLowerBound = paraRatingLowerBound;
    ratingUpperBound = paraRatingUpperBound;

    try {
      readData(paraFilename, paraNumUsers, paraNumItems, paraNumRatings);
    } catch (Exception ee) {
      System.out.println("File " + paraFilename + " cannot be read!\r\n" + ee);
      System.exit(0);
    }
  }

  /**
   * Set parameters.
   * 
   * @param paraRank. The given rank.
   * 
   * @throws IOException.
   */
  public void setParameters(int paraRank, double paraAlpha, double paraLambda) {
    rank = paraRank;
    alpha = paraAlpha;
    lambda = paraLambda;
  }

  /**
   * Read the data from the file.
   * 
   * @param paraFilename. The given file.
   * 
   * @throws IOException.
   */
  public void readData(String paraFilename, int paraNumUsers, int paraNumItems, int paraNumRatings) throws IOException {
    File tempFile = new File(paraFilename);
    if (!tempFile.exists()) {
      System.out.println("File " + paraFilename + " does not exists.");
      System.exit(0);
    }
    BufferedReader tempBufferReader = new BufferedReader(new FileReader(tempFile));

    // Allocate space.
    dataset = new Triple[paraNumRatings];
    String tempString;
    String[] tempStringArray;
    for (int i = 0; i < paraNumRatings; i++) {
      tempString = tempBufferReader.readLine();
      tempStringArray = tempString.split(",");
      dataset[i] = new Triple(Integer.parseInt(tempStringArray[0]), Integer.parseInt(tempStringArray[1]),
          Double.parseDouble(tempStringArray[2]));
    }
    tempBufferReader.close();
  }

  /*
   * Initialize subspaces. Each value is in [0, 1].
   */
  void initializeSubspaces() {
    userSubspace = new double[numUsers][rank];
    for (int i = 0; i < numUsers; i++)
      for (int j = 0; j < rank; j++)
        userSubspace[i][j] = rand.nextDouble();

    itemSubspace = new double[numItems][rank];
    for (int i = 0; i < numItems; i++)
      for (int j = 0; j < rank; j++)
        itemSubspace[i][j] = rand.nextDouble();
  }

  /*
   * Predict the rating of the user to the item.
   * 
   * @param paraUser. The user index.
   */
  public double predict(int paraUser, int paraItem) {
    double resultValue = 0;
    for (int i = 0; i < rank; i++)
      // The row vector of an user and the row column vector of an item.
      resultValue += userSubspace[paraUser][i] * itemSubspace[paraItem][i];
    return resultValue;
  }

  /*
   * Train.
   * 
   * @param paraRounds. The number of rounds.
   */
  public void train(int paraRounds) {
    initializeSubspaces();
    for (int i = 0; i < paraRounds; i++) {
      updateNoRegular();
      if (i % 50 == 0) {
        System.out.println("Round " + i);
        System.out.println("MAE: " + mae());
      }
    }
  }

  /*
   * Update sub-spaces using the training data.
   */
  public void updateNoRegular() {
    for (int i = 0; i < numRatings; i++) {
      int tempUserId = dataset[i].user;
      int tempItemId = dataset[i].item;
      double tempRate = dataset[i].rating;

      double tempResidual = tempRate - predict(tempUserId, tempItemId);

      // Update user subspace.
      double tempValue = 0;
      for (int j = 0; j < rank; j++) {
        tempValue = 2 * tempResidual * itemSubspace[tempItemId][j];
        userSubspace[tempUserId][j] += alpha * tempValue;
      }

      for (int j = 0; j < rank; j++) {
        tempValue = 2 * tempResidual * userSubspace[tempUserId][j];
        itemSubspace[tempItemId][j] += alpha * tempValue;
      }
    }
  }

  /*
   * Compute the RSME.
   * 
   * @return RSME of the current factorization.
   */
  public double rsme() {
    double reusltRsme = 0;
    int tempTestCount = 0;
    for (int i = 0; i < numRatings; i++) {
      int tempUserIndex = dataset[i].user;
      int tempItemIndex = dataset[i].item;
      double tempRate = dataset[i].rating;
      double tempPrediction = predict(tempUserIndex, tempItemIndex);

      if (tempPrediction < ratingLowerBound)
        tempPrediction = ratingLowerBound;
      else if (tempPrediction > ratingUpperBound)
        tempPrediction = ratingUpperBound;

      double tempError = tempRate - tempPrediction;
      reusltRsme += tempError * tempError;
      tempTestCount++;
    }
    return Math.sqrt(reusltRsme / tempTestCount);
  }

  /*
   * Compute the MAE.
   * 
   * @return MAE of the current factorization.
   */
  public double mae() {
    double resultMae = 0;
    int tempTestCount = 0;
    for (int i = 0; i < numRatings; i++) {
      int tempUserIndex = dataset[i].user;
      int tempItemIndex = dataset[i].item;
      double tempRate = dataset[i].rating;
      double tempPrediction = predict(tempUserIndex, tempItemIndex);
      if (tempPrediction < ratingLowerBound)
        tempPrediction = ratingLowerBound;
      else if (tempPrediction > ratingUpperBound)
        tempPrediction = ratingUpperBound;
      double tempError = tempRate - tempPrediction;
      resultMae += Math.abs(tempError);
      tempTestCount++;
    }
    return (resultMae / tempTestCount);
  }

  /*
   * Compute the MAE.
   * 
   * @return MAE of the current factorization.
   */
  public static void testTrainingTesting(String paraFilename, int paraNumUsers, int paraNumItems, int paraNumRatings,
      double paraRatingLowerBound, double paraRatingUpperBound, int paraRounds) {
    try {
      // Step 1. Read the training and testing data.
      MatrixFactorization tempMF = new MatrixFactorization(paraFilename, paraNumUsers, paraNumItems, paraNumRatings,
          paraRatingLowerBound, paraRatingUpperBound);
      // Step 2. Set parameters.
      tempMF.setParameters(5, 0.0001, 0.005);
      // Step 3. Update and predict.
      System.out.println("Begin Training...");
      tempMF.train(paraRounds);
      double tempMAE = tempMF.mae();
      double tempRSME = tempMF.rsme();
      System.out.println("Finally, MAE = " + tempMAE + " , RSME = " + tempRSME);
    } catch (Exception ee) {
      ee.printStackTrace();
    }
  }

  public static void main(String args[]) {
    testTrainingTesting("./sample-data/movielens-943u1682m.txt", 943, 1682, 10000, 1, 5, 2000);
  }
}
