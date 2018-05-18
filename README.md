# CheckExperimentSimilarity
检查实验报告内容的相似度。  实验报告以word文档形式存在，doc或docx为扩展名。 使用simhash算法检测。

程序入口
cn.sdkd.ccse.cise.ces.Main

使用hanlp分词。
采用单线程和多线程两种方式运行，默认采用单线程方式运行。
多线程方式借鉴jay-bill/check-similarity
