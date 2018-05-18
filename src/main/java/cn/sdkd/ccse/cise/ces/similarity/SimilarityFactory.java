package cn.sdkd.ccse.cise.ces.similarity;

/**
 * 相似度工厂
 * @author jaybill
 *
 */
public class SimilarityFactory {
	/**
	 * 获取相似度的一个实例
	 * @param type
	 * @return
	 */
	public static SimilarityService getSimiralityType(byte type){
		SimilarityService simiService = null;
		if(type==0){
		    simiService = new CommonWordSimilarityService();			
		}else if(type==1){
			simiService = new CosineSimilarityService();			
		}else if (type == 2){
			simiService = new SimHashSimilarityService();
		}
		return  simiService;
	}
}
