package machinelearning.cnn;

import java.util.Arrays;
import machinelearning.cnn.Dataset.Instance;
import machinelearning.cnn.MathUtils.Operator;

/**
 * CNN.
 */
public class FullCnn {
  /**
   * The value changes.
   */
  private static double ALPHA = 0.85;

  /**
   * A constant.
   */
  public static double LAMBDA = 0;

  /**
   * Manage layers.
   */
  private static LayerBuilder layerBuilder;

  /**
   * Train using a number of instances simultaneously.
   */
  private int batchSize;

  /**
   * Divide the batch size with the given value.
   */
  private Operator divideBatchSize;

  /**
   * Multiply alpha with the given value.
   */
  private Operator multiplyAlpha;

  /**
   * Multiply lambda and alpha with the given value.
   */
  private Operator multiplyLambda;

  /**
   * The first constructor.
   */
  public FullCnn(LayerBuilder paraLayerBuilder, int paraBatchSize) {
    layerBuilder = paraLayerBuilder;
    batchSize = paraBatchSize;
    setup();
    initOperators();
  }

  /**
   * Initialize operators using temporary classes.
   */
  private void initOperators() {
    divideBatchSize = new Operator() {
      private static final long serialVersionUID = 7424011281732651055L;

      @Override
      public double process(double value) {
        return value / batchSize;
      }
    };

    multiplyAlpha = new Operator() {
      private static final long serialVersionUID = 5761368499808006552L;

      @Override
      public double process(double value) {
        return value * ALPHA;
      }
    };

    multiplyLambda = new Operator() {
      private static final long serialVersionUID = 4499087728362870577L;

      @Override
      public double process(double value) {
        return value * (1 - LAMBDA * ALPHA);
      }
    };
  }

  /**
   * Setup according to the layer builder.
   */
  public void setup() {
    CnnLayer tempInputLayer = layerBuilder.getLayer(0);
    tempInputLayer.initOutMaps(batchSize);

    for (int i = 1; i < layerBuilder.getNumLayers(); i++) {
      CnnLayer tempLayer = layerBuilder.getLayer(i);
      CnnLayer tempFrontLayer = layerBuilder.getLayer(i - 1);
      int tempFrontMapNum = tempFrontLayer.getOutMapNum();
      switch (tempLayer.getType()) {
        case INPUT:
          // Should not be input. Maybe an error should be thrown out.
          break;
        case CONVOLUTION:
          tempLayer.setMapSize(
              tempFrontLayer.getMapSize().subtract(tempLayer.getKernelSize(), 1));
          tempLayer.initKernel(tempFrontMapNum);
          tempLayer.initBias();
          tempLayer.initErrors(batchSize);
          tempLayer.initOutMaps(batchSize);
          break;
        case SAMPLING:
          tempLayer.setOutMapNum(tempFrontMapNum);
          tempLayer.setMapSize(tempFrontLayer.getMapSize().divide(tempLayer.getScaleSize()));
          tempLayer.initErrors(batchSize);
          tempLayer.initOutMaps(batchSize);
          break;
        case OUTPUT:
          tempLayer.initOutputKernel(tempFrontMapNum, tempFrontLayer.getMapSize());
          tempLayer.initBias();
          tempLayer.initErrors(batchSize);
          tempLayer.initOutMaps(batchSize);
          break;
      }
    }
  }

