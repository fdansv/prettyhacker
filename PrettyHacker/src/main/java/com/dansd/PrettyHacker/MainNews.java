package com.dansd.PrettyHacker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainNews extends Activity {

    private Document newsDoc;

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
        Elements articles = newsDoc.select("html body center table tbody");
        System.out.println("Printing selection");
        System.out.println(articles);
    }

}
