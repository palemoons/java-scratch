# Day 60: Summary.

在完成 Day 56-57 KMeans 聚类算法时，遇到了如下的情况：当第一轮随机设置的中心不合理时，仅有两个聚类中有相应的样本，而第三个聚类为空集。此时如果不对第三个聚类进行合理的处理，第三个聚类的中心数值出现错误，聚类效果将会非常差。

因此，我们需要对这种情况进行一定的处理。有多种方法可以解决上述的问题：

1. 多次运行已有的聚类算法，取最好的聚类结果。
2. 使用整体数据集的中心：将第三个聚类的中心设定为整个数据集的中心。
3. 随机选择一个样本作为聚类中心。

在自行编写的代码中，选择了方案三，其实现方法和消耗时间最少：

```Java
for (int i = 0; i < numClusters; i++) {
    if (tempRealCenters[i] < dataset.numInstances())
      for (int j = 0; j < dataset.numAttributes() - 1; j++)
        tempNewCenters[i][j] = dataset.instance(tempRealCenters[i]).value(j);
    else {
      // Randomly choose a point as the new center.
      int tempIndex = random.nextInt(dataset.numInstances());
      for (int j = 0; j < dataset.numAttributes() - 1; j++)
        tempNewCenters[i][j] = dataset.instance(tempIndex).value(j);
    }
}
```
