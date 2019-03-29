package com.abcoder.apollo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class luoxiaLoad {

    final static String MAIN_SITE="http://www.luoxia.com/santi/santi-3/";

    final static String WEB_FOLDER="E:\\projs\\appolo\\book";

    final  static  String BOOK_NAME="sishenyongsheng";

    public static void main(String[] args) throws IOException {
      String oldMenuStr=Tool.readFile(WEB_FOLDER+BOOK_NAME+".json");


        JSONObject jsonObject=JSONObject.parseObject(oldMenuStr);


        Document doc = Jsoup.connect(MAIN_SITE).get();
        Elements links = doc.select(".book-list li a");

        Element ddLast=links.get(links.size()-1);

        String ddLastHref=ddLast.attr("href");
        String lastIndex=ddLastHref.replace(".htm","").split("/")[4];

        Integer pindex=0;
        JSONArray fcontentList=new JSONArray();
        for (Element link : links) {
            pindex++;
            JSONObject obj=new JSONObject();
            String fcontentName=link.text();
            obj.put("fcontentName",fcontentName.substring(0,7));


            String furl=BOOK_NAME+"/"+pindex.toString();
            obj.put("fcontentUrl",furl);
            fcontentList.add(obj);

            makeContent(link,pindex.toString(),lastIndex);
        }

        JSONArray fpartList=new JSONArray();
        jsonObject.put("fpartList",fpartList);

        JSONObject partObj=new JSONObject();
        partObj.put("fpartName","死神永生");
        fpartList.add(partObj);

        partObj.put("fcontentList",fcontentList);

        System.out.println(JSON.toJSONString(jsonObject));

        Tool.writeFile(WEB_FOLDER+BOOK_NAME+".json", JSON.toJSONString(jsonObject));

    }

    public static void makeContent(Element dd,String index,String lastIndex) {

        String ddHref=dd.attr("href");
        try {

            File contentFolder=new File(WEB_FOLDER+"/"+BOOK_NAME+"/");
            if(!contentFolder.exists()) {
                contentFolder.mkdir();
            }

            Thread.sleep(600);
            Document contentsDoc = Jsoup.connect(ddHref).get();

            Elements links= contentsDoc.select("a");

            for (Element link : links) {
                link.remove();
            }

            Elements ptexts= contentsDoc.select("p");

            for (Element ptext : ptexts) {
                String text=ptext.text();
                if(text.contains("落")&&text.contains("霞")&&text.contains("小")&&text.contains("说")) {
                    ptext.remove();
                }

                if(text.contains("微信看书更方便")){
                    ptext.remove();
                }
            }


            JSONObject jsonObject=new JSONObject();

            jsonObject.put("fcontentName",contentsDoc.selectFirst("#nr_title").text().substring(0,7));
            jsonObject.put("fcontentText",contentsDoc.selectFirst("#nr1").html().replace("搜索关注 落霞小说 公众号，微信看书更方便！",""));
            jsonObject.put("ffirstIndex","1");
            jsonObject.put("flastIndex",lastIndex);

            Tool.writeFile(WEB_FOLDER+"/"+BOOK_NAME+File.separator+index+".json", JSON.toJSONString(jsonObject));
        } catch (Exception e) {

            System.out.println("+++++"+ddHref);

            e.printStackTrace();
        }
    }
}
