package machinelearning.knn;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;

import weka.core.*;

/* 
 * KNN classification.
 */
public class KnnClassification {
  /*
   * Inner class for sort.
   */
  class elementNode {
    Object data;
    Object label;

    elementNode(Object paraData, Object paraLabel) {
      data = paraData;
      label = paraLabel;
    }

    public String toString() {
      String resultString = label + ": " + String.format("%4.3f", data);
      return resultString;
    }
  }

  // Manhattan distance.
  public static final int MANHATTAN = 0;
  // Euclidean distance.
  public static final int EUCLIDEAN = 1;
  // THe distance measure.
  public int distanceMeasure = EUCLIDEAN;
  // A random instance.
  public static final Random random = new Random();
  // The number of neighbors.
  int numNeighbors = 7;
  // The whole dataset.
  Instances dataset;
  // The training set. Represented by the indices of the data.
  int[] trainingSet;
  // The testing set. Represented by the indices of the data.
  int[] testingSet;
  // The predictions.
  int[] predictions;

  /*
   * The first constructor.
   * 
   * @param paraFilename. The arff filename.
   */
  public KnnClassification(String paraFilename) {
    try {
      FileReader fileReader = new FileReader(paraFilename);
      dataset = new Instances(fileReader);
      // The last attribute is the decision class.
      dataset.setClassIndex(dataset.numAttributes() - 1);
      fileReader.close();
    } catch (Exception ee) {
      System.out.println(
          "Error occurred while trying to read\'" + paraFilename + "\' in KnnClassification constructor.\r\n" + ee);
      System.exit(0);
    }
  }

  /*
   * Get a random indices for data randomization.
   * 
   * @param paraLength.
   * 
   * @return An array of indices, e.g., {4, 3, 1, 5, 0, 2} with length 6.
   */
  public static int[] getRandomIndices(int paraLength) {
    int[] resultIndices = new int[paraLength];
    // Step 1. Initialize.
    for (int i = 0; i < paraLength; i++)
      resultIndices[i] = i;

    // Step 2. Randomly swap.
    int tempFirst, tempSecond, tempValue;
    for (int i = 0; i < paraLength; i++) {
      // Generate two random indices.
      tempFirst = random.nextInt(paraLength);
      tempSecond = random.nextInt(paraLength);

      // Swap.
      tempValue = resultIndices[tempFirst];
      resultIndices[tempFirst] = resultIndices[tempSecond];
      resultIndices[tempSecond] = tempValue;
    }
    return resultIndices;
  }

  /*
   * Split the data into training and testing parts.
   * 
   * @param paraTrainingFraction.
   */
  public void splitTrainingTesting(double paraTrainingFraction) {
    int tempSize = dataset.numInstances();
    int[] tempIndices = getRandomIndices(tempSize);
    int tempTrainingSize = (int) (tempSize * paraTrainingFraction);

    trainingSet = new int[tempTrainingSize];
    testingSet = new int[tempSize - tempTrainingSize];

    for (int i = 0; i < tempTrainingSize; i++)
      trainingSet[i] = tempIndices[i];
    for (int i = 0; i < tempSize - tempTrainingSize; i++)
      testingSet[i] = tempIndices[tempTrainingSize + i];
  }

  /*
   * Predict for the whole testing set. The results are stored in predictions.
   */
  public void predict() {
    predictions = new int[testingSet.length];
    for (int i = 0; i < predictions.length; i++)
      predictions[i] = predict(testingSet[i]);
  }

  /*
   * Predict for given instance.
   * 
   * @return The prediction.
   */
  public int predict(int paraIndex) {
    elementNode[] tempNeighbors = computeNearests(paraIndex);
    // int[] tempIndices = new int[numNeighbors];
    // for(int i=0;i<numNeighbors;i++)
    // tempIndices[i] = (Integer)tempNeighbors[i].label;
    // int resultPrediction = simpleVoting(tempIndices);
    int resultPrediction = weightedVoting(tempNeighbors);

    return resultPrediction;
  }

