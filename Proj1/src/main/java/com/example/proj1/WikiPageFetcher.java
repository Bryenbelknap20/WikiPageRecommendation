package com.example.proj1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

// given a url fetches the page and parses its html into a parsed document
public class WikiPageFetcher {
    public Document fetchPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
