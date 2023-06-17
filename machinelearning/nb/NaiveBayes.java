package machinelearning.nb;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

import weka.core.*;

/* 
 * The Naive Bayes algorithm.
 */
public class NaiveBayes {
  // An inner class to store parameters.
  private class GaussianParameters {
    double mu;
    double sigma;

    public GaussianParameters(double paraMu, double paraSigma) {
      mu = paraMu;
      sigma = paraSigma;
    }

    public String toString() {
      return "(" + mu + ", " + sigma + ")";
    }
  }

  // The data.
  Instances dataset;

  // The number of classes. For binary classification it is 2.
  int numClasses;

  // The number of instances.
  int numInstances;

  // The number of conditional attributes.
  int numConditions;

  // The prediction, including queried and predicted labels.
  int[] predicts;

  // Class distribution.
  double[] classDistribution;

  // Class distribution with Laplacian smooth.
  double[] classDistributionLaplacian;

  // To calculate the conditional probabilities for all classes over all
  // attributes on all values.
  double[][][] conditionalCounts;

  // The conditional probabilities with Laplacian smooth.
  double[][][] conditionalProbabilitiesLaplacian;

  // The Guassian parameters.
  GaussianParameters[][] gaussianParameters;

  // Data type.
  int dataType;

  // Nominal.
  public static final int NOMINAL = 0;

  // Numerical.
  public static final int NUMERICAL = 1;

  /*
   * The constructor.
   * 
   * @param paraFilename. The given file.
   */
  public NaiveBayes(String paraFilename) {
    dataset = null;
    try {
      FileReader fileReader = new FileReader(paraFilename);
      dataset = new Instances(fileReader);
      fileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read the file: " + paraFilename + "\r\n");
      System.exit(0);
    }

    dataset.setClassIndex(dataset.numAttributes() - 1);
    numConditions = dataset.numAttributes() - 1;
    numInstances = dataset.numInstances();
    numClasses = dataset.attribute(numConditions).numValues();
  }

  /*
   * Teh constructor.
   * 
   * @param paraInstances. The given Instances.
   */
  public NaiveBayes(Instances paraInstances) {
    dataset = paraInstances;

    dataset.setClassIndex(dataset.numAttributes() - 1);
    numConditions = dataset.numAttributes() - 1;
    numInstances = dataset.numInstances();
    numClasses = dataset.attribute(numConditions).numValues();
  }

  /*
   * Set the data type.
   */
  public void setDataType(int paraDataType) {
    dataType = paraDataType;
  }

  /*
   * Calculate the class distribution with Laplacian smooth.
   */
  public void calculateClassDistribution() {
    classDistribution = new double[numClasses];
    classDistributionLaplacian = new double[numClasses];

    double[] tempCounts = new double[numClasses];
    for (int i = 0; i < numInstances; i++) {
      int tempClassValue = (int) dataset.instance(i).classValue();
      tempCounts[tempClassValue]++;
    }
    for (int i = 0; i < numClasses; i++) {
      classDistribution[i] = tempCounts[i] / numInstances;
      classDistributionLaplacian[i] = (tempCounts[i] + 1) / (numInstances + numClasses);
    }

    System.out.println("Class distribution: " + Arrays.toString(classDistribution));
    System.out.println("Class distribution Laplacian: " + Arrays.toString(classDistributionLaplacian));
  }

  // Calculate the conditional probabilities with Laplacian smooth. ONLY scan the
  // dataset once.
  public void calculateConditionalProbabilities() {
    conditionalCounts = new double[numClasses][numConditions][];
    conditionalProbabilitiesLaplacian = new double[numClasses][numConditions][];

    // Allocate space.
    for (int i = 0; i < numClasses; i++) {
      for (int j = 0; j < numConditions; j++) {
        int tempNumValues = (int) dataset.attribute(j).numValues();
        conditionalCounts[i][j] = new double[tempNumValues];
        conditionalProbabilitiesLaplacian[i][j] = new double[tempNumValues];
      }
    }

    // Count the numbers.
    int[] tempClassCounts = new int[numClasses];
    for (int i = 0; i < numInstances; i++) {
      int tempClass = (int) dataset.instance(i).classValue();
      tempClassCounts[tempClass]++;
      for (int j = 0; j < numConditions; j++) {
        int tempValue = (int) dataset.instance(i).value(j);
        conditionalCounts[tempClass][j][tempValue]++;
      }
    }

    // Now for the real probability with Laplacian.
    for (int i = 0; i < numClasses; i++) {
      for (int j = 0; j < numConditions; j++) {
        int tempNumValues = (int) dataset.attribute(j).numValues();
        for (int k = 0; k < tempNumValues; k++) {
          conditionalProbabilitiesLaplacian[i][j][k] = (conditionalCounts[i][j][k] + 1)
              / (tempClassCounts[i] + tempNumValues);
        }
      }
    }

    System.out.println("Conditional probabilities: " + Arrays.deepToString(conditionalCounts));
  }