  /*
   * The distance between two instances.
   * 
   * @param paraI. The index of the first instances.
   * 
   * @param paraJ. The index of the second instances.
   * 
   * @return The distance.
   */
  public double distance(int paraI, int paraJ) {
    double resultDistance = 0;
    double tempDifference;
    switch (distanceMeasure) {
      case MANHATTAN:
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
          tempDifference = dataset.instance(paraI).value(i) - dataset.instance(paraJ).value(i);
          if (tempDifference < 0)
            resultDistance -= tempDifference;
          else
            resultDistance += tempDifference;
        }
        break;

      case EUCLIDEAN:
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
          tempDifference = dataset.instance(paraI).value(i) - dataset.instance(paraJ).value(i);
          resultDistance += tempDifference * tempDifference;
        }
        break;

      default:
        System.out.println("Unsupported distance measure: " + distanceMeasure);
    }

    return resultDistance;
  }

  /*
   * Get the accuracy of the classifier.
   * 
   * @return The accuracy.
   */
  public double getAccuary() {
    // A double divides an int gets another double.
    double tempCorrect = 0;
    for (int i = 0; i < predictions.length; i++) {
      double tempClassValue = dataset.instance(testingSet[i]).classValue();
      if (predictions[i] == tempClassValue)
        tempCorrect++;
    }
    return tempCorrect / testingSet.length;
  }

  /*
   * Compute the nearest k neighbors.
   * 
   * @param paraK. The k value for KNN.
   * 
   * @param paraCurrent. Current instance. We are comparing it with all others.
   * 
   * @return The nearest instances.
   */
  public elementNode[] computeNearests(int paraCurrent) {
    elementNode[] resultNearests = new elementNode[numNeighbors];
    for (int i = 0; i < numNeighbors; i++)
      resultNearests[i] = new elementNode(0, 0);

    // Compute all distances to avoid redundant computation.
    double[] tempDistances = new double[trainingSet.length];
    for (int i = 0; i < trainingSet.length; i++)
      tempDistances[i] = distance(paraCurrent, trainingSet[i]);

    // Sort tempDistances.
    elementNode[] tempSortDistances = new elementNode[trainingSet.length];
    for (int i = 0; i < trainingSet.length; i++)
      tempSortDistances[i] = new elementNode(tempDistances[i], i);
    mergeSort(0, trainingSet.length - 1, tempSortDistances);

    // Select the nearest paraK indices.
    for (int i = 0; i < numNeighbors; i++) {
      resultNearests[i].data = (Double) tempSortDistances[i].data;
      resultNearests[i].label = trainingSet[(Integer) tempSortDistances[i].label];
    }

    System.out.println("The nearest of " + paraCurrent + " are " + Arrays.toString(resultNearests));
    return resultNearests;
  }

  /*
   * Recursive Merge Sort.
   * 
   * @param left. Left index.
   * 
   * @param right. Right index.
   * 
   * @param array. Origin array.
   */
  public void mergeSort(int left, int right, elementNode[] array) {
    // Exit.
    if (left >= right)
      return;
    int mid = (left + right) / 2;
    mergeSort(left, mid, array);
    mergeSort(mid + 1, right, array);
    // Merge.
    int tempLeft = left, tempRight = mid + 1;
    int tempIndex = left;
    elementNode[] tempArray = new elementNode[right - left + 1];
    for (int i = 0; i < right - left + 1; i++)
      tempArray[i] = array[left + i];
    while (tempLeft < mid + 1 && tempRight < right + 1) {
      if ((Double) tempArray[tempLeft - left].data < (Double) tempArray[tempRight - left].data)
        array[tempIndex++] = tempArray[tempLeft++ - left];
      else
        array[tempIndex++] = tempArray[tempRight++ - left];
    }
    // Merge rest elements.
    while (tempLeft < mid + 1)
      array[tempIndex++] = tempArray[tempLeft++ - left];
    while (tempRight < right + 1)
      array[tempIndex++] = tempArray[tempRight++ - left];
  }

  /*
   * Voting using the instances.
   * 
   * @param paraNeighbors. The indices of the neighbors.
   * 
   * @return The predicted label.
   */
  public int simpleVoting(int[] paraNeighbors) {
    int[] tempVotes = new int[dataset.numClasses()];
    for (int i = 0; i < paraNeighbors.length; i++)
      tempVotes[(int) dataset.instance(paraNeighbors[i]).classValue()]++;

    int tempMaximalVotingIndex = 0;
    int tempMaximalVoting = 0;
    for (int i = 0; i < dataset.numClasses(); i++) {
      if (tempVotes[i] > tempMaximalVoting) {
        tempMaximalVoting = tempVotes[i];
        tempMaximalVotingIndex = i;
      }
    }
    return tempMaximalVotingIndex;
  }

  /*
   * Vote with weight.
   * 
   * @param paraNeighbors. The indices of the neighbors.
   * 
   * @return The predicted label.
   */
  public int weightedVoting(elementNode[] paraNeighbors) {
    double[] tempVotes = new double[dataset.numClasses()];
    for (int i = 0; i < paraNeighbors.length; i++) {
      // double weight = gaussianWeight((Double) paraNeighbors[i].data, 5.0);
      double weight = simpleWeight((Double) paraNeighbors[i].data);
      tempVotes[(int) dataset.instance((Integer) paraNeighbors[i].label).classValue()] += weight;
    }

    int tempMaximalVotingIndex = 0;
    double tempMaximalVoting = 0;
    for (int i = 0; i < dataset.numClasses(); i++) {
      if (tempVotes[i] > tempMaximalVoting) {
        tempMaximalVoting = tempVotes[i];
        tempMaximalVotingIndex = i;
      }
    }
    return tempMaximalVotingIndex;
  }

  /*
   * Gaussian weight.
   */
  public double gaussianWeight(double paraDist, double sigma) {
    return Math.exp(-paraDist * paraDist / (2 * sigma * sigma));
  }

  /*
   * My simple weight.
   */
  public double simpleWeight(double paraDist) {
    return Math.exp(-Math.abs(10.0 * paraDist));
  }

  /*
   * Set the distance measure.
   * 
   * @param paraMeasure. The distance measure.
   */
  public void setDistanceMeasure(int paraMeasure) {
    distanceMeasure = paraMeasure;
  }

  /*
   * Set the number of neighbors.
   * 
   * @param paraNum. The number of neighbors.
   */
  public void setNumNeighbors(int paraNum) {
    numNeighbors = paraNum;
  }

  /*
   * Leave one out cross validation.
   */
  public void leaveOneOutTest() {
    double tempCorrect = 0;
    for (int i = 0; i < dataset.numInstances(); i++) {
      testingSet = new int[1];
      testingSet[0] = i;
      trainingSet = new int[dataset.numInstances() - 1];
      for (int j = 0; j < dataset.numInstances(); j++) {
        if (j == i)
          continue;
        else if (j < i)
          trainingSet[j] = j;
        else
          trainingSet[j - 1] = j;
      }
      this.predict();
      double tempClassValue = dataset.instance(testingSet[0]).classValue();
      if (this.predictions[0] == tempClassValue)
        tempCorrect++;
    }
    System.out
        .println("The accuracy of the classifier is: " + String.format("%4.3f", tempCorrect / dataset.numInstances()));
  }

  public static void main(String args[]) {
    KnnClassification tempClassifier = new KnnClassification("./sample-data/iris.arff");
    // tempClassifier.splitTrainingTesting(0.8);
    // tempClassifier.predict();
    // System.out.println("The accuracy of the classifier is: " +
    // String.format("%4.3f", tempClassifier.getAccuary()));
    tempClassifier.leaveOneOutTest();
  }
}