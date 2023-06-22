package machinelearning.adaboosting;

import java.util.Random;
import weka.core.Instance;

public abstract class SimpleClassifier {
  // The index of the current attribute.
  int selectedAttribute;
  // Weighted data.
  WeightedInstances weightedInstances;
  // The accuracy on the training set.
  double trainingAccuracy;
  // The number of classes.
  int numClasses;
  // The number of instances.
  int numInstances;
  // The number of conditional attributes.
  int numConditions;
  // For random number generation.
  Random random = new Random();

  /*
   * The first constructor.
   * 
   * @param paraWeightedInstances. The given instances.
   */
  public SimpleClassifier(WeightedInstances paraWeightedInstances) {
    weightedInstances = paraWeightedInstances;

    numConditions = weightedInstances.numAttributes() - 1;
    numInstances = weightedInstances.numInstances();
    numClasses = weightedInstances.classAttribute().numValues();
  }

  /*
   * Train the classifier.
   */
  public abstract void train();

  /*
   * Classify an instance.
   * 
   * @param paraInstance. The given instance.
   * 
   * @return Predicted label.
   */
  public abstract int classify(Instance paraInstance);

  /*
   * Which instances in the training set are correctly classified.
   * 
   * @return The correctness array.
   */
  public boolean[] computeCorrectnessArray() {
    boolean[] resultCorrectArray = new boolean[weightedInstances.numInstances()];
    for (int i = 0; i < resultCorrectArray.length; i++) {
      Instance tempInstance = weightedInstances.instance(i);
      if ((int) (tempInstance.classValue()) == classify(tempInstance))
        resultCorrectArray[i] = true;
    }
    return resultCorrectArray;
  }

  /*
   * Compute the accuracy on the training set.
   * 
   * @return The training accuracy.
   */
  public double computeTrainingAccuracy() {
    double tempCorrect = 0;
    boolean[] tempCorrectnessArray = computeCorrectnessArray();
    for (int i = 0; i < tempCorrectnessArray.length; i++) {
      if (tempCorrectnessArray[i])
        tempCorrect++;
    }
    double resultAccuracy = tempCorrect / tempCorrectnessArray.length;
    return resultAccuracy;
  }

  /*
   * Compute the weighted error on the training set.
   * 
   * @return The weighted error.
   */
  public double computeWeightedError() {
    double resultError = 0;
    boolean[] tempCorrectnessArray = computeCorrectnessArray();
    for (int i = 0; i < tempCorrectnessArray.length; i++) {
      if (!tempCorrectnessArray[i])
        resultError += weightedInstances.getWeight(i);
    }
    if (resultError < 1e-6)
      resultError = 1e-6;
    return resultError;
  }
}
