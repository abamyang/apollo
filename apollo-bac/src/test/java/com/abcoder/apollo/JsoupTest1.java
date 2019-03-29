package com.abcoder.apollo;

import org.hibernate.sql.Template;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsoupTest1 {

    final static String MAIN_SITE="http://www.kehuan.net.cn";

    final static String WEB_FOLDER="/Users/yangjing/Documents/webproject/apollo/";

    public static void main(String[] args) {
        try {

            File input = new File(WEB_FOLDER+"indexTemplate.html");
            Document docIndexTemplate = Jsoup.parse(input,"UTF-8","http://www.oschina.net/");

            File authorInput = new File(WEB_FOLDER+"/author/authorTemplate.html");
            Document docAuthorTemplate = Jsoup.parse(authorInput,"UTF-8","http://www.oschina.net/");

            File bookInput = new File(WEB_FOLDER+"/book/bookTemplate.html");
            Document docBookTemplate = Jsoup.parse(bookInput,"UTF-8","http://www.oschina.net/");

            Elements regionIns = docIndexTemplate.select(".row");

            Document doc = Jsoup.connect("http://www.kehuan.net.cn/").get();
            //System.out.println(doc);
            Elements regionLinks = doc.select(".sidebox");

            int i=0;
            for (Element region : regionLinks) {
                i++;
                String regionTitleStr="<div class=\"col-md-4\">";
                regionTitleStr+="<h2>"+region.selectFirst("h2").text()+"</h2>";
                Elements authorLinks = region.select("li");
                regionTitleStr+="<div class=\"region-div\" style=\"background:url(region_"+i+".jpeg)\"><ul>";
                for (Element authLink : authorLinks) {
                    String linkHref = authLink.selectFirst("a").attr("href");
                    String linkText = authLink.text();
                    System.out.println(linkHref + "\t" + linkText);

                    regionTitleStr+="<li onclick=\"window.location.href='/apollo"+linkHref+"'\">"+authLink.text()+"</li>";

                    String filePath=WEB_FOLDER+linkHref;
                    //System.out.println(filePath);


                    Document booksDoc = Jsoup.connect(MAIN_SITE+linkHref).get();


                    File authoHtmlFile=new File(WEB_FOLDER+linkHref);
                    authoHtmlFile.createNewFile();

                    docAuthorTemplate.selectFirst("#author_name").text(linkText);

                    docAuthorTemplate.selectFirst("#author_desc").text(booksDoc.selectFirst(".text").selectFirst("p").text());

                    String booksStr="<ul>";

                    Elements booksLinks = booksDoc.select(".co3 li a");
                    for (Element booksLink : booksLinks) {
                        String bookLinkHref = booksLink.attr("href");
                        String bookLinkText = booksLink.text();
                        System.out.println("--" + bookLinkHref + "\t" + bookLinkText);
                        booksStr+="<li onclick=\"window.location.href='/apollo"+bookLinkHref+"'\">"+bookLinkText+"</li>";

                        docBookTemplate.selectFirst("#book_name").text(bookLinkText);

                        Document menusDoc = Jsoup.connect(MAIN_SITE+bookLinkHref).get();
                        System.out.println("---"+menusDoc.selectFirst(".description"));

                        if(menusDoc.selectFirst(".description")!=null) {

                            docBookTemplate.selectFirst("#book_desc").text(menusDoc.selectFirst(".description").text());
                        }


                        File bookHtmlFile=new File(WEB_FOLDER+bookLinkHref);
                        bookHtmlFile.createNewFile();

                        FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+bookLinkHref), false);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                        osw.write(docBookTemplate.html());
                        osw.close();
                    }

                    booksStr+="</ul>";
                    docAuthorTemplate.selectFirst("#book_list").html(booksStr);


                    FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+linkHref), false);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                    osw.write(docAuthorTemplate.html());
                    osw.close();
                }
                regionTitleStr+="</ul></div>";

                regionTitleStr+="</div>";
                regionIns.append(regionTitleStr);

            }

            FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+"oup.html"), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(docIndexTemplate.html());
            osw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
