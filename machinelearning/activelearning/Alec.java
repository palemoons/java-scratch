package machinelearning.activelearning;

import java.io.FileReader;
import java.util.*;
import weka.core.Instances;

public class Alec {
  // The whole dataset;
  Instances dataset;
  // The maximal number of queries that can be provided.
  int maxNumQuery;
  // The actual number of queries.
  int numQuery;
  // The radius/dc for density computation.
  double radius;
  // The densities of instances, aka rho.
  double[] densities;
  // distanceToMaster.
  double[] distanceToMaster;
  // Sorted indices. The first element indicates the instance with the biggest
  // density.
  int[] descendantDensities;
  // Priority.
  double[] priority;
  // The maixmal distance between any pair of points.
  double maximalDistance;
  // Master list.
  int[] masters;
  // Predicted labels.
  int[] predictedLabels;
  // Instance status. 0 for unprocessed, 1 for queried, 2 for classified.
  int[] instanceStatusArray;
  // The descendant indices to show the representativeness of instances in a
  // descendant order.
  int[] descendantRepresentatives;
  // Indicate the cluster of each instance. Only used in clusterInTwo(int[]).
  int[] clusterIndices;
  // Blocks with size no more than this threshold should be split further.
  int smallBlockThreshold = 3;

  /*
   * The constructor.
   * 
   * @param paraFilename. The data filename.
   */
  public Alec(String paraFilename) {
    try {
      FileReader tempFileReader = new FileReader(paraFilename);
      dataset = new Instances(tempFileReader);
      dataset.setClassIndex(dataset.numAttributes() - 1);
      tempFileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read file: " + paraFilename + "\r\n" + ee);
      System.exit(0);
    }
    computeMaximalDistance();
    clusterIndices = new int[dataset.numInstances()];
  }

  /*
   * Merge sort in descendant order to obtain an index array.
   * 
   * @param paraArray. The original array.
   * 
   * @return The sorted array.
   */
  public static int[] mergeSortToIndices(double[] paraArray) {
    int tempLength = paraArray.length;
    int[][] resultMatrix = new int[2][tempLength];

    // Initialize.
    int tempIndex = 0;
    for (int i = 0; i < tempLength; i++)
      resultMatrix[tempIndex][i] = i;

    // Merge.
    int tempCurrentLength = 1;
    // The indices for current merged groups.
    int tempFirstStart, tempSecondStart, tempSecondEnd;

    while (tempCurrentLength < tempLength) {
      // Divide into a number of groups.
      // the boundary is adaptive to array length not equal to 2^k.
      for (int i = 0; i < Math.ceil((double) tempLength / tempCurrentLength / 2); i++) {
        // Boundaries of the group.
        tempFirstStart = i * tempCurrentLength * 2;
        tempSecondStart = tempFirstStart + tempCurrentLength;
        tempSecondEnd = Math.min(tempSecondStart + tempCurrentLength - 1, tempLength - 1);
        // Merge this group.
        int tempFirstIndex = tempFirstStart;
        int tempSecondIndex = tempSecondStart;
        int tempCurrentIndex = tempFirstStart;

        if (tempSecondStart >= tempLength) {
          while (tempFirstIndex < tempLength)
            resultMatrix[(tempIndex + 1) % 2][tempCurrentIndex++] = resultMatrix[tempIndex % 2][tempFirstIndex++];
          break;
        }

        while ((tempFirstIndex <= tempSecondStart - 1) && (tempSecondIndex <= tempSecondEnd)) {
          if (paraArray[resultMatrix[tempIndex % 2][tempFirstIndex]] >= paraArray[resultMatrix[tempIndex
              % 2][tempSecondIndex]])
            resultMatrix[(tempIndex + 1) % 2][tempCurrentIndex++] = resultMatrix[tempIndex % 2][tempFirstIndex++];
          else
            resultMatrix[(tempIndex + 1) % 2][tempCurrentIndex++] = resultMatrix[tempIndex % 2][tempSecondIndex++];
        }

        while (tempFirstIndex < tempSecondStart)
          resultMatrix[(tempIndex + 1) % 2][tempCurrentIndex++] = resultMatrix[tempIndex % 2][tempFirstIndex++];
        while (tempSecondIndex <= tempSecondEnd)
          resultMatrix[(tempIndex + 1) % 2][tempCurrentIndex++] = resultMatrix[tempIndex % 2][tempSecondIndex++];
      }

      tempCurrentLength *= 2;
      tempIndex = (tempIndex + 1) % 2;
    }
    return resultMatrix[tempIndex % 2];
  }

