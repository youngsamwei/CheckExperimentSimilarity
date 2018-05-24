package cn.sdkd.ccse.cise.ces.cluster;

import cn.sdkd.ccse.cise.ces.pojo.Doc;
import cn.sdkd.ccse.cise.ces.similarity.SimHash;

import java.math.BigInteger;
import java.util.*;

/**
 * K均值聚类算法
 */
public class KMeans {
    private int k;// 分成多少簇
    private int m;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private List<Doc> dataSet;// 数据集链表
    private ArrayList<Doc> center;// 中心链表
    private Map<String, Map<String, Double>> sim;// 相似度矩阵
    private ArrayList<ArrayList<Doc>> cluster; // 簇
    private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
    private Random random;

    /**
     * 设置需分组的原始数据集
     *
     * @param dataSet
     */

    public void setDataSet(List<Doc> dataSet) {
        this.dataSet = dataSet;

        initSim();
    }

    /**
     * 计算任意doc间的相似度
     *
     */
    private void initSim() {

        for (int i = 0; i < dataSet.size(); i++) {
            Doc doc1 = dataSet.get(i);
            Map<String, Double> map = sim.get(doc1.getId());
            if (map == null) {
                map = new HashMap<String, Double>();
            }
            for (int j = 0; j < dataSet.size(); j++) {

                Doc doc2 = dataSet.get(j);
                Double s = 0.0;
                if (i!=j){
                    s = SimHash.gtSemblance(doc1.getSimhashFigerints(), doc2.getSimhashFigerints());
                }
                map.put(doc2.getId(), s);
            }
            sim.put(doc1.getId(), map);
        }
    }

    /**
     * 获取结果分组
     *
     * @return 结果集
     */

    public ArrayList<ArrayList<Doc>> getCluster() {
        return cluster;
    }

    /**
     * 构造函数，传入需要分成的簇数量
     *
     * @param k 簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
     */
    public KMeans(int k) {
        sim = new HashMap<String, Map<String, Double>>();

        if (k <= 0) {
            k = 1;
        }
        this.k = k;
    }

    /**
     * 初始化
     */
    private void init() {
        m = 0;
        random = new Random();

        dataSetLength = dataSet.size();
        if (k > dataSetLength) {
            k = dataSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        jc = new ArrayList<Float>();
    }

    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点
     *
     * @return 中心点集
     */
    private ArrayList<Doc> initCenters() {
        ArrayList<Doc> center = new ArrayList<Doc>();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(dataSetLength);
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(dataSetLength);
                int j = 0;

                while (j < i) {
                    if (temp == randoms[j]) {
                        break;
                    }
                    j++;
                }
                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }

        for (int i = 0; i < k; i++) {
            center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
        }
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private ArrayList<ArrayList<Doc>> initCluster() {
        ArrayList<ArrayList<Doc>> cluster = new ArrayList<ArrayList<Doc>>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<Doc>());
        }

        return cluster;
    }

    /**
     * 从sim中获取doc间的相似度，再计算doc之间的距离
     *
     * @param element 点1
     * @param center  点2
     * @return 距离
     */
    private double distance(Doc element, Doc center) {
        Map<String, Double> m = sim.get(element.getId());
        Double d = m.get(center.getId());

        return 1 -d;
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance 距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(double[] distance) {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     * 核心，将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet() {
        double[] distance = new double[k];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = distance(dataSet.get(i), center.get(j));
                // System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

            }
            int minLocation = minDistance(distance);
            // System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
            // System.out.println();

            cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

        }
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element 点1
     * @param center  点2
     * @return 误差平方
     */
    private double errorSquare(Doc element, Doc center) {
        double v = distance(element, center);
        return v * v;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        float jcF = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));

            }
        }
        jc.add(jcF);
    }

    /**
     * 选取簇中离所有点平均距离最小的那个doc为中心
     */
    private void setNewCenter() {
        for (int i = 0; i < k; i++) {
            int n = cluster.get(i).size();
            if (n != 0) {

                List<Doc> c = cluster.get(i);
                Doc newCenter = c.get(0);
                /*最小平均距离*/
                Double mind = Double.MAX_VALUE;
                /*平均距离*/
                Double md = 0.0;
                for (int j = 0; j < n; j++) {
                    Map<String, Double > map = sim.get(c.get(j).getId());
                    for (int k = 0; k < c.size();k++) {
                        Double d = map.get(c.get(k).getId());
                        md += (1 - d);
                    }
                    md = md / c.size();
                    if (md < mind){
                        newCenter = c.get(j);
                        mind = md;
                    }
                }

                center.set(i, newCenter);
            }
        }
    }

    /**
     * Kmeans算法核心过程方法
     */
    public void kmeans() {
        init();

        // 循环分组，直到误差不变为止
        while (true) {
            clusterSet();

            countRule();

            // 误差不变了，分组完成
            if (m != 0) {
                if (jc.get(m) - jc.get(m - 1) == 0) {
                    break;
                }
            }

            setNewCenter();

            m++;
            cluster.clear();
            cluster = initCluster();
        }

    }

}