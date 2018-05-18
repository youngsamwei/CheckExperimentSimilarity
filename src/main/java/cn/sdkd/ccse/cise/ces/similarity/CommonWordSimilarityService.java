package cn.sdkd.ccse.cise.ces.similarity;


import cn.sdkd.ccse.cise.ces.pojo.Similarity;
import cn.sdkd.ccse.cise.ces.pojo.SimilarityPair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class CommonWordSimilarityService extends SimilarityService{
	private double threshold = 0.9;
	public CommonWordSimilarityService(){}
	public CommonWordSimilarityService(List<HashMap<String,ArrayList<String>>> list,
			HashMap<String,ArrayList<String>> currentStu){
		this.list = list;
		this.currentStu = currentStu;
	}

	public List<SimilarityPair> analyseSimilaritySingleThread(List<HashMap<String,ArrayList<String>>> list){
		List<SimilarityPair> r = new ArrayList<SimilarityPair>();
		List<Similarity> resList = new ArrayList<Similarity>();
		//多线程计算相似度
		for(int i=0;i<list.size();i++){
			HashMap<String, ArrayList<String>> expe = list.get(i);
			CommonWordSimilarityService cwss = new CommonWordSimilarityService();
			Similarity sm = cwss.analyse(list, expe);
			resList.add(sm);
		}

		//显示相似度
		for(int i=0;i<resList.size();i++){
			Similarity sm = resList.get(i);
			for(int j=0;j<sm.getsId().size();j++){
				System.out.println(sm.getId()+"---"+sm.getsId().get(j)+"---"+sm.getSimilarity().get(j));
			}
		}
		//整理格式
		ArrayList<String> arr = resList.get(0).getsId();
		List<Similarity> res = new ArrayList<Similarity>();
		for(int i=0;i<arr.size();i++){
			String flag = arr.get(i);
			for(int j=0;j<resList.size();j++){
				if(resList.get(j).getId().equals(flag)){
					Similarity sm = resList.get(j);
					res.add(sm);
					break;
				}
			}
		}
		System.out.println("整理格式完成");
		return r;
	}
	/**
	 * 分析重复率
	 * 
	 * @param list:每一个文档的学生名字和分词数组
	 * @return
	 */
	public List<Similarity> analyseSimilarity(List<HashMap<String,ArrayList<String>>> list){
		List<Similarity> resList = new ArrayList<Similarity>();
		//多线程计算相似度
		for(int i=0;i<list.size();i++){

			cs.submit(new CommonWordSimilarityService(list,list.get(i)));
		}
		//等待各个线程完成
		for(int i=0;i<list.size();i++){
			try {
				Similarity sm = cs.take().get();

				resList.add(sm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		//显示相似度
		for(int i=0;i<resList.size();i++){
			Similarity sm = resList.get(i);
			for(int j=0;j<sm.getsId().size();j++){
				System.out.println(sm.getId()+"---"+sm.getsId().get(j)+"---"+sm.getSimilarity().get(j));
			}
		}
		//整理格式
		ArrayList<String> arr = resList.get(0).getsId();
		List<Similarity> res = new ArrayList<Similarity>();
		for(int i=0;i<arr.size();i++){
			String flag = arr.get(i);
			for(int j=0;j<resList.size();j++){
				if(resList.get(j).getId().equals(flag)){
					Similarity sm = resList.get(j);
					res.add(sm);
					break;
				}
			}			
		}
		System.out.println("整理格式完成");
		return res;
	}
	
	/**
	 * 分析相似度
	 * @param list 
	 * @param currentStu
	 */
	public Similarity analyse(List<HashMap<String,ArrayList<String>>> list,HashMap<String,ArrayList<String>> currentStu){
		System.out.println("共有词相似度："+Thread.currentThread().getName());
		Similarity resOne = new Similarity();//与其他人的重复率比较
		for(int j=0;j<list.size();j++){
			HashMap<String,ArrayList<String>> nextStu = list.get(j);
			double same = 0.0;//相同的词的数量
			double similar = 1.0;//相似度
			if(currentStu!=nextStu){
				//遍历当前所求的学生的分词数组
				for(ArrayList<String> arr : currentStu.values()){
					//判断下一个学生分词数组中是否包含了str这个值
					for(String str:arr){
						for(ArrayList<String> nextArr : nextStu.values()){
							for(String nextStr : nextArr){
								if(str.equals(nextStr)){
									same++;
									break;
								}
							}
							break;
						}	
					}
					break;
				}
				//计算相似度,保留3位小数
				BigDecimal b = new BigDecimal(same/(double)getFirstVal(currentStu).size());
				similar = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			
			//设置当前学生的姓名
			String currentKey = getFirstKey(currentStu);
			resOne.setId(currentKey);
			
			String nextKey = getFirstKey(nextStu);
			//设置比较学生的id
			ArrayList<String> arr = resOne.getsId();
			arr.add(nextKey);
			resOne.setsId(arr);
			//设置重复率
			ArrayList<Double> arrSim = resOne.getSimilarity();
			arrSim.add(similar);
			resOne.setSimilarity(arrSim);				
		}
		return resOne;
	}
	
	//获取HashMap的第一个value
	public <T> T getFirstVal(HashMap<String,T> map){
		//设置当前学生的姓名
		Iterator<Entry<String, T>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, T> en = it.next();
			//获取12位的学号
			T res = en.getValue();
			return res;
		}
		return null;
	}
}
