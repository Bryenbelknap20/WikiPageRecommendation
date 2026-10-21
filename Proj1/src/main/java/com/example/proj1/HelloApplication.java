package com.example.proj1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import java.io.IOException;
import org.jsoup.nodes.Document;
import java.util.Map;
import java.util.HashMap;

//Entry point for my JavaFX
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //My ControlFileReader reads my control.txt file and returns a url
        ControlFileReader reader = new ControlFileReader();
        List<String> urls = reader.read("/control.txt");

        //this then fetches the wiki page, and creates an empty map for page text
        WikiPageFetcher fetcher = new WikiPageFetcher();
        Map<String, String> pageTexts = new HashMap<>();

        //this fetches the info for Jsoup, grabs main body text only
        //strips html and returns plain text
        for (String url : urls) {
            try {
                Document doc = fetcher.fetchPage(url);
                String content = doc.select("#mw-content-text").text();
                pageTexts.put(url, content);
            } catch (IOException e) {
                System.out.println("Failed to fetch: " + url);
            }
        }

        // This will build my freq tables turns into Hash Table,
        // holding data of how often every word appears
        TextAnalyzer analyzer = new TextAnalyzer();
        Map<String, HT> pageFrequencies = new HashMap<>();
        for (Map.Entry<String, String> entry : pageTexts.entrySet()) {
            pageFrequencies.put(entry.getKey(), analyzer.buildFrequencyTable(entry.getValue()));
        }

        //this is my IDF and scores the word based on rarity
        //low scores common words, high score rare words
        Map<String, Double> idfScores = analyzer.computeIDF(pageFrequencies);

        //This just loads my UI from my fxml and creates a scene/window
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        //After finding this just hands it important info
        HelloController controller = fxmlLoader.getController();
        controller.setData(pageFrequencies, idfScores, analyzer);

        stage.setTitle("Wikipedia Similarity Finder");
        stage.setScene(scene);
        stage.show();
    }
}


