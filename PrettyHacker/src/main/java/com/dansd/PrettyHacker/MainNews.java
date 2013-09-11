package com.dansd.PrettyHacker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;

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
                generateViews();
            }
            else{
                dealWithNullDocument();
            }
        }
    }

    private void generateViews() {
        String[] items={"this", "is", "a", "really", "silly", "list"};
        new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                items);
    }

    private void dealWithNullDocument() {
        System.out.println("Null doc");
    }

    private void parseNewsDocument() {
        Elements articles = newsDoc.select("html body center table tbody tr").get(3).select("td table tbody tr");

        for(int i = 0; i<articles.size(); i+=3){
            Article thisArticle = new Article();
            thisArticle.title = articles.get(i).getElementsByClass("title").select("a").text();
            thisArticle.link = articles.get(i).getElementsByClass("title").select("a").attr("href");
            try{
                String baseURL = "http://news.ycombinator.com/";
                thisArticle.commentsLink = baseURL + articles.get(i+1).select("td").get(1).select("a").get(1).attr("href");
            }
            catch(Exception e){
                thisArticle.link = "";
            }

        }
    }
    
    class Article{
        public String title;
        public String link;
        public String commentsLink;
    }

}