  /*
   * The Euclidean distance between two instances.
   * 
   * @param paraI. The index of the first instance.
   * 
   * @param paraJ. The index of the second instance.
   * 
   * @return The distance.
   */
  public double distance(int paraI, int paraJ) {
    double resultDistance = 0;
    double tempDifference;
    for (int i = 0; i < dataset.numAttributes() - 1; i++) {
      tempDifference = dataset.instance(paraI).value(i) - dataset.instance(paraJ).value(i);
      resultDistance += tempDifference * tempDifference;
    }
    resultDistance = Math.sqrt(resultDistance);

    return resultDistance;
  }

  /*
   * Compute the maximal distance. The result is stored in a member variable.
   */
  public void computeMaximalDistance() {
    maximalDistance = 0;
    double tempDistance;
    for (int i = 0; i < dataset.numInstances(); i++) {
      for (int j = 0; j < dataset.numInstances(); j++) {
        tempDistance = distance(i, j);
        if (maximalDistance < tempDistance)
          maximalDistance = tempDistance;
      }
    }

    System.out.println("maximalDistance = " + maximalDistance);
  }

  /*
   * Compute the densities using Gaussian kernel.
   * 
   * @param paraBlock. The given block.
   */
  public void computeDensitiesGaussian() {
    System.out.println("radius = " + radius);
    densities = new double[dataset.numInstances()];
    double tempDistance;

    for (int i = 0; i < dataset.numInstances(); i++) {
      for (int j = 0; j < dataset.numInstances(); j++) {
        tempDistance = distance(i, j);
        densities[i] += Math.exp(-tempDistance * tempDistance / radius / radius);
      }
    }

    System.out.println("The densities are " + Arrays.toString(densities));
  }

  /*
   * Compute distanceToMaster, the distance to its master.
   */
  public void computeDistanceToMaster() {
    distanceToMaster = new double[dataset.numInstances()];
    masters = new int[dataset.numInstances()];
    descendantDensities = new int[dataset.numInstances()];
    instanceStatusArray = new int[dataset.numInstances()];

    descendantDensities = mergeSortToIndices(densities);

    double tempDistance;
    for (int i = 0; i < dataset.numInstances(); i++) {
      // Initialize.
      distanceToMaster[descendantDensities[i]] = maximalDistance;
      for (int j = 0; j < i; j++) {
        tempDistance = distance(descendantDensities[i], descendantDensities[j]);
        if (distanceToMaster[descendantDensities[i]] > tempDistance) {
          distanceToMaster[descendantDensities[i]] = tempDistance;
          masters[descendantDensities[i]] = descendantDensities[j];
        }
      }
    }
    System.out.println("First compute, masters = " + Arrays.toString(masters));
    System.out.println("descendantDensities = " + Arrays.toString(descendantDensities));
  }

  /*
   * Compute priority.
   * Element with higher priority is more likely to be selected as a cluster
   * center.
   * Now it's rho * distanceToMaster, which can also be rho^alpha *
   * distanceToMaster.
   */
  public void computePriority() {
    priority = new double[dataset.numInstances()];
    for (int i = 0; i < dataset.numInstances(); i++)
      priority[i] = densities[i] * distanceToMaster[i];
  }

