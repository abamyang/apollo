package com.abcoder.apollo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsoupTest2 {

    final static String MAIN_SITE="http://www.kehuan.net.cn";

    final static String WEB_FOLDER="/Users/yangjing/Documents/webproject/apollo/";


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

    static File bookInput = new File(WEB_FOLDER+"bookTemplate.html");
    static File contentInput = new File(WEB_FOLDER+"contentTemplate.html");

    public static void main(String[] args) {
        try {
            Elements regionIns = docIndexTemplate.select(".row");
            Document doc = Jsoup.connect("http://www.kehuan.net.cn/").get();
            Elements regionLinks = doc.select(".sidebox");

            makeRegion(regionLinks,regionIns);

            FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+"index.html"), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(docIndexTemplate.html());
            osw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void makeRegion(Elements regionLinks,Elements regionIns) throws Exception {
        int i=0;
        for (Element region : regionLinks) {
            i++;
            String regionTitleStr="<div class=\"col-md-4\">";
            regionTitleStr+="<h2>"+region.selectFirst("h2").text()+"</h2>";

            regionTitleStr+="<div class=\"region-div\" style=\"background:url(region_"+i+".jpeg)\"><ul>";
            Elements authorLinks = region.select("li");

            regionTitleStr+=makeAuthor(authorLinks);

            regionTitleStr+="</ul></div>";

            regionTitleStr+="</div>";
            regionIns.append(regionTitleStr);
        }
    }

    public static String makeAuthor(Elements authorLinks) throws Exception {
        String regionTitleStr="";
        for (Element authLink : authorLinks) {
            String linkHref = authLink.selectFirst("a").attr("href");
            String linkText = authLink.text();

            regionTitleStr+="<li onclick=\"window.location.href='/apollo"+linkHref+"'\">"+authLink.text()+"</li>";

            Document booksDoc = Jsoup.connect(MAIN_SITE+linkHref).get();

            File authoHtmlFile=new File(WEB_FOLDER+linkHref);
            authoHtmlFile.createNewFile();

            docAuthorTemplate.selectFirst("#author_name").text(linkText);

            docAuthorTemplate.selectFirst("#author_desc").text(booksDoc.selectFirst(".text").selectFirst("p").text());

            String booksStr="<div class=\"row\">";

            Elements booksLinks = booksDoc.select(".co3 li a");

            booksStr+=makeBook(booksLinks);

            booksStr+="</div>";
            docAuthorTemplate.selectFirst("#book_list").html(booksStr);

            FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+linkHref), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(docAuthorTemplate.html());
            osw.close();
        }
        return regionTitleStr;
    }

    public static String makeBook(Elements booksLinks) throws Exception {
        String booksStr="";
        for (Element booksLink : booksLinks) {
            Document docBookTemplate=Jsoup.parse(bookInput,"UTF-8","http://www.oschina.net/");

            String bookLinkHref = booksLink.attr("href");
            String bookLinkText = booksLink.text();

            //booksStr+="<li onclick=\"window.location.href='/apollo"+bookLinkHref+"'\">"+bookLinkText+"</li>";

            booksStr+="<div class=\"col-md-3\" style=\"padding:10px 10px 10px 10px\" onclick=\"window.location.href='/apollo"+bookLinkHref
                    +"'\"><div style=\"padding-left:5px;padding-top:5px;width:100%;height:30px;background-color:#eee\">"+bookLinkText+"</div></div>";

            docBookTemplate.selectFirst("#book_name").text(bookLinkText);

            Document menusDoc = Jsoup.connect(MAIN_SITE+bookLinkHref).get();
            makeMenuList(menusDoc,docBookTemplate);


            File bookHtmlFile=new File(WEB_FOLDER+bookLinkHref);
            bookHtmlFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+bookLinkHref), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(docBookTemplate.html());
            osw.close();
        }
        return booksStr;
    }

    public static void makeMenuList(Document menusDoc,Document docBookTemplate) throws Exception {

        if(menusDoc.selectFirst(".description")!=null) {
            docBookTemplate.selectFirst("#book_desc").text(menusDoc.selectFirst(".description").text());
        }else{
            //System.out.println(bookLinkText);
        }

        Elements menus=menusDoc.select("dl");

        for(Element menu:menus){
            String dt=menu.selectFirst("dt").text();
            //System.out.println("---"+dt);
            if(!dt.equals("作品相关")){
                String menuTitle="<div>";
                menuTitle+="<h2><small> "+dt+"</small></h2>";

                Elements dds=menu.select("dd a");
                menuTitle+="<div class=\"row\" >";
                for(Element dd:dds){

                    String ddHref=dd.attr("href");

                    String[] files= ddHref.split("/");

                    String contentFolderName=files[2];
                    File contentFolder=new File(WEB_FOLDER+"/book/"+contentFolderName);
                    if(!contentFolder.exists()) {
                        contentFolder.mkdir();
                    }

                    File contentHtmlFile=new File(WEB_FOLDER+ddHref);
                    contentHtmlFile.createNewFile();
                    makeContent(dd,contentHtmlFile);

                    menuTitle+="<div class=\"col-md-3\" style=\"padding:10px 10px 10px 10px\" onclick=\"window.location.href='/apollo"+ddHref
                            +"'\"><div style=\"padding-left:5px;padding-top:5px;width:100%;height:30px;background-color:#eee\">"+dd.text()+"</div></div>";
                }

                menuTitle+="</div>";
                docBookTemplate.selectFirst("#book_menu").append(menuTitle);
            }
        }
    }

    public static void makeContent(Element dd,File file) throws IOException, InterruptedException {
        String ddHref=dd.attr("href");

        Thread.sleep(100);
        Document contentsDoc = Jsoup.connect(MAIN_SITE+ddHref).get();

        Document docContentTemplate = Jsoup.parse(contentInput,"UTF-8","http://www.oschina.net/");
        docContentTemplate.selectFirst("#content_name").text(contentsDoc.selectFirst("#container").selectFirst("h1").text());

        docContentTemplate.selectFirst("#content_text").html(contentsDoc.selectFirst(".text").html());

        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        osw.write(docContentTemplate.html());
        osw.close();
    }
}
