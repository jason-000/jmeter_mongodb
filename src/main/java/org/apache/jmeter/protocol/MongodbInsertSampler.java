package org.apache.jmeter.protocol;

import com.alibaba.fastjson.JSONObject;
import org.apache.jmeter.config.Arguments;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;

public class MongodbInsertSampler implements JavaSamplerClient {

    private static String host = null;
    private static int port = 0;
    private String collection = "";
    private String db = "";

    private MongodbClient mc = null;
    private String content = ""; //接收要post的json数据

    //拿到用户gui输入参数值
    @Override
    public void setupTest(JavaSamplerContext context) {
        host = context.getParameter("host");
        port = context.getIntParameter("port");
        db = context.getParameter("db");
        collection = context.getParameter("collection");
        content = context.getParameter("content");

    }

    //核心处理逻辑
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        SampleResult result = new SampleResult();
        result.setSampleLabel("MongodbInsertSampler");
        result.sampleStart();
        mc = new MongodbClient();
        mc.ConnectionMongoClient(host, port, db);
        Document doc = new Document();

        //输入的json字符中转换 对象，遍历赋值给doc
        JSONObject JsonInput = JSONObject.parseObject(content);
        Iterator iter = JsonInput.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            doc.append(entry.getKey().toString(),entry.getValue());

        }

        Boolean res =mc.insert(collection,doc);

        if(res==true){
            result.setResponseData("Insert Successfully".getBytes());
            mc.closeConnection();
            result.setSuccessful(true);
        }else {
            result.setResponseData("Insert Fail".getBytes());
            mc.closeConnection();
            result.setSuccessful(false);
        }

        result.sampleEnd();


        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {

    }

    //参数默认值展示在gui
    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();
        params.addArgument("host", "localhost");
        params.addArgument("port", "27017");
        params.addArgument("db", "myDB");
        params.addArgument("collection", "test");
        params.addArgument("content", "");

        return params;
    }


}
