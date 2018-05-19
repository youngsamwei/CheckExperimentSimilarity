package cn.sdkd.ccse.cise.ces.pojo;

import java.math.BigInteger;

/**
 * Created by sam on 2018/5/19.
 */
public class Doc {
    String id;
    BigInteger simhashFigerints;
    DocCluster cluster;

    public Doc(String id, BigInteger simhashFigerints) {
        this.id = id;
        this.simhashFigerints = simhashFigerints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getSimhashFigerints() {
        return simhashFigerints;
    }

    public void setSimhashFigerints(BigInteger simhashFigerints) {
        this.simhashFigerints = simhashFigerints;
    }

    public DocCluster getCluster() {
        return cluster;
    }

    public void setCluster(DocCluster cluster) {
        this.cluster = cluster;
    }
}
