package cn.sdkd.ccse.cise.ces.pojo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 重复率
 *
 * @author jaybill
 */
public class Similarity {
    private String id;
    private ArrayList<Double> similarity = new ArrayList<Double>();

    public ArrayList<Double> getSimilarity() {
        return similarity;
    }

    public void setSimilarity(ArrayList<Double> similarity) {
        this.similarity = similarity;
    }

    private ArrayList<String> sId = new ArrayList<String>();//和学号为id的学生相比，最高重复率的学生学号

    public ArrayList<String> getsId() {
        return sId;
    }

    public void setsId(ArrayList<String> sId) {
        this.sId = sId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Similarity [id=" + id + ", similarity=" + similarity + ", sId=" + sId + ", getSimilarity()="
                + getSimilarity() + ", getsId()=" + getsId() + ", getId()=" + getId() + "]";
    }

    public ArrayList<SimilarityRec> sort() {
        ArrayList<SimilarityRec> simis = new ArrayList<SimilarityRec>();
        for (int i = 0; i < sId.size(); i++) {
            SimilarityRec sr = new SimilarityRec(similarity.get(i), sId.get(i));
            simis.add(sr);
        }
        Collections.sort(simis);
        return simis;

    }

    public class SimilarityRec implements Comparable<SimilarityRec> {
        Double similarity;
        String id;

        public SimilarityRec(Double similarity, String id) {
            this.similarity = similarity;
            this.id = id;
        }

        public Double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(Double similarity) {
            this.similarity = similarity;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int compareTo(SimilarityRec o) {
            Double i = this.getSimilarity() - o.getSimilarity();//先按照年龄排序
            if (i < 0) {
                return 1;
            } else if (i > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
