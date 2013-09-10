package com.dansd.PrettyHacker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainNews extends Activity {

    private Document newsDoc;
    List articleList = new ArrayList<Article>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RequestTask().execute("http://news.ycombinator.com");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_news, menu);
        return true;
    }

    class RequestTask extends AsyncTask<String, Document, Document> {

        @Override
        protected Document doInBackground(String... uri) {
            try {
                newsDoc = Jsoup.connect(uri[0]).get();
                return newsDoc;
            }
            catch (IOException e) {

            }
            return null;

        }

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            if(result != null){
                parseNewsDocument();
            }
            else{
                dealWithNullDocument();
            }
        }
    }

    private void dealWithNullDocument() {
        System.out.println("Null doc");
    }

    private void parseNewsDocument() {
        Elements articles = newsDoc.select("html body center table tbody tr").get(3).select("td table tbody tr");

        for(Element articleElement: articles){
            Article thisArticle = new Article();
            thisArticle.title = articleElement.getElementsByClass("title").select("a").text();
            thisArticle.link = articleElement.getElementsByClass("title").select("a").attr("href");
            if(articleElement.getElementsByClass("title").size()>0){
                thisArticle.commentsLink = articleElement.select("td center a").attr("id").substring(3);
                System.out.println(thisArticle.commentsLink);
            }

        }
    }
    
    class Article{
        public String title;
        public String link;
        public String commentsLink;
    }

}
