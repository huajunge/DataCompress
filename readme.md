## 数据压缩算法实现

本代码实现了以下压缩算法：
Delta、VarintGB、Simple8b、PFOR

``
输入：一个整数数组
``

``
输出：bytes数组
``

运行CompressPerformanceTest类，压缩1000000条数据的测试结果
未排序的整数压缩效果：
![img.png](img.png)

排序后再压缩效果
![img_1.png](img_1.png)