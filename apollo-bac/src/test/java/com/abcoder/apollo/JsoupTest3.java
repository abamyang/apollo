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

public class JsoupTest3 {

    final static String MAIN_SITE="http://www.kehuan.net.cn";

    final static String WEB_FOLDER="E:\\projs\\web_appolo\\";


    static File input = new File(WEB_FOLDER+"indexTemplate.html");
    static Document docIndexTemplate;

    static {
        try {
            docIndexTemplate = Jsoup.parse(input,"UTF-8","http://www.oschina.net/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static File authorInput = new File(WEB_FOLDER+"authorTemplate.html");
    static Document docAuthorTemplate;

    static {
        try {
            docAuthorTemplate = Jsoup.parse(authorInput,"UTF-8","http://www.oschina.net/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Elements regionIns = docIndexTemplate.select(".row");
            Document doc = Jsoup.connect("http://www.kehuan.net.cn/").get();
            Elements regionLinks = doc.select(".sidebox");

            makeRegion(regionLinks,regionIns);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeRegion(Elements regionLinks,Elements regionIns) throws Exception {

        JSONArray jsonArray=new JSONArray();

        for (Element region : regionLinks) {

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("fregionName",region.selectFirst("h2").text());
            Elements authorLinks = region.select("li");
            jsonObject.put("fauthorList",makeAuthorList(authorLinks));
            jsonArray.add(jsonObject);

        }

        File file=new File(WEB_FOLDER+"region.json");
        if(!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(JSON.toJSONString(jsonArray));
            osw.close();
        }
    }

    public static JSONArray makeAuthorList(Elements authorLinks) throws Exception {
        JSONArray jsonArray=new JSONArray();
        for (Element authLink : authorLinks) {
            JSONObject jsonObject=new JSONObject();
            String linkHref = authLink.selectFirst("a").attr("href");
            String linkText = authLink.text();
            jsonObject.put("fauthorName",linkText);
            if(!linkText.equals("倪匡")) {
                String furl = linkHref.substring(8, linkHref.length() - 5);
                jsonObject.put("fauthorUrl", furl);
                jsonArray.add(jsonObject);
                makeAuthor(linkHref, linkText);
            }
        }
        return jsonArray;
    }

    public static void makeAuthor(String authorHref, String authorName) throws Exception {

        JSONObject jsonObject=new JSONObject();
        Document booksDoc = Jsoup.connect(MAIN_SITE+authorHref).get();

        jsonObject.put("fauthorName",authorName);
        jsonObject.put("fauthorDesc",booksDoc.selectFirst(".text").selectFirst("p").text());

        Elements bookNamesLinks = booksDoc.select(".co3 li a");
        JSONArray jsonArray=new JSONArray();
        for (Element bookNameLink : bookNamesLinks) {
            JSONObject bookNameObj=new JSONObject();
            String bookName=bookNameLink.text();
            bookNameObj.put("fbookName",bookName);

            String linkHref = bookNameLink.attr("href");
            makeMenuList(linkHref,bookName);

            String furl=linkHref.substring(6,linkHref.length()-5);
            bookNameObj.put("fbookUrl",furl);
            jsonArray.add(bookNameObj);


        }
        jsonObject.put("fbookList",jsonArray);

        File file=new File(WEB_FOLDER + authorHref.replace(".html", ".json"));
        if(!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(JSON.toJSONString(jsonObject));
            osw.close();
        }
    }


    public static void makeMenuList(String bookHref, String bookName) throws Exception {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("fbookName",bookName);
        Document bookDoc = Jsoup.connect(MAIN_SITE+bookHref).get();

        if(bookDoc.selectFirst(".description")!=null) {
            jsonObject.put("fbookDesc",bookDoc.selectFirst(".description").text());
        }else{
            jsonObject.put("fbookDesc","");
        }

        Elements menus=bookDoc.select("dl");

        JSONArray partArray=new JSONArray();
        for(Element menu:menus){
            String dt=menu.selectFirst("dt").text();
            if(!dt.equals("作品相关")){
                Elements dds=menu.select("dd a");

                JSONObject partObj=new JSONObject();
                partObj.put("fpartName",dt);

                JSONArray contentArray=new JSONArray();

                Element ddFirsr=dds.get(0);
                Element ddLast=dds.get(dds.size()-1);

                String ddFirstHref=ddFirsr.attr("href");
                String firstIndex=ddFirstHref.replace(".html","").split("/")[3];

                String ddLastHref=ddLast.attr("href");
                String lastIndex=ddLastHref.replace(".html","").split("/")[3];

                for(Element dd:dds){
                    JSONObject contentObj=new JSONObject();
                    String ddHref=dd.attr("href");
                    makeContent(dd,Integer.parseInt(firstIndex),Integer.parseInt(lastIndex));
                    contentObj.put("fcontentName",dd.text());
                    contentObj.put("fcontentUrl",ddHref.substring(6,ddHref.length()-5));
                    contentArray.add(contentObj);
                }
                partObj.put("fcontentList",contentArray);
                partArray.add(partObj);
            }
        }

        jsonObject.put("fpartList",partArray);

        File file=new File(WEB_FOLDER + bookHref.replace(".html", ".json"));
        if(!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(JSON.toJSONString(jsonObject));
            osw.close();
        }
    }

    public static void makeContent(Element dd,int firstIndex,int lastIndex) {

        String ddHref=dd.attr("href");
        String[] files= ddHref.split("/");
        String contentFolderName=files[2];
        File contentFolder=new File(WEB_FOLDER+"/book/"+contentFolderName);
        if(!contentFolder.exists()) {
            contentFolder.mkdir();
        }

        try {
            Thread.sleep(500);
            Document contentsDoc = Jsoup.connect(MAIN_SITE+ddHref).get();

            JSONObject jsonObject=new JSONObject();

            jsonObject.put("fcontentName",contentsDoc.selectFirst("#container").selectFirst("h1").text());
            jsonObject.put("fcontentText",contentsDoc.selectFirst(".text").html());
            jsonObject.put("ffirstIndex",firstIndex);
            jsonObject.put("flastIndex",lastIndex);

            File file=new File(WEB_FOLDER + ddHref.replace(".html", ".json"));
            if(!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file, false);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                osw.write(JSON.toJSONString(jsonObject));
                osw.close();
            }
        } catch (Exception e) {

            System.out.println("+++++"+ddHref);

            e.printStackTrace();
        }
    }
}
