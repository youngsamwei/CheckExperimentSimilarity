package cn.sdkd.ccse.cise.ces.similarity;


import cn.sdkd.ccse.cise.ces.pojo.Similarity;
import cn.sdkd.ccse.cise.ces.pojo.SimilarityPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;

public abstract class SimilarityService implements Callable<Similarity>{

	protected ExecutorService es = Executors.newCachedThreadPool();
	protected CompletionService<Similarity> cs = new ExecutorCompletionService<Similarity>(es);
	
	protected List<HashMap<String,ArrayList<String>>> list;
	protected HashMap<String,ArrayList<String>> currentStu;
	/**
	 * 重复率分析
	 * @param list
	 * @return
	 */
	public abstract List<Similarity> analyseSimilarity(List<HashMap<String, ArrayList<String>>> list);
	public abstract List<SimilarityPair> analyseSimilaritySingleThread(List<HashMap<String, ArrayList<String>>> list);
	public abstract Similarity analyse(List<HashMap<String,ArrayList<String>>> list,HashMap<String,ArrayList<String>> currentStu);
	
	/**
	 * 获取HashMap的第一个key
	 */
	String getFirstKey(HashMap<String,ArrayList<String>> nextStu){
		Iterator<Entry<String, ArrayList<String>>> it = nextStu.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, ArrayList<String>> en = it.next();
			return en.getKey();
		}
		return null;
	}
	
	public Similarity call(){
		return analyse(list,currentStu);
	}
}
