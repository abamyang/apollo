package com.abcoder.apollo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsoupTest {

    final static String MAIN_SITE="http://www.kehuan.net.cn";

    final static String WEB_FOLDER="/Users/yangjing/Documents/webproject/apollo/";

    public static void main(String[] args) {
        try {

            String authorStr="";

            Document doc = Jsoup.connect("http://www.kehuan.net.cn/").get();
            //System.out.println(doc);
            Elements authorLinks = doc.select(".sidebox li a");
            for (Element link : authorLinks) {
                String linkHref = MAIN_SITE+link.attr("href");
                String linkText = link.text();
                System.out.println(linkHref + "\t"+linkText);

                authorStr+="<h2>"+linkText+"</h2>";

//                doc = Jsoup.connect(linkHref).get();
//                Elements booksLinks = doc.select(".co3 li a");
//                for (Element booksLink : booksLinks) {
//                    String bookLinkHref = MAIN_SITE + booksLink.attr("href");
//                    String bookLinkText = booksLink.text();
//                    System.out.println("--"+bookLinkHref + "\t" + bookLinkText);
//
//                    doc = Jsoup.connect(bookLinkHref).get();
//                    Elements bigMenus = doc.select("dl");
//                    for (Element bigMenu : bigMenus) {
//                        String bigMenuText = bigMenu.selectFirst("dt").text();
//                        System.out.println("----"+bigMenuText);
//
//                        Elements menuLinks= bigMenu.select("dd a");
//                        for (Element menuLink : menuLinks) {
//                            String menuLinkHref = MAIN_SITE + menuLink.attr("href");
//                            String menuLinkText = menuLink.text();
//                            System.out.println("------" + menuLinkHref + "\t" + menuLinkText);
//
//                            doc = Jsoup.connect(menuLinkHref).get();
//                            System.out.println("------"+doc.selectFirst(".text"));
//                        }
//                    }
//                }
            }

            File input = new File(WEB_FOLDER+"indexTemplate.html");

            Document doc3 = Jsoup.parse(input,"UTF-8","http://www.oschina.net/");
            doc3.selectFirst(".container").append(authorStr);

            FileOutputStream fos = new FileOutputStream(new File(WEB_FOLDER+"oup.html"), false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(doc3.html());
            osw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
