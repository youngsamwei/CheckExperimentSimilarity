package cn.sdkd.ccse.cise.ces.cluster;

import cn.sdkd.ccse.cise.ces.pojo.Doc;
import cn.sdkd.ccse.cise.ces.pojo.DocCluster;
import cn.sdkd.ccse.cise.ces.similarity.SimHash;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 先使用simhash计算指纹，再使用层次聚类
 *
 * @author sam
 */
public class HierarchicalCluster {

    /**
     * 全部文档
     */
    private Map<String, Doc> docs;

    public HierarchicalCluster(Map<String, Doc> docs) {
        this.docs = docs;
    }

    public static void main(String[] args) {

        double threshold = 0.80;

        double disThreshold = 0.6;//阈值
//        HierarchicalCluster mh = new HierarchicalCluster();
        int clusterNum = 20;

//        List<DocCluster> clusters = mh.startAnalysis(clusterNum, disThreshold);
    }

    /**
     * @param ClusterNum 需要聚类的个数
     * @return
     */
    public List<DocCluster> startAnalysis(int ClusterNum, double threshold) {
        //最聚类结果
        List<DocCluster> finalClusters = new ArrayList<DocCluster>();
        //初始聚类结果
        List<DocCluster> originalClusters = initialCluster();
        finalClusters = originalClusters;
        while (finalClusters.size() > ClusterNum) {
            double min = Double.MAX_VALUE;
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            for (int i = 0; i < finalClusters.size(); i++) {
                for (int j = 0; j < finalClusters.size(); j++) {
                    if (i != j) {
                        DocCluster clusterA = finalClusters.get(i);//获取聚类1
                        DocCluster clusterB = finalClusters.get(j);//获取聚类2
                        List<Doc> dataPointsA = clusterA.getMembers();
                        List<Doc> dataPointsB = clusterB.getMembers();

                        //以下两个for循环来确定应聚那两个类
                        for (int m = 0; m < dataPointsA.size(); m++) {
                            for (int n = 0; n < dataPointsB.size(); n++) {//划分类的标准:两类中最近的两篇文档作为类的距离
                                //计算两个聚类专利文献的距离
//                              System.out.println("outer:" + (m + 1) + " / " + dataPointsA.size()+ ",inner:"+ (n + 1) + " / " +dataPointsB.size());
                                double tempWeight = getDistance(dataPointsA.get(m).getId(), dataPointsB.get(n).getId());
                                if (0 <= tempWeight && tempWeight <= 1) {//如果在0-1之间,将相似度转换为距离
                                    tempWeight = 1 - tempWeight;
                                }
                                if (tempWeight < min) {
                                    min = tempWeight;
                                    mergeIndexA = i;//保存最小距离的专利文献所在的簇
                                    mergeIndexB = j;//保存最小距离的专利文献所在的簇
                                }//end_if
                            }//end_for
                        }//end_for
                    }//end_if
                } // end for j
            }// end for i
            // 合并cluster[mergeIndexA]和cluster[mergeIndexB]
            if (min > threshold) {
                System.out.println("距离超过指定的阈值，聚类结束！");
                break;
            }
            finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
        }// end while
        return finalClusters;
    }

    /**
     * 获取两篇专利文献之间的相似度
     *
     * @param dataPointName1
     * @param dataPointName2
     * @return
     */
    private double getDistance(String dataPointName1, String dataPointName2) {
        BigInteger simhash1 = this.docs.get(dataPointName1).getSimhashFigerints();
        BigInteger simhash2 = this.docs.get(dataPointName2).getSimhashFigerints();
        return SimHash.gtSemblance(simhash1, simhash2);
    }

    /**
     * 合并两个类
     *
     * @param clusters
     * @param mergeIndexA
     * @param mergeIndexB
     * @return
     */
    private List<DocCluster> mergeCluster(List<DocCluster> clusters, int mergeIndexA,
                                          int mergeIndexB) {
        if (mergeIndexA != mergeIndexB) {
            // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
            DocCluster clusterA = clusters.get(mergeIndexA);
            DocCluster clusterB = clusters.get(mergeIndexB);
//            System.out.println("合并 " + clusterA.getName() + "[" + clusterA.getMembers().size() + "] + " + clusterB.getName() + "[" + clusterB.getMembers().size() + "]");
            List<Doc> dpB = clusterB.getMembers();
            for (Doc dp : dpB) {
                dp.setCluster(clusterA);
            }
            clusterA.addAll(dpB);
            // List<DocCluster> clusters中移除cluster[mergeIndexB]
            clusters.remove(mergeIndexB);
        }
        return clusters;
    }

    // 初始化类簇

    /**
     * 每个节点属于一个类
     *
     * @return
     */
    private List<DocCluster> initialCluster() {
        List<DocCluster> originalClusters = new ArrayList<DocCluster>();
        int i = 0;
        Iterator<String> it = docs.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Doc tempDataPoint = docs.get(key);
            List<Doc> tempDataPoints = new ArrayList<Doc>();
            tempDataPoints.add(tempDataPoint);
            DocCluster tempCluster = new DocCluster(MessageFormat.format("DocCluster {0}", String.valueOf(i++)));
            tempCluster.addAll(tempDataPoints);
            tempDataPoint.setCluster(tempCluster);
            originalClusters.add(tempCluster);
        }
        return originalClusters;
    }

}
