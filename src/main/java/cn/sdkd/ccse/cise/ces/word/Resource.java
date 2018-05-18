package cn.sdkd.ccse.cise.ces.word;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.util.concurrent.Callable;

public interface Resource extends Callable<String>{
	
	/**
	 * 获取文件的文本内容
	 * @param filePath：文件路径
	 * @return
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	String getText(String filePath) throws IOException, XmlException, OpenXML4JException;
}
