package cn.sdkd.ccse.cise.ces.pojo;

import java.math.BigInteger;

/**
 * Created by sam on 2018/5/18.
 */
public class SimilarityPair implements Comparable<SimilarityPair> {
    String name1;
    String name2;
    Double similarity;

    BigInteger simhash1;
    BigInteger simhash2;

    public SimilarityPair(String name1, String name2, Double similarity) {
        this.name1 = name1;
        this.name2 = name2;
        this.similarity = similarity;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public BigInteger getSimhash1() {
        return simhash1;
    }

    public void setSimhash1(BigInteger simhash1) {
        this.simhash1 = simhash1;
    }

    public BigInteger getSimhash2() {
        return simhash2;
    }

    public void setSimhash2(BigInteger simhash2) {
        this.simhash2 = simhash2;
    }

    /**
     * 先按照相似度排序，再按照name1和name2排序
     *
     * @param o
     * @return
     */
    public int compareTo(SimilarityPair o) {
        Double i = this.getSimilarity() - o.getSimilarity();
        if (i < 0) {
            return 1;
        } else if (i > 0) {
            return -1;
        } else {
            int name1cmp = name1.compareTo(o.getName1());
            if (name1cmp == 0) {
                return name2.compareTo(o.getName2());
            } else {
                return name1cmp;
            }
        }
    }
}