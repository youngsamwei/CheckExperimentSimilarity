package cn.sdkd.ccse.cise.ces.similarity;

import cn.sdkd.ccse.cise.ces.pojo.Similarity;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam on 2018/5/17.
 */
public class SimHashSimilarityService extends SimilarityService {
    private double threshold = 0.9;
    static HashMap<String, SimHash> simHashs = new HashMap<String, SimHash>();

    public SimHashSimilarityService() {
    }

    public SimHashSimilarityService(List<HashMap<String, ArrayList<String>>> list,
                                    HashMap<String, ArrayList<String>> currentStu) {
        this.list = list;
        this.currentStu = currentStu;
    }

    public List<Similarity> analyseSimilaritySingleThread(List<HashMap<String, ArrayList<String>>> list) {

        List<Similarity> resList = new ArrayList<Similarity>();
        //多线程计算相似度
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, ArrayList<String>> expe = list.get(i);
            String key = getFirstKey(expe);
            SimHash sh = new SimHash(expe.get(key), 64);
            simHashs.put(key, sh);
            Similarity sm = analyse(list, expe);
            resList.add(sm);
//            cs.submit(new SimHashSimilarityService(list, expe));
        }
        //等待各个线程完成
//        for(int i=0;i<list.size();i++){
//            try {
//                Similarity sm = cs.take().get();
//
//                resList.add(sm);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
        //显示相似度

        for (int i = 0; i < resList.size(); i++) {
            Similarity sm = resList.get(i);
            ArrayList<Similarity.SimilarityRec> srecs = sm.sort();
            int out = 0;
            for (Similarity.SimilarityRec rec : srecs) {
                if (rec.getSimilarity() >= this.threshold) {
                    out = 1;
                    if (rec.getSimilarity() == 1) {
                        System.err.println(sm.getId() + "---" + rec.getId() + "---" + rec.getSimilarity());
                    }else{
                        System.out.println(sm.getId() + "---" + rec.getId() + "---" + rec.getSimilarity());
                    }
                }
            }
            if (out > 0) {
                System.out.println();
            }
        }
        //整理格式
        ArrayList<String> arr = resList.get(0).getsId();
        List<Similarity> res = new ArrayList<Similarity>();
        for (int i = 0; i < arr.size(); i++) {
            String flag = arr.get(i);
            for (int j = 0; j < resList.size(); j++) {
                if (resList.get(j).getId().equals(flag)) {
                    Similarity sm = resList.get(j);
                    res.add(sm);
                    break;
                }
            }
        }
        System.out.println("整理格式完成");
        return res;
    }

    public List<Similarity> analyseSimilarity(List<HashMap<String, ArrayList<String>>> list) {
        return null;
    }

    public Similarity analyse(List<HashMap<String, ArrayList<String>>> list, HashMap<String, ArrayList<String>> currentStu) {
        Similarity resOne = new Similarity();//与其他人的重复率比较
        //设置当前学生的姓名
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
