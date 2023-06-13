package machinelearning.kmeans;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;
import weka.core.Instances;

public class KMeans {
  // Manhattan distance.
  public static final int MANHATTAN = 0;
  // Euclidean distance.
  public static final int EUCLIDEAN = 1;
  // The distance measure.
  public int distanceMeasure = EUCLIDEAN;
  // A random instance.
  public static final Random random = new Random();
  // The data.
  Instances dataset;
  // The number of clusters.
  int numClusters = 2;
  // The clusters.
  int[][] clusters;

  /*
   * The first constructor.
   * 
   * @param paraFilename. The data filename.
   */
  public KMeans(String paraFilename) {
    dataset = null;
    try {
      FileReader fileReader = new FileReader(paraFilename);
      dataset = new Instances(fileReader);
      fileReader.close();
    } catch (Exception ee) {
      System.out.println("Cannot read the file: " + paraFilename + "\r\n");
      System.exit(0);
    }
  }

  /*
   * A setter.
   */
  public void setNumClusters(int paraNumClusters) {
    numClusters = paraNumClusters;
  }

  /*
   * Get a random indices for data randomization.
   * 
   * @param paraLength. The length of the sequence.
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
   * The distance between two instances.
   * 
   * @param paraI. The index of the first instance.
   * 
   * @param paraArray. The array representing a point in the space.
   */
  public double distance(int paraI, double[] paraArray) {
    int resultDistance = 0;
    double tempDifference;
    switch (distanceMeasure) {
      case MANHATTAN:
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
          tempDifference = dataset.instance(paraI).value(i) - paraArray[i];
          if (tempDifference < 0)
            resultDistance -= tempDifference;
          else
            resultDistance += tempDifference;
        }
        break;
      case EUCLIDEAN:
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
          tempDifference = dataset.instance(paraI).value(i) - paraArray[i];
          resultDistance += tempDifference * tempDifference;
        }
        break;
      default:
        System.out.println("Unsupported distance measure: " + distanceMeasure);
    }

    return resultDistance;
  }

  /*
   * Clustering.
   */
  public void clustering() {
    int[] tempOldClusterArray = new int[dataset.numInstances()];
    tempOldClusterArray[0] = -1;
    int[] tempClusterArray = new int[dataset.numInstances()];
    Arrays.fill(tempClusterArray, 0);
    double[][] tempCenters = new double[numClusters][dataset.numAttributes() - 1];

    // Step 1. Initialize centers.
    int[] tempRandomOrders = getRandomIndices(dataset.numInstances());
    for (int i = 0; i < numClusters; i++)
      for (int j = 0; j < tempCenters[0].length; j++)
        tempCenters[i][j] = dataset.instance(tempRandomOrders[i]).value(j);

    int[] tempClusterLengths = null;
    while (!Arrays.equals(tempOldClusterArray, tempClusterArray)) {
      System.out.println("New loop ...");
      tempOldClusterArray = tempClusterArray;
      tempClusterArray = new int[dataset.numInstances()];

      // Step 2.1. Minimization.
      int tempNearestCenter;
      double tempNearestDistance, tempDistance;

      for (int i = 0; i < dataset.numInstances(); i++) {
        tempNearestCenter = -1;
        tempNearestDistance = Double.MAX_VALUE;

        for (int j = 0; j < numClusters; j++) {
          tempDistance = distance(i, tempCenters[j]);
          if (tempNearestDistance > tempDistance) {
            // Update cluster.
            tempNearestDistance = tempDistance;
            tempNearestCenter = j;
          }
        }
        tempClusterArray[i] = tempNearestCenter;
      }

      // Step 2.2. Mean.
      tempClusterLengths = new int[numClusters];
      Arrays.fill(tempClusterLengths, 0);
      double[][] tempNewCenters = new double[numClusters][dataset.numAttributes() - 1];
      for (int i = 0; i < dataset.numInstances(); i++) {
        for (int j = 0; j < tempNewCenters[0].length; j++)
          tempNewCenters[tempClusterArray[i]][j] += dataset.instance(i).value(j);
        tempClusterLengths[tempClusterArray[i]]++;
      }

      // Step 2.3. Update new centers.
      updateVirtualCenters(tempNewCenters, tempClusterLengths);

      System.out.println("Now the new centers are: " + Arrays.deepToString(tempNewCenters));
      tempCenters = tempNewCenters;
    }

    // Step 3. Form clusters.
    clusters = new int[numClusters][];
    int[] tempCounters = new int[numClusters];
    for (int i = 0; i < numClusters; i++)
      clusters[i] = new int[tempClusterLengths[i]];

    for (int i = 0; i < tempClusterArray.length; i++)
      clusters[tempClusterArray[i]][tempCounters[tempClusterArray[i]]++] = i;

    System.out.println("The clusters are: " + Arrays.deepToString(clusters));
  }

  /*
   * Calculate virtual centers
   * 
   * @param paraCenters. Array of cluster centers.
   * 
   * @param paraClustersLength. Array of cluster length.
   */
  public void updateVirtualCenters(double[][] paraCenters, int[] paraClustersLength) {
    for (int i = 0; i < paraCenters.length; i++)
      for (int j = 0; j < paraCenters[0].length; j++)
        paraCenters[i][j] /= paraClustersLength[i];
  }

  /*
   * Calculate real centers.
   * 
   * @param paraCenters. Array of cluster centers.
   * 
   * @param paraClustersLength. Array of cluster length.
   */
  public void updateRealCenters(double[][] paraCenters, int[] paraClustersLength) {
    // TODO
  }

  /*
   * Clustering.
   */
  public static void testClustering() {
    KMeans tempKMeans = new KMeans("./sample-data/iris.arff");
    tempKMeans.setNumClusters(3);
    tempKMeans.clustering();
  }

  /*
   * A testing method.
   */
  public static void main(String args[]) {
    testClustering();
  }
}