  /*
   * Get cluster index of the node.
   * 
   * @param paraIndex. The index of given node.
   * 
   * @return The cluster index of the current node.
   */
  public int coincideWithMaster(int paraIndex) {
    return clusterIndices[paraIndex] == -1 ? clusterIndices[paraIndex] = coincideWithMaster(masters[paraIndex])
        : clusterIndices[paraIndex];
  }

  /*
   * Cluster a block in two. According to the master tree.
   * 
   * @param paraBlock. The given block.
   * 
   * @return The new blocks where the two most represent instances serve as the
   * root.
   */
  public int[][] clusterInTwo(int[] paraBlock) {
    // Reinitialize.
    Arrays.fill(clusterIndices, -1);
    // Initialize the cluster number of two roots.
    for (int i = 0; i < 2; i++)
      clusterIndices[paraBlock[i]] = i;
    for (int i = 0; i < paraBlock.length; i++) {
      if (clusterIndices[paraBlock[i]] != -1)
        // Already have a cluster number.
        continue;
      clusterIndices[paraBlock[i]] = coincideWithMaster(masters[paraBlock[i]]);
    }
    // The sub block.
    int[][] resultBlock = new int[2][];
    int tempFirstBlockCount = 0;
    for (int i = 0; i < clusterIndices.length; i++) {
      if (clusterIndices[i] == 0)
        tempFirstBlockCount++;
    }
    resultBlock[0] = new int[tempFirstBlockCount];
    resultBlock[1] = new int[paraBlock.length - tempFirstBlockCount];
    // Copy.
    int tempFirstIndex = 0;
    int tempSecondIndex = 0;
    for (int i = 0; i < paraBlock.length; i++) {
      if (clusterIndices[paraBlock[i]] == 0)
        resultBlock[0][tempFirstIndex++] = paraBlock[i];
      else
        resultBlock[1][tempSecondIndex++] = paraBlock[i];
    }
    System.out.println("Split (" + paraBlock.length + ") instances " + Arrays.toString(paraBlock) + "\r\nto ("
        + resultBlock[0].length + ") instances " + Arrays.toString(resultBlock[0]) + "\r\nand (" + resultBlock[1].length
        + ") instances " + Arrays.toString(resultBlock[1]));

    return resultBlock;
  }

  /*
   * Classify instances in the block by simple voting.
   * 
   * @param paraBlock. The given block.
   */
  public void vote(int[] paraBlock) {
    int[] tempClassCounts = new int[dataset.numClasses()];
    for (int i = 0; i < paraBlock.length; i++) {
      if (instanceStatusArray[paraBlock[i]] == 1)
        tempClassCounts[(int) dataset.instance(paraBlock[i]).classValue()]++;
    }
    int tempMaxClass = -1;
    int tempMaxCount = -1;
    for (int i = 0; i < tempClassCounts.length; i++) {
      if (tempMaxCount < tempClassCounts[i]) {
        tempMaxClass = i;
        tempMaxCount = tempClassCounts[i];
      }
    }
    // Classify unprocessed instances.
    for (int i = 0; i < paraBlock.length; i++) {
      if (instanceStatusArray[paraBlock[i]] == 0) {
        predictedLabels[paraBlock[i]] = tempMaxClass;
        instanceStatusArray[paraBlock[i]] = 2;
      }
    }
  }

  /*
   * Cluster based active learning.
   * 
   * @param paraRatio. The ratio of the maximal distance as the dc.
   * 
   * @param paraMaxNumQuery. The maximal number of queries for the whole dataset.
   * 
   * @param paraSmallBlockThreshold. The small block threshold.
   */
  public void clusterBasedActiveLearning(double paraRatio, int paraMaxNumQuery, int paraSmallBlockThreshold) {
    radius = maximalDistance * paraRatio;
    smallBlockThreshold = paraSmallBlockThreshold;
    maxNumQuery = paraMaxNumQuery;
    predictedLabels = new int[dataset.numInstances()];
    for (int i = 0; i < dataset.numInstances(); i++)
      predictedLabels[i] = -1;

    computeDensitiesGaussian();
    computeDistanceToMaster();
    computePriority();
    descendantRepresentatives = mergeSortToIndices(priority);
    System.out.println("desendantRepresentivates = " + Arrays.toString(descendantRepresentatives));

    numQuery = 0;
    clusterBasedActiveLearning(descendantRepresentatives);
  }

