package cn.sdkd.ccse.cise.ces.similarity;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class WordSegService implements Callable<HashMap<String,ArrayList<String>>>{
	private static final Logger log = Logger.getLogger(WordSegService.class);

	private String text;
	private String name;
	public WordSegService(){}
	public WordSegService(String text, String name){
		this.text = text;
		this.name = name;
	}
	
	
    public HashMap<String,ArrayList<String>> getAnalysis(String text,String name) {  	
        //hashmap，key=名字，value=分词数组
        HashMap<String,ArrayList<String>> results = new HashMap<String,ArrayList<String>>();
        ArrayList<String> arrList = new ArrayList<String>();

        List<Term> termList = StandardTokenizer.segment(text); // 对字符串进行分词
        for (Term t : termList){
            arrList.add(t.word);
        }
         //存放到hashmap中
         results.put(name, arrList);
//         System.out.println("分词："+Thread.currentThread().getName());
         return results;
     }

	public HashMap<String, ArrayList<String>> call() throws Exception {
		return getAnalysis(text,name);
	}
}
