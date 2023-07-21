package machinelearning.ann;

import java.util.Arrays;
import java.util.Random;

public class AnnLayer {
  /**
   * The number of input.
   */
  int numInput;
  /**
   * The number of output.
   */
  int numOutput;
  /**
   * The learning rate.
   */
  double learningRate;
  /**
   * The mobp.
   */
  double mobp;
  /**
   * The weight matrix.
   */
  double[][] weights;
  /**
   * The delta weight matrix.
   */
  double[][] deltaWeights;
  /**
   * Error on nodes.
   */
  double[] errors;
  /**
   * The inputs.
   */
  double[] input;
  /**
   * The outputs.
   */
  double[] output;
  /**
   * The output after activate.
   */
  double[] activatedOutput;
  /**
   * The inputs.
   */
  Activator activator;
  /**
   * The inputs.
   */
  Random random = new Random();

  /**
   * The first constructor
   * 
   * @param paraNumInput
   *                         The number of input
   * @param paraNumOutput
   *                         The number of output
   * @param paraActivator
   *                         The activator
   * @param paraLearningRate
   *                         The learning rate
   * @param paraMobp
   *                         The mobp
   */
  public AnnLayer(int paraNumInput, int paraNumOutput, char paraActivator,
      double paraLearningRate, double paraMobp) {
    numInput = paraNumInput;
    numOutput = paraNumOutput;
    learningRate = paraLearningRate;
    mobp = paraMobp;

    weights = new double[numInput + 1][numOutput];
    deltaWeights = new double[numInput + 1][numOutput];
    for (int i = 0; i < numInput + 1; i++)
      for (int j = 0; j < numOutput; j++)
        weights[i][j] = random.nextDouble();

    errors = new double[numInput];
    input = new double[numInput];
    output = new double[numOutput];
    activatedOutput = new double[numOutput];

    activator = new Activator(paraActivator);
  }

  /**
   * Set parameters for the Activator
   * 
   * @param paraAlpha
   *                  Alpha. Only valid for certain types.
   * @param paraBeta
   *                  The beta
   * @param paraGamma
   *                  The gamma
   */
  public void setParameters(double paraAlpha, double paraBeta, double paraGamma) {
    activator.setAlpha(paraAlpha);
    activator.setBeta(paraBeta);
    activator.setGamma(paraGamma);
  }

  /**
   * Forward prediction
   * 
   * @param paraInput
   *                  The input data of one instance
   * @return The value at the output end
   */
  public double[] forward(double[] paraInput) {
    // Copy data.
    for (int i = 0; i < numInput; i++)
      input[i] = paraInput[i];
    for (int i = 0; i < numOutput; i++) {
      output[i] = weights[numInput][i];
      for (int j = 0; j < numInput; j++)
        output[i] += input[j] * weights[j][i];
      activatedOutput[i] = activator.activate(output[i]);
    }
    return activatedOutput;
  }

  /**
   * Back propogation and update edge weights
   * 
   * @param paraErrors
   */
  public double[] backPropagation(double[] paraErrors) {
    // Step 1. Adjust the errors.
    for (int i = 0; i < paraErrors.length; i++) {
      paraErrors[i] = activator.derive(output[i], activatedOutput[i]) * paraErrors[i];
    }

    // Step 2. Compute current errors.
    for (int i = 0; i < numInput; i++) {
      errors[i] = 0;
      for (int j = 0; j < numOutput; j++) {
        errors[i] += paraErrors[j] * weights[i][j];
        deltaWeights[i][j] = mobp * deltaWeights[i][j]
            + learningRate * paraErrors[j] * input[i];
        weights[i][j] += deltaWeights[i][j];
      }
    }

    for (int i = 0; i < numOutput; i++) {
      deltaWeights[numInput][i] = mobp * deltaWeights[numInput][i] + learningRate * paraErrors[i];
      weights[numInput][i] += deltaWeights[numInput][i];
    }

    return errors;
  }

  /**
   * Get the last layer's error
   */
  public double[] getLastLayerErrors(double[] paraTarget) {
    double[] resultErrors = new double[numOutput];
    for (int i = 0; i < numOutput; i++)
      resultErrors[i] = (paraTarget[i] - activatedOutput[i]);

    return resultErrors;
  }

  /**
   * Reclaim toString method
   */
  public String toString() {
    String resultString = "";
    resultString += "Activator: " + activator;
    resultString += "\r\n weights = " + Arrays.deepToString(weights);
    return resultString;
  }

  /**
	 * Unit test.
	 */
	public static void unitTest() {
		AnnLayer tempLayer = new AnnLayer(2, 3, 's', 0.01, 0.1);
		double[] tempInput = { 1, 4 };

		System.out.println(tempLayer);

		double[] tempOutput = tempLayer.forward(tempInput);
		System.out.println("Forward, the output is: " + Arrays.toString(tempOutput));

		double[] tempError = tempLayer.backPropagation(tempOutput);
		System.out.println("Back propagation, the error is: " + Arrays.toString(tempError));
	}

	/**
	 * Test the algorithm.
	 */
	public static void main(String[] args) {
		unitTest();
	}
}