  /*
   * Calculate the conditional probabilities with Laplacian smooth.
   */
  public void calculateGaussianParameters() {
    gaussianParameters = new GaussianParameters[numClasses][numConditions];
    double[] tempValuesArray = new double[numInstances];
    int tempNumValues = 0;
    double tempSum = 0;

    for (int i = 0; i < numClasses; i++) {
      for (int j = 0; j < numConditions; j++) {
        tempSum = 0;

        // Obtain values for the class.
        tempNumValues = 0;
        for (int k = 0; k < numInstances; k++) {
          if ((int) dataset.instance(k).classValue() != i)
            continue;

          tempValuesArray[tempNumValues] = dataset.instance(k).value(j);
          tempSum += tempValuesArray[tempNumValues];
          tempNumValues++;
        }

        // Obtain parameters.
        double tempMu = tempSum / tempNumValues;
        double tempSigma = 0;
        for (int k = 0; k < tempNumValues; k++)
          tempSigma += (tempValuesArray[k] - tempMu) * (tempValuesArray[k] - tempMu);

        tempSigma /= tempNumValues;
        tempSigma = Math.sqrt(tempSigma);

        gaussianParameters[i][j] = new GaussianParameters(tempMu, tempSigma);
      }
    }

    System.out.println(Arrays.deepToString(gaussianParameters));
  }

  /*
   * Classify all instances, the results are stored in predicts[].
   */
  public void classify() {
    predicts = new int[numInstances];
    for (int i = 0; i < numInstances; i++)
      predicts[i] = classify(dataset.instance(i));
  }

  /*
   * Classify an instance.
   */
  public int classify(Instance paraInstance) {
    if (dataType == NOMINAL)
      return classifyNominal(paraInstance);
    else if (dataType == NUMERICAL)
      return classifyNumerical(paraInstance);

    return -1;
  }

  /*
   * Classify an instances with nominal data.
   */
  public int classifyNominal(Instance paraInstance) {
    // Find the biggest one.
    double tempBiggest = -10000;
    int resultBestIndex = 0;
    for (int i = 0; i < numClasses; i++) {
      double tempPseudoProbability = Math.log(classDistributionLaplacian[i]);
      for (int j = 0; j < numConditions; j++) {
        int tempAttributeValue = (int) paraInstance.value(j);
        tempPseudoProbability += Math.log(conditionalProbabilitiesLaplacian[i][j][tempAttributeValue]);
      }

      if (tempBiggest < tempPseudoProbability) {
        tempBiggest = tempPseudoProbability;
        resultBestIndex = i;
      }
    }

    return resultBestIndex;
  }

  /*
   * Classify an instances with numerical data.
   */
  public int classifyNumerical(Instance paraInstance) {
    // Find the biggest one.
    double tempBiggest = -10000;
    int resultBestIndex = 0;

    for (int i = 0; i < numClasses; i++) {
      double tempPseudoProbability = Math.log(classDistributionLaplacian[i]);
      for (int j = 0; j < numConditions; j++) {
        double tempAttributeValue = (int) paraInstance.value(j);
        double tempSigma = gaussianParameters[i][j].sigma;
        double tempMu = gaussianParameters[i][j].mu;

        tempPseudoProbability += -Math.log(tempSigma)
            - (tempAttributeValue - tempMu) * (tempAttributeValue - tempMu) / (2 * tempSigma * tempSigma);
      }

      if (tempBiggest < tempPseudoProbability) {
        tempBiggest = tempPseudoProbability;
        resultBestIndex = i;
      }
    }

    return resultBestIndex;
  }

  /*
   * Compute accuarcy.
   */
  public double computeAccuracy() {
    double tempCorrect = 0;
    for (int i = 0; i < numInstances; i++)
      if (predicts[i] == (int) dataset.instance(i).classValue())
        tempCorrect++;

    return tempCorrect / numInstances;
  }