  /*
   * Cluster based active learning
   * 
   * @param paraBlock. The given block. The block must be sorted by priority in
   * descendant order.
   */
  public void clusterBasedActiveLearning(int[] paraBlock) {
    System.out.println("clusterBasedActiveLearning for block " + Arrays.toString(paraBlock));

    // Step 1. How many labels are queried in the block.
    int tempExpectedQueries = (int) Math.sqrt(paraBlock.length);
    int tempNumQuery = 0;
    for (int i = 0; i < paraBlock.length; i++) {
      if (instanceStatusArray[paraBlock[i]] == 1)
        tempNumQuery++;
    }

    // Step 2. Vote for small block.
    if ((tempNumQuery >= tempExpectedQueries) && (paraBlock.length <= smallBlockThreshold)) {
      System.out
          .println("" + tempNumQuery + " instances are queried. Vote for the block:\r\n" + Arrays.toString(paraBlock));
      vote(paraBlock);
      return;
    }

    // Step 3. Query enough labels.
    for (int i = 0; i < tempExpectedQueries; i++) {
      if (numQuery >= maxNumQuery) {
        System.out.println("No more queries are provided, numQuery = " + numQuery + ".");
        vote(paraBlock);
        return;
      }
      if (instanceStatusArray[paraBlock[i]] == 0) {
        instanceStatusArray[paraBlock[i]] = 1;
        predictedLabels[paraBlock[i]] = (int) dataset.instance(paraBlock[i]).classValue();
        numQuery++;
      }
    }

    // Check if pure.
    int tempFirstLabel = predictedLabels[paraBlock[0]];
    boolean tempPure = true;
    for (int i = 1; i < tempExpectedQueries; i++) {
      if (predictedLabels[paraBlock[i]] != tempFirstLabel) {
        tempPure = false;
        break;
      }
    }
    if (tempPure) {
      System.out.println("Classify for pure block: " + Arrays.toString(paraBlock));
      for (int i = tempExpectedQueries; i < paraBlock.length; i++) {
        if (instanceStatusArray[paraBlock[i]] == 0) {
          predictedLabels[paraBlock[i]] = tempFirstLabel;
          instanceStatusArray[paraBlock[i]] = 2;
        }
      }
      return;
    }

    // Step 5. Split into two and process them independently.
    int[][] tempBlocks = clusterInTwo(paraBlock);
    for (int i = 0; i < 2; i++)
      clusterBasedActiveLearning(tempBlocks[i]);
  }

  /*
   * Show the statistics information.
   */
  public String toString() {
    int[] tempStatusCounts = new int[3];
    double tempCorrect = 0;
    for (int i = 0; i < dataset.numInstances(); i++) {
      tempStatusCounts[instanceStatusArray[i]]++;
      if (predictedLabels[i] == (int) dataset.instance(i).classValue())
        tempCorrect++;
    }
    String resultString = "(Unhandled, Queried, Classified) = " + Arrays.toString(tempStatusCounts) + "\r\nCorrect = "
        + tempCorrect + ", accuracy = " + (tempCorrect / dataset.numInstances());
    return resultString;
  }

  public static void main(String args[]) {
    long tempStart = System.currentTimeMillis();
    System.out.println("Starting ALEC...");

    Alec tempAlec = new Alec("./sample-data/iris.arff");
    tempAlec.clusterBasedActiveLearning(0.15, 30, 3);
    System.out.println(tempAlec);

    long tempEnd = System.currentTimeMillis();
    System.out.println("Runtime: " + (tempEnd - tempStart) + "ms.");
  }
}
