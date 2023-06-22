package machinelearning.adaboosting;

import weka.core.Instance;
import java.io.FileReader;
import java.util.*;

public class StumpClassifier extends SimpleClassifier {
  // The best cut for the current attribute on weightedInstances.
  double bestCut;
  // The class label attribute value less than bestCut.
  int leftLeafLabel;
  // The class label for attribute value no less than bestCut.
  int rightLeafLabel;

  /*
   * The only constructor.
   * 
   * @param paraWeightedInstances. The given instances.
   */
  public StumpClassifier(WeightedInstances paraWeighedInstances) {
    super(paraWeighedInstances);
  }

  /*
   * Train the classifier.
   */
  public void train() {
    // Step 1. Randomly choose an attribute.
    selectedAttribute = random.nextInt(numConditions);

    // Step 2. Find all attribute values and sort.
    double[] tempValuesArray = new double[numInstances];
    for (int i = 0; i < tempValuesArray.length; i++)
      tempValuesArray[i] = weightedInstances.instance(i).value(selectedAttribute);
    Arrays.sort(tempValuesArray);

    // Step 3. Initialize. Classify all instances as the same with the original cut.
    int tempNumLabels = numClasses;
    double[] tempLabelCountArray = new double[tempNumLabels];
    int tempCurrentLabel;

    // Step 3.1. Scan all labels to obtain their counts.
    for (int i = 0; i < numInstances; i++) {
      // The label of the instance.
      tempCurrentLabel = (int) weightedInstances.instance(i).classValue();
      tempLabelCountArray[tempCurrentLabel] += weightedInstances.getWeight(i);
    }

    // Step 3.2. Find the label with the maximal count.
    double tempMaxCorrect = 0;
    int tempBestLabel = -1;
    for (int i = 0; i < tempLabelCountArray.length; i++) {
      if (tempMaxCorrect < tempLabelCountArray[i]) {
        tempMaxCorrect = tempLabelCountArray[i];
        tempBestLabel = i;
      }
    }

    // Step 3.3. The cut is a little bit smaller than the minimal value.
    bestCut = tempValuesArray[0] - 0.1;
    leftLeafLabel = tempBestLabel;
    rightLeafLabel = tempBestLabel;

    // Step 4. Check candidate cuts one by one.
    double tempCut;
    double[][] tempLabelCountMatrix = new double[2][tempNumLabels];

    for (int i = 0; i < tempValuesArray.length - 1; i++) {
      // Step 4.1. Some attribute values are identical, ignore them.
      if (tempValuesArray[i] == tempValuesArray[i + 1])
        continue;
      tempCut = (tempValuesArray[i] + tempValuesArray[i + 1]) / 2;

      // Step 4.2. Initialize.
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < tempNumLabels; k++)
          tempLabelCountMatrix[j][k] = 0;
      }

      for (int j = 0; j < numInstances; j++) {
        // The label of the j th instance.
        tempCurrentLabel = (int) weightedInstances.instance(j).classValue();
        // Allocate instance to left/right leaf.
        if (weightedInstances.instance(j).value(selectedAttribute) < tempCut)
          tempLabelCountMatrix[0][tempCurrentLabel] += weightedInstances.getWeight(j);
        else
          tempLabelCountMatrix[1][tempCurrentLabel] += weightedInstances.getWeight(j);
      }

      // Step 4.3. Update left leaf.
      double tempLeftMaxCorrect = 0;
      int tempLeftBestLabel = 0;
      for (int j = 0; j < tempLabelCountMatrix[0].length; j++) {
        if (tempLeftMaxCorrect < tempLabelCountMatrix[0][j]) {
          tempLeftMaxCorrect = tempLabelCountMatrix[0][j];
          tempLeftBestLabel = j;
        }
      }

      // Step 4.4. Update right leaf.
      double tempRightMaxCorrect = 0;
      int tempRightBestLabel = 0;
      for (int j = 0; j < tempLabelCountMatrix[1].length; j++) {
        if (tempRightMaxCorrect < tempLabelCountMatrix[1][j]) {
          tempRightMaxCorrect = tempLabelCountMatrix[1][j];
          tempRightBestLabel = j;
        }
      }

      // Step 4.5. Compare with the current best.
      if (tempMaxCorrect < tempLeftMaxCorrect + tempRightMaxCorrect) {
        tempMaxCorrect = tempLeftMaxCorrect + tempRightMaxCorrect;
        bestCut = tempCut;
        leftLeafLabel = tempLeftBestLabel;
        rightLeafLabel = tempRightBestLabel;
      }
    }

    System.out.println("Attribute = " + selectedAttribute + ", cut = " + bestCut + ", leftLeafLabel = " + leftLeafLabel
        + ", rightLeafLabel = " + rightLeafLabel);
  }

  /*
   * Classify an instance.
   * 
   * @param paraInstance. The given instance.
   * 
   * @return Predicted label.
   */
  public int classify(Instance paraInstance) {
    int resultLabel = -1;
    if (paraInstance.value(selectedAttribute) < bestCut)
      resultLabel = leftLeafLabel;
    else
      resultLabel = rightLeafLabel;
    return resultLabel;
  }

  /*
   * For display.
   */
  public String toString() {
    String resultString = "I am stump classifier.\r\nI choose attribute #" + selectedAttribute + " with cut value "
        + bestCut + ".\r\nThe left and right left labels are " + leftLeafLabel + " and " + rightLeafLabel
        + ", respectively.\r\nMy weighted error is: " + computeWeightedError() + ".\r\nMy weighted accuracy is: "
        + computeTrainingAccuracy() + ".";
    return resultString;
  }

  /*
   * For unit test.
   */
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

    StumpClassifier tempClassifier = new StumpClassifier(tempWeightedInstances);
    tempClassifier.train();
    System.out.println(tempClassifier);

    System.out.println(Arrays.toString(tempClassifier.computeCorrectnessArray()));
  }
}
