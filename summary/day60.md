# Day 60: Summary.

## KNN
KNN算法作为传统的惰性学习算法，并没有利用已有数据集进行训练的过程，因此也不会提取样本内部特征。在小样本的情况下，算法可以快速获取最近邻结果；当样本增加，运行时间会显著增加。

## M-distance
协同过滤（collaborative filtering）是一种在推荐系统中广泛使用的技术。该技术通过分析用户或者事物之间的相似性（“协同”），来预测用户可能感兴趣的内容并将此内容推荐给用户。  

Memory-based协同过滤有下述的两种情况：
1. 基于用户（User-based）的协同过滤：用相似统计的方法得到具有相似爱好或者兴趣的相邻用户，称为基于用户的协同过滤。
2. 基于项目（Item-based）的协同过滤：通过计算项目之间的相似性来代替计算用户之间的相似性。

提供的样本由于是按照用户ID进行排序，如果不重新按照项目ID排序，运行user-based recommendation时算法执行时间会显著增加。


## KMeans
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

## 朴素贝叶斯
朴素贝叶斯与概率论息息相关，结合相关材料记录了一些笔记(朴素贝叶斯笔记)[https://palemoons.tech/2023/06/16/朴素贝叶斯笔记/]。