  /**
   * Forward computing.
   */
  private void forward(Instance instance) {
    setInputLayerOutput(instance);
    for (int l = 1; l < layerBuilder.getNumLayers(); l++) {
      CnnLayer tempCurrentLayer = layerBuilder.getLayer(l);
      CnnLayer tempLastLayer = layerBuilder.getLayer(l - 1);
      switch (tempCurrentLayer.getType()) {
        case CONVOLUTION:
          setConvolutionOutput(tempCurrentLayer, tempLastLayer);
          break;
        case SAMPLING:
          setSampOutput(tempCurrentLayer, tempLastLayer);
          break;
        case OUTPUT:
          setConvolutionOutput(tempCurrentLayer, tempLastLayer);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Set the in layer output. Given a record, copy its values to the input
   * map.
   */
  private void setInputLayerOutput(Instance paraRecord) {
    CnnLayer tempInputLayer = layerBuilder.getLayer(0);
    Size tempMapSize = tempInputLayer.getMapSize();
    double[] tempAttributes = paraRecord.getAttributes();
    if (tempAttributes.length != tempMapSize.width * tempMapSize.height)
      throw new RuntimeException("input record does not match the map size.");

    for (int i = 0; i < tempMapSize.width; i++) {
      for (int j = 0; j < tempMapSize.height; j++) {
        tempInputLayer.setMapValue(0, i, j, tempAttributes[tempMapSize.height * i + j]);
      }
    }
  }

  /**
   * Compute the convolution output according to the output of the last layer.
   * 
   * @param paraLastLayer
   *                      the last layer.
   * @param paraLayer
   *                      the current layer.
   */
  private void setConvolutionOutput(final CnnLayer paraLayer, final CnnLayer paraLastLayer) {
    // int mapNum = paraLayer.getOutMapNum();
    final int lastMapNum = paraLastLayer.getOutMapNum();

    // Attention: paraLayer.getOutMapNum() may not be right.
    for (int j = 0; j < paraLayer.getOutMapNum(); j++) {
      double[][] tempSumMatrix = null;
      for (int i = 0; i < lastMapNum; i++) {
        double[][] lastMap = paraLastLayer.getMap(i);
        double[][] kernel = paraLayer.getKernel(i, j);
        if (tempSumMatrix == null) {
          // On the first map.
          tempSumMatrix = MathUtils.convnValid(lastMap, kernel);
        } else {
          // Sum up convolution maps
          tempSumMatrix = MathUtils.matrixOp(MathUtils.convnValid(lastMap, kernel),
              tempSumMatrix, null, null, MathUtils.plus);
        }
      }

      // Activation.
      final double bias = paraLayer.getBias(j);
      tempSumMatrix = MathUtils.matrixOp(tempSumMatrix, new Operator() {
        private static final long serialVersionUID = 2469461972825890810L;

        @Override
        public double process(double value) {
          return MathUtils.sigmod(value + bias);
        }

      });

      paraLayer.setMapValue(j, tempSumMatrix);
    }
  }

  /**
   * Compute the convolution output according to the output of the last layer.
   * 
   * @param paraLastLayer
   *                      the last layer.
   * @param paraLayer
   *                      the current layer.
   */
  private void setSampOutput(final CnnLayer paraLayer, final CnnLayer paraLastLayer) {
    // int tempLastMapNum = paraLastLayer.getOutMapNum();

    // Attention: paraLayer.outMapNum may not be right.
    for (int i = 0; i < paraLayer.outMapNum; i++) {
      double[][] lastMap = paraLastLayer.getMap(i);
      Size scaleSize = paraLayer.getScaleSize();
      double[][] sampMatrix = MathUtils.scaleMatrix(lastMap, scaleSize);
      paraLayer.setMapValue(i, sampMatrix);
    }
  }

  /**
   * Train the cnn.
   */
  public void train(Dataset paraDataset, int paraRounds) {
    for (int t = 0; t < paraRounds; t++) {
      System.out.println("Iteration: " + t);
      int tempNumEpochs = paraDataset.size() / batchSize;
      if (paraDataset.size() % batchSize != 0)
        tempNumEpochs++;
      // logger.info("第{}次迭代，epochsNum: {}", t, epochsNum);
      double tempNumCorrect = 0;
      int tempCount = 0;
      for (int i = 0; i < tempNumEpochs; i++) {
        int[] tempRandomPerm = MathUtils.randomPerm(paraDataset.size(), batchSize);
        CnnLayer.prepareForNewBatch();

        for (int index : tempRandomPerm) {
          boolean isRight = train(paraDataset.getInstance(index));
          if (isRight)
            tempNumCorrect++;
          tempCount++;
          CnnLayer.prepareForNewRecord();
        }

        updateParameters();
        if (i % 50 == 0) {
          System.out.print("..");
          if (i + 50 > tempNumEpochs)
            System.out.println();
        }
      }
      double p = 1.0 * tempNumCorrect / tempCount;
      if (t % 10 == 1 && p > 0.96) {
        ALPHA = 0.001 + ALPHA * 0.9;
        // logger.info("设置 alpha = {}", ALPHA);
      }
      System.out.println("Training precision: " + p);
      // logger.info("计算精度： {}/{}={}.", right, count, p);
    }
  }

  /**
   * Train the cnn with only one record.
   * 
   * @param paraRecord
   *                   The given record.
   */
  private boolean train(Instance paraRecord) {
    forward(paraRecord);
    boolean result = backPropagation(paraRecord);
    return result;
  }

  /**
   * Back-propagation.
   * 
   * @param paraRecord
   *                   The given record.
   */
  private boolean backPropagation(Instance paraRecord) {
    boolean result = setOutputLayerErrors(paraRecord);
    setHiddenLayerErrors();
    return result;
  }

  /**
   * Update parameters.
   */
  private void updateParameters() {
    for (int l = 1; l < layerBuilder.getNumLayers(); l++) {
      CnnLayer layer = layerBuilder.getLayer(l);
      CnnLayer lastLayer = layerBuilder.getLayer(l - 1);
      switch (layer.getType()) {
        case CONVOLUTION:
        case OUTPUT:
          updateKernels(layer, lastLayer);
          updateBias(layer, lastLayer);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Update bias.
   */
  private void updateBias(final CnnLayer paraLayer, CnnLayer paraLastLayer) {
    final double[][][][] errors = paraLayer.getErrors();
    // int mapNum = paraLayer.getOutMapNum();

    // Attention: getOutMapNum() may not be correct.
    for (int j = 0; j < paraLayer.getOutMapNum(); j++) {
      double[][] error = MathUtils.sum(errors, j);
      double deltaBias = MathUtils.sum(error) / batchSize;
      double bias = paraLayer.getBias(j) + ALPHA * deltaBias;
      paraLayer.setBias(j, bias);
    }
  }

  /**
   * Update kernels.
   */
  private void updateKernels(final CnnLayer paraLayer, final CnnLayer paraLastLayer) {
    // int mapNum = paraLayer.getOutMapNum();
    int tempLastMapNum = paraLastLayer.getOutMapNum();

    // Attention: getOutMapNum() may not be right
    for (int j = 0; j < paraLayer.getOutMapNum(); j++) {
      for (int i = 0; i < tempLastMapNum; i++) {
        double[][] tempDeltaKernel = null;
        for (int r = 0; r < batchSize; r++) {
          double[][] error = paraLayer.getError(r, j);
          if (tempDeltaKernel == null)
            tempDeltaKernel = MathUtils.convnValid(paraLastLayer.getMap(r, i), error);
          else {
            tempDeltaKernel = MathUtils.matrixOp(
                MathUtils.convnValid(paraLastLayer.getMap(r, i), error),
                tempDeltaKernel, null, null, MathUtils.plus);
          }
        }

        tempDeltaKernel = MathUtils.matrixOp(tempDeltaKernel, divideBatchSize);
        if (!rangeCheck(tempDeltaKernel, -10, 10)) {
          System.exit(0);
        }
        double[][] kernel = paraLayer.getKernel(i, j);
        tempDeltaKernel = MathUtils.matrixOp(kernel, tempDeltaKernel, multiplyLambda,
            multiplyAlpha, MathUtils.plus);
        paraLayer.setKernel(i, j, tempDeltaKernel);
      }
    }
  }

  /**
   * Set errors of all hidden layers.
   */
  private void setHiddenLayerErrors() {
    // System.out.println("setHiddenLayerErrors");
    for (int l = layerBuilder.getNumLayers() - 2; l > 0; l--) {
      CnnLayer layer = layerBuilder.getLayer(l);
      CnnLayer nextLayer = layerBuilder.getLayer(l + 1);
      // System.out.println("layertype = " + layer.getType());
      switch (layer.getType()) {
        case SAMPLING:
          setSamplingErrors(layer, nextLayer);
          break;
        case CONVOLUTION:
          setConvolutionErrors(layer, nextLayer);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Set errors of a sampling layer.
   */
  private void setSamplingErrors(final CnnLayer paraLayer, final CnnLayer paraNextLayer) {
    // int mapNum = layer.getOutMapNum();
    int tempNextMapNum = paraNextLayer.getOutMapNum();
    // Attention: getOutMapNum() may not be correct
    for (int i = 0; i < paraLayer.getOutMapNum(); i++) {
      double[][] sum = null;
      for (int j = 0; j < tempNextMapNum; j++) {
        double[][] nextError = paraNextLayer.getError(j);
        double[][] kernel = paraNextLayer.getKernel(i, j);
        if (sum == null) {
          sum = MathUtils.convnFull(nextError, MathUtils.rot180(kernel));
        } else {
          sum = MathUtils.matrixOp(
              MathUtils.convnFull(nextError, MathUtils.rot180(kernel)), sum, null,
              null, MathUtils.plus);
        }
      }
      paraLayer.setError(i, sum);

      if (!rangeCheck(sum, -2, 2))
        System.out.println(
            "setSampErrors, error out of range.\r\n" + Arrays.deepToString(sum));
    }
  }

  /**
   * Set errors of a sampling layer.
   */
  private void setConvolutionErrors(final CnnLayer paraLayer, final CnnLayer paraNextLayer) {
    // System.out.println("setConvErrors");
    for (int m = 0; m < paraLayer.getOutMapNum(); m++) {
      Size tempScale = paraNextLayer.getScaleSize();
      double[][] tempNextLayerErrors = paraNextLayer.getError(m);
      double[][] tempMap = paraLayer.getMap(m);
      double[][] tempOutMatrix = MathUtils.matrixOp(tempMap, MathUtils.cloneMatrix(tempMap),
          null, MathUtils.one_value, MathUtils.multiply);
      tempOutMatrix = MathUtils.matrixOp(tempOutMatrix,
          MathUtils.kronecker(tempNextLayerErrors, tempScale), null, null,
          MathUtils.multiply);
      paraLayer.setError(m, tempOutMatrix);

      // System.out.println("range check nextError");
      if (!rangeCheck(tempNextLayerErrors, -10, 10)) {
        System.out.println("setConvErrors, nextError out of range:\r\n"
            + Arrays.deepToString(tempNextLayerErrors));
        System.out.println("the new errors are:\r\n" + Arrays.deepToString(tempOutMatrix));

        System.exit(0);
      }

      if (!rangeCheck(tempOutMatrix, -10, 10)) {
        System.out.println("setConvErrors, error out of range.");
        System.exit(0);
      }
    }
  }

  /**
   * Set errors of a sampling layer.
   */
  private boolean setOutputLayerErrors(Instance paraRecord) {
    CnnLayer tempOutputLayer = layerBuilder.getOutputLayer();
    int tempMapNum = tempOutputLayer.getOutMapNum();

    double[] tempTarget = new double[tempMapNum];
    double[] tempOutMaps = new double[tempMapNum];
    for (int m = 0; m < tempMapNum; m++) {
      double[][] outmap = tempOutputLayer.getMap(m);
      tempOutMaps[m] = outmap[0][0];
    }

    int tempLabel = paraRecord.getLabel().intValue();
    tempTarget[tempLabel] = 1;
    // Log.i(record.getLable() + "outmaps:" +
    // Util.fomart(outmaps)
    // + Arrays.toString(target));
    for (int m = 0; m < tempMapNum; m++) {
      tempOutputLayer.setError(m, 0, 0,
          tempOutMaps[m] * (1 - tempOutMaps[m]) * (tempTarget[m] - tempOutMaps[m]));
    }

    return tempLabel == MathUtils.getMaxIndex(tempOutMaps);
  }

  /**
   * Setup the network.
   */
  public void setup(int paraBatchSize) {
    CnnLayer tempInputLayer = layerBuilder.getLayer(0);
    tempInputLayer.initOutMaps(paraBatchSize);

    for (int i = 1; i < layerBuilder.getNumLayers(); i++) {
      CnnLayer tempLayer = layerBuilder.getLayer(i);
      CnnLayer tempLastLayer = layerBuilder.getLayer(i - 1);
      int tempLastMapNum = tempLastLayer.getOutMapNum();
      switch (tempLayer.getType()) {
        case INPUT:
          break;
        case CONVOLUTION:
          tempLayer.setMapSize(
              tempLastLayer.getMapSize().subtract(tempLayer.getKernelSize(), 1));
          tempLayer.initKernel(tempLastMapNum);
          tempLayer.initBias();
          tempLayer.initErrors(paraBatchSize);
          tempLayer.initOutMaps(paraBatchSize);
          break;
        case SAMPLING:
          tempLayer.setOutMapNum(tempLastMapNum);
          tempLayer.setMapSize(tempLastLayer.getMapSize().divide(tempLayer.getScaleSize()));
          tempLayer.initErrors(paraBatchSize);
          tempLayer.initOutMaps(paraBatchSize);
          break;
        case OUTPUT:
          tempLayer.initOutputKernel(tempLastMapNum, tempLastLayer.getMapSize());
          tempLayer.initBias();
          tempLayer.initErrors(paraBatchSize);
          tempLayer.initOutMaps(paraBatchSize);
          break;
      }
    }
  }

  /**
   * Predict for the dataset.
   */
  public int[] predict(Dataset paraDataset) {
    System.out.println("Predicting ... ");
    CnnLayer.prepareForNewBatch();

    int[] resultPredictions = new int[paraDataset.size()];
    double tempCorrect = 0.0;

    Instance tempRecord;
    for (int i = 0; i < paraDataset.size(); i++) {
      tempRecord = paraDataset.getInstance(i);
      forward(tempRecord);
      CnnLayer outputLayer = layerBuilder.getOutputLayer();

      int tempMapNum = outputLayer.getOutMapNum();
      double[] tempOut = new double[tempMapNum];
      for (int m = 0; m < tempMapNum; m++) {
        double[][] outmap = outputLayer.getMap(m);
        tempOut[m] = outmap[0][0];
      }

      resultPredictions[i] = MathUtils.getMaxIndex(tempOut);
      if (resultPredictions[i] == tempRecord.getLabel().intValue()) {
        tempCorrect++;
      }
    }

    System.out.println("Accuracy: " + tempCorrect / paraDataset.size());
    return resultPredictions;
  }

  /**
   * Range check, only for debugging.
   * 
   * @param paraMatix
   *                       The given matrix.
   * @param paraLowerBound
   * @param paraUpperBound
   */
  public boolean rangeCheck(double[][] paraMatrix, double paraLowerBound, double paraUpperBound) {
    for (int i = 0; i < paraMatrix.length; i++) {
      for (int j = 0; j < paraMatrix[0].length; j++) {
        if ((paraMatrix[i][j] < paraLowerBound) || (paraMatrix[i][j] > paraUpperBound)) {
          System.out.println("" + paraMatrix[i][j] + " out of range (" + paraLowerBound
              + ", " + paraUpperBound + ")\r\n");
          return false;
        }
      }
    }

    return true;
  }

  /**
   * The main entrance.
   */
  public static void main(String[] args) {
    LayerBuilder builder = new LayerBuilder();
    // Input layer, the maps are 28*28
    builder.addLayer(new CnnLayer(LayerTypeEnum.INPUT, -1, new Size(28, 28)));
    // Convolution output has size 24*24, 24=28+1-5
    builder.addLayer(new CnnLayer(LayerTypeEnum.CONVOLUTION, 6, new Size(5, 5)));
    // Sampling output has size 12*12,12=24/2
    builder.addLayer(new CnnLayer(LayerTypeEnum.SAMPLING, -1, new Size(2, 2)));
    // Convolution output has size 8*8, 8=12+1-5
    builder.addLayer(new CnnLayer(LayerTypeEnum.CONVOLUTION, 12, new Size(5, 5)));
    // Sampling output has size4×4,4=8/2
    builder.addLayer(new CnnLayer(LayerTypeEnum.SAMPLING, -1, new Size(2, 2)));
    // output layer, digits 0 - 9.
    builder.addLayer(new CnnLayer(LayerTypeEnum.OUTPUT, 10, null));
    // Construct the full CNN.
    FullCnn tempCnn = new FullCnn(builder, 10);

    Dataset tempTrainingSet = new Dataset("./sample-data/train.format", ",", 784);

    // Train the model.
    tempCnn.train(tempTrainingSet, 10);
    // tempCnn.predict(tempTrainingSet);
  }
}
