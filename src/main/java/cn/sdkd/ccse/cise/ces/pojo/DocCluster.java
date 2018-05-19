package cn.sdkd.ccse.cise.ces.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 2018/5/19.
 */
public class DocCluster {
    String name;
    List<Doc> memebers;

    public DocCluster(String name) {
        this.name = name;
        memebers = new ArrayList<Doc>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Doc> getMembers() {
        return memebers;
    }

    public Doc getDoc(int i){
        return memebers.get(i);
    }

    public boolean add(Doc doc){
        return memebers.add(doc);
    }

    public boolean addAll(List<Doc> docs){
       return memebers.addAll(docs);
    }
}
