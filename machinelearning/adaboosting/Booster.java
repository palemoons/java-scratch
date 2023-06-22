package machinelearning.adaboosting;

import java.io.FileReader;
import weka.core.*;

public class Booster {
  // Classifiers.
  SimpleClassifier[] classifiers;

  // Number of classifiers.
  int numClassifiers;

  // Whether or not stop after the training error is 0.
  boolean stopAfterConverge = false;

  // The weights of classifiers.
  double[] classifierWeights;

  // The training data.
  Instances trainingData;

  // The testing data.
  Instances testingData;

  /*
   * The first constructor.
   * The testing set is the same as the training set.
   * 
   * @param paraTrainingFilename. The data filename.
   */
  public Booster(String paraTrainingFilename) {
    // Step 1. Read training set.
    try {
      FileReader tempFileReader = new FileReader(paraTrainingFilename);
      trainingData = new Instances(tempFileReader);
      tempFileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read the file: " + paraTrainingFilename + "\r\n" + ee);
      System.exit(0);
    }

    // Step 2. Set the last attribute as the class index.
    trainingData.setClassIndex(trainingData.numAttributes() - 1);

    // Step 3. Set test data.
    testingData = trainingData;

    stopAfterConverge = true;
    System.out.println("---------Data---------\r\n" + trainingData);
  }

  /*
   * Set the number of base classifier, and allocate space for them.
   * 
   * @param paraNumBaseClassifiers. The number of base classifier.
   */
  public void setNumBaseClassifiers(int paraNumBaseClassifiers) {
    numClassifiers = paraNumBaseClassifiers;

    // Step 1. Allocate space (only reference) for classifiers.
    classifiers = new SimpleClassifier[numClassifiers];

    // Step 2. Initialize classifier weights.
    classifierWeights = new double[numClassifiers];
  }

  /*
   * Train the booster.
   * 
   * @see algorithm.StumpClassifier#train().
   */
  public void train() {
    // Step 1. Initialize.
    WeightedInstances tempWeightedInstances = null;
    double tempError;
    numClassifiers = 0;

    // Step 2. Build other classifiers.
    for (int i = 0; i < classifierWeights.length; i++) {
      // Step 2.1. Key code: Construct or adjust the weightedInstances.
      if (i == 0)
        tempWeightedInstances = new WeightedInstances(trainingData);
      else
        // Adjust the weights of the data.
        tempWeightedInstances.adjustWeights(classifiers[i - 1].computeCorrectnessArray(), classifierWeights[i - 1]);

      // Step 2.2. Train the next classifier.
      classifiers[i] = new StumpClassifier(tempWeightedInstances);
      classifiers[i].train();

      tempError = classifiers[i].computeWeightedError();

      // Key code: Set the classifier weight.
      classifierWeights[i] = 0.5 * Math.log(1 / tempError - 1);
      if (classifierWeights[i] < 1e-6)
        classifierWeights[i] = 0;

      System.out.println(
          "Classifier #" + i + ", weighted error: = " + tempError + ", weight = " + classifierWeights[i] + "\r\n");

      numClassifiers++;

      if (stopAfterConverge) {
        double tempTrainingAccuracy = computeTrainingAccuracy();
        System.out.println("The accuracy of the booster is: " + tempTrainingAccuracy + "\r\n");
        if (tempTrainingAccuracy > 0.999999) {
          System.out.println("Stop at the round: " + i + " due to converge.\r\n");
          break;
        }
      }
    }
  }

  /*
   * Classify an instance.
   * 
   * @param paraInstance. The given instance.
   * 
   * @return The predicted label.
   */
  public int classify(Instance paraInstance) {
    double[] tempLabelsCountArray = new double[trainingData.classAttribute().numValues()];
    for (int i = 0; i < numClassifiers; i++) {
      int tempLabel = classifiers[i].classify(paraInstance);
      tempLabelsCountArray[tempLabel] += classifierWeights[i];
    }

    int resultLabel = -1;
    double tempMax = -1;
    for (int i = 0; i < tempLabelsCountArray.length; i++) {
      if (tempMax < tempLabelsCountArray[i]) {
        tempMax = tempLabelsCountArray[i];
        resultLabel = i;
      }
    }
    return resultLabel;
  }

  /*
   * Test the booster on the training data.
   * 
   * @return The classification accuracy.
   */
  public double test() {
    System.out.println("Testing on " + testingData.numAttributes() + " instances\r\n");
    return test(testingData);
  }

  /*
   * Test the booster.
   * 
   * @param paraInstances. The testing set.
   * 
   * @return The classification accuracy.
   */
  public double test(Instances paraInstances) {
    double tempCorrect = 0;
    paraInstances.setClassIndex(paraInstances.numAttributes() - 1);
    for (int i = 0; i < paraInstances.numInstances(); i++) {
      Instance tempInstance = paraInstances.instance(i);
      if (classify(tempInstance) == (int) tempInstance.classValue())
        tempCorrect++;
    }

    double resultAccuracy = tempCorrect / paraInstances.numInstances();
    System.out.println("The accuracy is: " + resultAccuracy);

    return resultAccuracy;
  }

  /*
   * Compute the training accuracy of the booster. It is not weighted.
   * 
   * @return The training accuracy.
   */
  public double computeTrainingAccuracy() {
    double tempCorrect = 0;
    for (int i = 0; i < trainingData.numInstances(); i++) {
      if (classify(trainingData.instance(i)) == (int) trainingData.instance(i).classValue())
        tempCorrect++;
    }
    double tempAccuracy = tempCorrect / trainingData.numInstances();
    return tempAccuracy;
  }

  /*
   * For integration test.
   */
  public static void main(String args[]) {
    System.out.println("Starting AdaBoosting...");
    Booster tempBooster = new Booster("./sample-data/iris.arff");

    tempBooster.setNumBaseClassifiers(200);
    tempBooster.train();

    System.out.println("The training accuracy is: " + tempBooster.computeTrainingAccuracy());
    tempBooster.test();
  }
}
