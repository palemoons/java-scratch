package machinelearning.adaboosting;

import java.io.FileReader;
import java.util.Arrays;

import weka.core.Instances;

/*
 * Weighted instances.
 */
public class WeightedInstances extends Instances {
  // Just the requirement of some classes.
  private static final long serialVersionUID = 11087456L;
  // Weights.
  private double[] weights;

  /*
   * The first constructor.
   * 
   * @param paraFileReader. The given reader to read data from file.
   */
  public WeightedInstances(FileReader paraFileReader) throws Exception {
    super(paraFileReader);
    setClassIndex(numAttributes() - 1);

    // Initialize weights.
    weights = new double[numInstances()];
    double tempAverage = 1.0 / numInstances();
    for (int i = 0; i < weights.length; i++)
      weights[i] = tempAverage;
    System.out.println("Instances weights are: " + Arrays.toString(weights));
  }

  /*
   * The second constructor.
   * 
   * @param paraInstances. The given instances.
   */
  public WeightedInstances(Instances paraInstances) {
    super(paraInstances);
    setClassIndex(numAttributes() - 1);

    // Initialize weights.
    weights = new double[numInstances()];
    double tempAverage = 1.0 / numInstances();
    for (int i = 0; i < weights.length; i++)
      weights[i] = tempAverage;
    System.out.println("Instances weights are: " + Arrays.toString(weights));
  }

  /*
   * Getter.
   * 
   * @param paraIndex. The given index.
   * 
   * @return The weight of the given index.
   */
  public double getWeight(int paraIndex) {
    return weights[paraIndex];
  }

  /*
   * Adjust the weights.
   * 
   * @param paraCorrectArray. Indicate which instances have been correctly
   * classified.
   * 
   * @param paraAlpha. The weight of the last classifier.
   */
  public void adjustWeights(boolean[] paraCorrectArray, double paraAlpha) {
    // Step 1. Calculate alpha.
    double tempIncrease = Math.exp(paraAlpha);

    // Step 2. Adjust.
    double tempWeightsSum = 0;
    for (int i = 0; i < weights.length; i++) {
      if (paraCorrectArray[i])
        weights[i] /= tempIncrease;
      else
        weights[i] *= tempIncrease;
      tempWeightsSum += weights[i];
    }

    // Step 3. Normalize.
    for (int i = 0; i < weights.length; i++)
      weights[i] /= tempWeightsSum;
    System.out.println("After adjusting, instances weights are: " + Arrays.toString(weights));
  }

  /*
   * Test the method.
   */
  public void adjustWeightsTest() {
    boolean[] tempCorrectArray = new boolean[numInstances()];
    for (int i = 0; i < tempCorrectArray.length / 2; i++)
      tempCorrectArray[i] = true;

    double tempWeigthedError = 0.3;
    adjustWeights(tempCorrectArray, tempWeigthedError);

    System.out.println("After adjusting");
    System.out.println(toString());
  }

  /*
   * For display.
   */
  public String toString() {
    String resultString = "I am a weighted Instances object.\r\nI have " + numInstances() + " instances and "
        + (numAttributes() - 1) + " conditional attributes.\r\nMy weights are: " + Arrays.toString(weights)
        + "\r\nMy data are: \r\n" + super.toString();
    return resultString;
  }

  public static void main(String args[]) {
    WeightedInstances tempWeightedInstances = null;
    String tempFilename = "./sample-data/iris.arff";
    try {
      FileReader tempFileReader = new FileReader(tempFilename);
      tempWeightedInstances = new WeightedInstances(tempFileReader);
      tempFileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read the file: " + tempFilename + "\r\n" + ee);
      System.exit(0);
    }
    System.out.println(tempWeightedInstances.toString());
    tempWeightedInstances.adjustWeightsTest();
  }
}