package cn.sdkd.ccse.cise.ces.similarity;


import cn.sdkd.ccse.cise.ces.word.WordResource;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * 服务门面，通过这个类，可以通过多线程的方式，
 * 获取文本、分词。
 *
 * @author jaybill
 */
public class ServiceFacade {
    private static final Logger logger = Logger.getLogger(WordSegService.class);
    private final ExecutorService es;
    private final CompletionService<String> cs;//存放文本
    private final CompletionService<HashMap<String, ArrayList<String>>> csh;//存放分词

    public ServiceFacade() {
        this.es = Executors.newCachedThreadPool();
        this.cs = new ExecutorCompletionService<String>(es);
        this.csh = new
                ExecutorCompletionService<HashMap<String, ArrayList<String>>>(es);
    }

    public void singleThreadHandleWords(File[] files, ArrayList<HashMap<String, ArrayList<String>>> resList) {
        for (int i = 0; i < files.length; i++) {
            //先获取word文档的内容
            //开启多线程，获取文字
            String name = files[i].getName();
            if (name.endsWith(".pdf") || name.endsWith(".zip"))
                continue;
            WordResource wr = new WordResource();
            try {
                String text = wr.getText(files[i].getAbsolutePath());
                WordSegService ss = new WordSegService();
                HashMap<String, ArrayList<String>> map = ss.getAnalysis(text.split("#_#")[1], text.split("#_#")[0]);
                resList.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 多线程处理word文档
     *
     * @param files
     * @param resList
     */
    public void multiThreadHandleWords(File[] files, ArrayList<HashMap<String, ArrayList<String>>> resList) {
        for (int i = 0; i < files.length; i++) {
            //先获取word文档的内容
            //开启多线程，获取文字
            String name = files[i].getName();
            if (name.endsWith(".pdf") || name.endsWith(".zip"))
                continue;
            cs.submit(new WordResource(files[i].getAbsolutePath()));
        }
        //等待多线程执行完成
        //再分词,传入文本和zip的名称
        for (int i = 0; i < files.length; i++) {
            String text = null;
            try {
                text = cs.take().get();//获取各个线程返回的文本内容
//				System.out.println(text);
                //获取分词
                csh.submit(new WordSegService(text.split("#_#")[1], text.split("#_#")[0]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        multiThreadDevideWords(files.length, resList);
    }

    /**
     * 多线程分词
     *
     * @param length
     * @param resList
     */
    public void multiThreadDevideWords(int length, ArrayList<HashMap<String, ArrayList<String>>> resList) {
        //把每个分词HashMap添加到list中
        for (int i = 0; i < length; i++) {
            try {
                HashMap<String, ArrayList<String>> map = csh.take().get();//获取各个线程返回的分词内容

                resList.add(map);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
