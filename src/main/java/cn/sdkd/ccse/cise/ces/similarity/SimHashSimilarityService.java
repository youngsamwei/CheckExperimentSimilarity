package cn.sdkd.ccse.cise.ces.similarity;

import cn.sdkd.ccse.cise.ces.pojo.Similarity;
import cn.sdkd.ccse.cise.ces.pojo.SimilarityPair;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam on 2018/5/17.
 */
public class SimHashSimilarityService extends SimilarityService {
    private double threshold = 0.9;
    private int hashbits = 64;
    static HashMap<String, SimHash> simHashs = new HashMap<String, SimHash>();

    public SimHashSimilarityService() {
    }

    public SimHashSimilarityService(List<HashMap<String, ArrayList<String>>> list,
                                    HashMap<String, ArrayList<String>> currentStu) {
        this.list = list;
        this.currentStu = currentStu;
    }

    /**
     * 在有序的基础上消除重复
     *
     * @param r 已经有序的list
     * @return 消除重复后的list
     */
    public List<SimilarityPair> distinct(List<SimilarityPair> r) {
        List<SimilarityPair> nr = new ArrayList<SimilarityPair>();
        nr.add(r.get(0));
        for (int i = 1; i < r.size(); i++) {
            SimilarityPair sp1 = r.get(i - 1);
            SimilarityPair sp2 = r.get(i);
            if (sp1.getName1().equalsIgnoreCase(sp2.getName1()) && sp1.getName2().equalsIgnoreCase(sp2.getName2())) {
                continue;
            } else {
                nr.add(sp2);
            }
        }
        return nr;
    }

    public List<SimilarityPair> analyseSimilaritySingleThread(List<HashMap<String, ArrayList<String>>> list) {
        List<SimilarityPair> r = new ArrayList<SimilarityPair>();
        List<Similarity> resList = new ArrayList<Similarity>();
        //转换为simhash
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, ArrayList<String>> expe = list.get(i);
            String key = getFirstKey(expe);
            SimHash sh = new SimHash(expe.get(key), hashbits);
            simHashs.put(key, sh);
        }
        //计算相似度
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, ArrayList<String>> expe = list.get(i);
            String key = getFirstKey(expe);
            Similarity sm = analyse(list, expe);
            for (int j = 0; j < sm.getsId().size(); j++) {
                if (sm.getSimilarity().get(j) >= threshold) {
                    String name1 = key;
                    String name2 = sm.getsId().get(j);
                    if (name1.compareTo(name2) > 0) {
                        name1 = name2;
                        name2 = key;
                    }
                    SimilarityPair sp = new SimilarityPair(name1, name2, sm.getSimilarity().get(j));
                    sp.setSimhash1(simHashs.get(name1).getStrSimHash());
                    sp.setSimhash2(simHashs.get(name2).getStrSimHash());
                    r.add(sp);
                }
            }
            resList.add(sm);
        }

        Collections.sort(r);
        System.out.println("消除重复前：" + r.size() + "对");
        r = distinct(r);
        System.out.println("消除重复后 ：" + r.size() + "对");
        //显示相似度

        double prev = 0;
        for (SimilarityPair sp : r) {

            if (sp.getSimilarity() != prev) {
                System.out.println();
            }
            String s = String.format("(%s,\t %s,\t %8.8f)", sp.getName1(), sp.getName2(), sp.getSimilarity());
            System.out.println(s);
            prev = sp.getSimilarity();
        }

        System.out.println("输出完成");
        return r;
    }

    public List<Similarity> analyseSimilarity(List<HashMap<String, ArrayList<String>>> list) {
        return null;
    }

    public Similarity analyse(List<HashMap<String, ArrayList<String>>> list, HashMap<String, ArrayList<String>> currentStu) {
        Similarity resOne = new Similarity();//与其他人的重复率比较
        String currentKey = getFirstKey(currentStu);
        resOne.setId(currentKey);
        SimHash simHash = simHashs.get(currentKey);
        Iterator<Map.Entry<String, SimHash>> it = this.simHashs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SimHash> entry = it.next();

            String nextKey = entry.getKey();
            if (currentKey.equalsIgnoreCase(nextKey)) {
                continue;
            }
            ArrayList<String> arr = resOne.getsId();
            arr.add(nextKey);
            resOne.setsId(arr);

            SimHash simHash1 = this.simHashs.get(nextKey);
            Double similar = simHash.getSemblance(simHash1);

            ArrayList<Double> arrSim = resOne.getSimilarity();
            arrSim.add(similar);
            resOne.setSimilarity(arrSim);
        }

        return resOne;
    }
}
