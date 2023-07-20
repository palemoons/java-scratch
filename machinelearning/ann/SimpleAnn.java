package machinelearning.ann;

public class SimpleAnn extends GeneralAnn {
  // The first dimension stands for layer, the second one stands for node.
  public double[][] layerNodeValues;
  public double[][] layerNodeErrors;
  /**
   * The weights of edges. The first dimension stands for the layer, the second
   * stands for the node index of the layer, and the third dimension stands for
   * the node index of the next layer.
   */
  public double[][][] edgeWeights;
  // The changes of the edge weights.
  public double[][][] edgeWeightsDelta;

  /**
   * The first constructor.
   * 
   * @param paraFilename
   *                          The arff filename.
   * @param paraLayerNumNodes
   *                          The number of nodes for each layer (may be
   *                          different).
   * @param paraLearningRate
   *                          Learning rate.
   * @param paraMobp
   *                          Momentum coefficient.
   */
  public SimpleAnn(String paraFilename, int[] paraLayerNumNodes, double paraLearningRate,
      double paraMobp) {
    super(paraFilename, paraLayerNumNodes, paraLearningRate, paraMobp);

    // Step 1. Across layer initialization.
    layerNodeValues = new double[numLayers][];
    layerNodeErrors = new double[numLayers][];
    edgeWeights = new double[numLayers - 1][][];
    edgeWeightsDelta = new double[numLayers - 1][][];

    // Step 2. Inner layer initialization.
    for (int l = 0; l < numLayers; l++) {
      layerNodeValues[l] = new double[layerNumNodes[l]];
      layerNodeErrors[l] = new double[layerNumNodes[l]];

      // One less layer because each edge crosses two layers.
      if (l + 1 == numLayers)
        break;

      // In layerNumNodes[l] + 1, the last one is reserved for the offset.
      edgeWeights[l] = new double[layerNumNodes[l] + 1][layerNumNodes[l + 1]];
      edgeWeightsDelta[l] = new double[layerNumNodes[l] + 1][layerNumNodes[l + 1]];
      for (int i = 0; i < layerNumNodes[l] + 1; i++) {
        for (int j = 0; j < layerNumNodes[l + 1]; j++) {
          // Initialize weights.
          edgeWeights[l][i][j] = random.nextDouble();
        }
      }
    }
  }

  /**
   * Forward prediction.
   * 
   * @param paraInput
   *                  The input data of one instance.
   * @return The data at the output end.
   */
  public double[] forward(double[] paraInput) {
    // Initialize the input layer.
    for (int i = 0; i < layerNodeValues[0].length; i++)
      layerNodeValues[0][i] = paraInput[i];

    // Calculate the node values of each layer.
    double z;
    for (int l = 1; l < numLayers; l++) {
      for (int j = 0; j < layerNodeValues[l].length; j++) {
        // Initialize according to the offset, which is always +1
        z = edgeWeights[l - 1][layerNodeValues[l - 1].length][j];
        // Weighted sum on all edges for this node.
        for (int i = 0; i < layerNodeValues[l - 1].length; i++) {
          z += edgeWeights[l - 1][i][j] * layerNodeValues[l - 1][i];
        }

        // Sigmoid activation.
        // This line should be changed for other activation functions.
        layerNodeValues[l][j] = 1 / (1 + Math.exp(-z));
      }
    }
    return layerNodeValues[numLayers - 1];
  }

  /**
   * Back propagation and change the edge weights.
   * 
   * @param paraTarget
   *                   For 3-class data, it is [0, 0, 1], [0, 1, 0] or [1, 0, 0].
   */
  public void backPropagation(double[] paraTarget) {
    // Step 1. Initialize the output layer error.
    int l = numLayers - 1;
    for (int j = 0; j < layerNodeErrors[l].length; j++) {
      layerNodeErrors[l][j] = layerNodeValues[l][j] * (1 - layerNodeValues[l][j])
          * (paraTarget[j] - layerNodeValues[l][j]);
    }

    // Step 2. Back-propagation even for l == 0
    while (l > 0) {
      l--;
      // Layer l, for each node.
      for (int j = 0; j < layerNumNodes[l]; j++) {
        double z = 0.0;
        // For each node of the next layer.
        for (int i = 0; i < layerNumNodes[l + 1]; i++) {
          if (l > 0) {
            z += layerNodeErrors[l + 1][i] * edgeWeights[l][j][i];
          }

          // Weight adjusting.
          edgeWeightsDelta[l][j][i] = mobp * edgeWeightsDelta[l][j][i]
              + learningRate * layerNodeErrors[l + 1][i] * layerNodeValues[l][j];
          edgeWeights[l][j][i] += edgeWeightsDelta[l][j][i];
          if (j == layerNumNodes[l] - 1) {
            // Weight adjusting for the offset part.
            edgeWeightsDelta[l][j + 1][i] = mobp * edgeWeightsDelta[l][j + 1][i]
                + learningRate * layerNodeErrors[l + 1][i];
            edgeWeights[l][j + 1][i] += edgeWeightsDelta[l][j + 1][i];
          }
        }

        // Record the error according to the differential of Sigmoid.
        // This line should be changed for other activation functions.
        layerNodeErrors[l][j] = layerNodeValues[l][j] * (1 - layerNodeValues[l][j]) * z;
      }
    }
  }

  public static void main(String[] args) {
    int[] tempLayerNodes = { 4, 8, 8, 3 };
    SimpleAnn tempNetwork = new SimpleAnn("./sample-data/iris.arff", tempLayerNodes, 0.01,
        0.6);

    for (int round = 0; round < 5000; round++)
      tempNetwork.train();

    double tempAccuracy = tempNetwork.test();
    System.out.println("The accuracy is: " + tempAccuracy);
  }
}
