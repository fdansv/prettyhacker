package com.dansd.PrettyHacker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainNews extends Activity {

    private Document newsDoc;
    List articleList = new ArrayList<Article>();
    private boolean inWebView;

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
        NewsAdapter theAdapter = new NewsAdapter(this,
                R.layout.newsrow,
                this.articleList);
        ListView newsList = (ListView) findViewById(R.id.newsList);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                WebView webView = new WebView(MainNews.this);
                webView.setWebViewClient(new WebViewClient());
                System.out.println(webView);
                MainNews.this.setContentView(webView);
                Article thisArticle = (Article) view.getTag();
                webView.loadUrl(thisArticle.link);
                inWebView = true;

            }
        });
        newsList.setAdapter(theAdapter);
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
            try{
                thisArticle.user = articles.get(i+1).select("td").get(1).select("a").get(0).text();
            }
            catch(Exception e){
                thisArticle.user = "";
            }
            articleList.add(thisArticle);

        }
    }
    
    class Article{
        public String title;
        public String link;
        public String commentsLink;
        public String user;

        public void getImages(){
            new ImageGetter().execute(this);
        }

        class ImageGetter extends AsyncTask<Article, Void, Integer> {
            @Override
            protected Integer doInBackground(Article... article) {
                try {
                    Document page =Jsoup.connect(Article.this.link).get();
                    Elements imgs = page.getElementsByTag("img");
                    System.out.println(imgs.size());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }
    }

    class NewsAdapter extends ArrayAdapter<Article>{

        private Context context;
        private int resource;
        private List<Article> theArticles;

        public NewsAdapter(Context context, int textViewResourceId, List<Article> articles) {
            super(context, textViewResourceId, articles);
            this.context = context;
            this.resource = textViewResourceId;
            this.theArticles = articles;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Article theArticle = theArticles.get(position);
            theArticle.getImages();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(resource, parent, false);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.articleImage);
            imageView.setImageResource(R.drawable.hk);
            Typeface rt = Typeface.createFromAsset(context.getAssets(),"Roboto-Regular.ttf");
            TextView textView = (TextView) rowView. findViewById(R.id.newsTitle);
            textView.setTypeface(rt);
            textView.setText(theArticle.title);
            rowView.setTag(theArticle);
            return rowView;
        }
    }
    @Override
    public void onBackPressed() {
        if(inWebView){
            setContentView(R.layout.activity_main);
            generateViews();
            inWebView = false;
        }
        else{
            super.onBackPressed();
        }
    }

}
