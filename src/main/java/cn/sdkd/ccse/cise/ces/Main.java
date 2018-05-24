package cn.sdkd.ccse.cise.ces;

import cn.sdkd.ccse.cise.ces.cluster.HierarchicalCluster;
import cn.sdkd.ccse.cise.ces.cluster.KMeans;
import cn.sdkd.ccse.cise.ces.pojo.Doc;
import cn.sdkd.ccse.cise.ces.pojo.DocCluster;
import cn.sdkd.ccse.cise.ces.pojo.Similarity;
import cn.sdkd.ccse.cise.ces.pojo.SimilarityPair;
import cn.sdkd.ccse.cise.ces.similarity.ServiceFacade;
import cn.sdkd.ccse.cise.ces.similarity.SimilarityFactory;
import cn.sdkd.ccse.cise.ces.similarity.SimilarityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2018/5/17.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /* 获取文本 */
    /* 计算相似度 */
    /* 输出结果*/
    public static void checkExperimentSimilarity(String dirRealPath) {
        byte type = 0;

        File file = new File(dirRealPath);
        File[] files = file.listFiles();
        ArrayList<HashMap<String, ArrayList<String>>> resList = new ArrayList<HashMap<String, ArrayList<String>>>();
        //获取word内容、分词
        LOGGER.info("--------开始获取内容并分词-------");
        ServiceFacade serviceFacade = new ServiceFacade();

        serviceFacade.multiThreadHandleWords(files, resList);

        LOGGER.info("-------获取内容并分词结束--------");
        LOGGER.info("-------开始分析相似度--------");
        //获取相似度实例
        SimilarityService simiService = SimilarityFactory.getSimiralityType(type);
        //多线程分析相似度
        List<SimilarityPair> res = simiService.analyseSimilaritySingleThread(resList);
        LOGGER.info("-------分析相似度结束-------");
    }

    /**
     * 处理从蓝墨云班课上导出的文件夹：
     * 每个学生一个文件夹，文件夹名称就是学生账号
     * 每个文件对应的名称是：文件夹名称 + 文件名称
     *
     * @param dirRealPath
     * @return
     */
    public static Map<String, File> getDocs(String dirRealPath) {
        HashMap<String, File> fdocs = new HashMap<String, File>();
        File file = new File(dirRealPath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                File[] fs = f.listFiles();
                for (File fdoc : fs) {
                    String name = f.getName() + "_" + fdoc.getName();
                    fdocs.put(name, fdoc);
                }
            }
        }
        return fdocs;
    }

    /**
     * @param dirRealPath 文件目录
     * @param type        算法类型：0共有词汇，1cos相似度，2simhash
     */
    public static void checkExperimentSimilaritySingleThread(String dirRealPath, byte type) {
        Map<String, File> docs = getDocs(dirRealPath);

        ArrayList<HashMap<String, ArrayList<String>>> resList = new ArrayList<HashMap<String, ArrayList<String>>>();
        //获取word内容、分词
        LOGGER.info("--------开始获取内容并分词-------");
        ServiceFacade serviceFacade = new ServiceFacade();

        serviceFacade.singleThreadHandleWords(docs, resList);

        LOGGER.info("-------获取内容并分词结束--------");
        LOGGER.info("-------开始分析相似度--------");
        //获取相似度实例
        SimilarityService simiService = SimilarityFactory.getSimiralityType(type);
        //多线程分析相似度
        List<SimilarityPair> res = simiService.analyseSimilaritySingleThread(resList);
        LOGGER.info("-------分析相似度结束-------");
//        LOGGER.info("-------层次聚类开始-------");
//        Map<String, Doc> pair2Map = pair2Map(res);
//        List<DocCluster> clusters = hierarchicalCluster(pair2Map, 20, 0.9);
//        for (DocCluster dc : clusters) {
//            System.out.println(dc.getName());
//            for (Doc doc : dc.getMembers()) {
//                System.out.println("\t" + doc.getId());
//            }
//        }
//        LOGGER.info("-------层次聚类结束-------");

//        LOGGER.info("-------kmeans聚类开始-------");
//        List<Doc> docs = pair2List(res);
//        KMeans km = new KMeans(20);
//        km.setDataSet(docs);
//        km.kmeans();
//        ArrayList<ArrayList<Doc>> clusters = km.getCluster();
//        int i = 0;
//        for (List<Doc> ls : clusters) {
//            System.out.println("Cluster" + i++);
//            for (Doc doc : ls) {
//                System.out.println("\t" + doc.getId());
//            }
//        }
//        LOGGER.info("-------kmeans聚类结束-------");
    }

    public static List<Doc> pair2List(List<SimilarityPair> pairs) {
        Map<String, Doc> map = pair2Map(pairs);
        List<Doc> docs = new ArrayList<Doc>();
        for (Object o : map.values().toArray()) {
            docs.add((Doc) o);
        }

        return docs;
    }

    public static Map<String, Doc> pair2Map(List<SimilarityPair> pairs) {
        Map<String, Doc> docs = new HashMap<String, Doc>();
        for (SimilarityPair sp : pairs) {
            if (docs.get(sp.getName1()) == null) {
                Doc doc = new Doc(sp.getName1(), sp.getSimhash1());
                docs.put(sp.getName1(), doc);
            }
            if (docs.get(sp.getName2()) == null) {
                Doc doc = new Doc(sp.getName2(), sp.getSimhash2());
                docs.put(sp.getName2(), doc);
            }
        }
        return docs;
    }

    /**
     * @param docs         聚类文档
     * @param clusterNum   聚类数
     * @param disThreshold 阈值
     * @return
     */
    public static List<DocCluster> hierarchicalCluster(Map<String, Doc> docs, int clusterNum, double disThreshold) {
        HierarchicalCluster hc = new HierarchicalCluster(docs);

        List<DocCluster> clusters = hc.startAnalysis(clusterNum, disThreshold);
        return clusters;
    }

    public static void main(String[] args) {
        String path = "E:\\IntelliJ_IDEA_workspace\\check-similarity\\src\\main\\webapp\\files\\ThuMay17100709CST2018";
        path = "G:\\360云盘工作目录\\计算机同步文件夹\\工作目录\\教学\\数据库设计\\计算机15数据库设计实验\\实验0 数据库任务陈述与系统定义";
        path = "G:\\360云盘工作目录\\计算机同步文件夹\\工作目录\\教学\\数据库设计\\计算机15数据库设计实验\\计算机15-1,2,3-数据库设计-实验1 逻辑数据库设计-ER模型设计";
        byte type = 2;

        checkExperimentSimilaritySingleThread(path, type);
//        checkExperimentSimilarity(path);
    }
}