  /*
   * Test nominal data.
   */
  public static void testNominal() {
    System.out.println("Hello, Naive Bayes. I only want to test the nominal data.");
    String tempFilename = "./sample-data/mushroom.arff";

    NaiveBayes tempLearner = new NaiveBayes(tempFilename);
    tempLearner.setDataType(NOMINAL);
    tempLearner.calculateClassDistribution();
    tempLearner.calculateConditionalProbabilities();
    tempLearner.classify();

    System.out.println("The accuracy is: " + tempLearner.computeAccuracy());
  }

  /*
   * Test numerical data.
   */
  public static void testNumerical() {
    System.out.println("Hello, Naive Bayes. I only want to test the numerical data.");
    String tempFilename = "./sample-data/iris-imbalance.arff";

    NaiveBayes tempLearner = new NaiveBayes(tempFilename);
    tempLearner.setDataType(NUMERICAL);
    tempLearner.calculateClassDistribution();
    tempLearner.calculateGaussianParameters();
    tempLearner.classify();

    System.out.println("The accuracy is: " + tempLearner.computeAccuracy());
  }

  /*
   * Get a random indices for data randomization.
   * 
   * @param paraLength. The length of the sequence.
   * 
   * @return An array of indices, e.g. {4, 3, 1, 5, 0, 2} with length 6.
   */
  public static int[] getRandomIndices(int paraLength) {
    Random random = new Random();
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
   * @param paraTrainingFraction. The fraction of the training set.
   */
  public static Instances[] splitTrainingTesting(Instances paraDataset, double paraTrainingFraction) {
    int tempSize = paraDataset.numInstances();
    int[] tempIndices = getRandomIndices(tempSize);
    int tempTrainingSize = (int) (tempSize * paraTrainingFraction);

    // Empty datasets.
    Instances tempTrainingSet = new Instances(paraDataset);
    tempTrainingSet.delete();
    Instances tempTestingSet = new Instances(tempTrainingSet);

    for (int i = 0; i < tempTrainingSize; i++)
      tempTrainingSet.add(paraDataset.instance(tempIndices[i]));

    for (int i = 0; i < tempSize - tempTrainingSize; i++)
      tempTestingSet.add(paraDataset.instance(tempIndices[tempTrainingSize]));

    tempTrainingSet.setClassIndex(tempTrainingSet.numAttributes() - 1);
    tempTestingSet.setClassIndex(tempTestingSet.numAttributes() - 1);
    Instances[] resultInstancesArray = new Instances[2];
    resultInstancesArray[0] = tempTrainingSet;
    resultInstancesArray[1] = tempTestingSet;

    return resultInstancesArray;
  }

  /*
   * Classify all instances, the results are stored in predicts[].
   */
  public double classify(Instances paraTestingSet) {
    double tempCorrect = 0;
    int[] tempPredicts = new int[paraTestingSet.numInstances()];
    for (int i = 0; i < tempPredicts.length; i++) {
      tempPredicts[i] = classify(paraTestingSet.instance(i));
      if (tempPredicts[i] == (int) paraTestingSet.instance(i).classValue())
        tempCorrect++;
    }

    System.out.println("" + tempCorrect + " correct over " + tempPredicts.length + " instances.");
    double resultAccuracy = tempCorrect / tempPredicts.length;
    return resultAccuracy;
  }

  /*
   * Test nominal data.
   */
  public static void testNominal(double paraTrainingFraction) {
    System.out.println("Hello, Naive Bayes. I only want to test the nominal data.");
    String tempFilename = "./sample-data/mushroom.arff";

    Instances tempDataset = null;
    try {
      FileReader fileReader = new FileReader(tempFilename);
      tempDataset = new Instances(fileReader);
      fileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read the file: " + tempFilename + "\r\n" + ee);
      System.exit(0);
    }

    Instances[] tempDatasets = splitTrainingTesting(tempDataset, paraTrainingFraction);
    NaiveBayes tempLearner = new NaiveBayes(tempDatasets[0]);
    tempLearner.setDataType(NOMINAL);
    tempLearner.calculateClassDistribution();
    tempLearner.calculateConditionalProbabilities();

    double tempAccuracy = tempLearner.classify(tempDatasets[1]);

    System.out.println("The accuracy is: " + tempAccuracy);
  }

  /*
   * Test this class.
   */
  public static void main(String[] args) {
    // testNominal();
    testNumerical();
    // testNominal(0.8);
  }
}
