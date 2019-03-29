package com.abcoder.apollo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class BookToHtml {

    final static String WEB_FOLDER="/Users/yangjing/Documents/webproject/apollo/book/";

    final static String bookTemplatePath="";

    static File bookInput = new File(bookTemplatePath);

    public static void main(String[] args) throws IOException {

        //读取book下的所有的xx.json

        File dir=new File(WEB_FOLDER);

        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();//获取文件的名称E:\复件 demodir\Learn\sgim_piccell.v1.bin.bak

                return name.endsWith(".json");//过滤文件类型为.bak或者.BAK文件，而不包含.BAK或者.bak的文件
            }
        });

        //读取模版

        Document docContentTemplate = Jsoup.parse(bookInput,"UTF-8","http://www.oschina.net/");

        //操作模版

        for(File jsonFile:files){


        }

        //生成html

    }
}
