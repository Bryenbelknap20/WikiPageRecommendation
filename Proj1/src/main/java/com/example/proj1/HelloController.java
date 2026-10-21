package com.example.proj1;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;

public class HelloController {

    @FXML
    private ComboBox<String> topicDropdown;

    @FXML
    private Label resultLabel;

    //These just hold my data passed from my HelloApplication
    private Map<String, HT> pageFrequencies;
    private Map<String, Double> idfScores;
    private TextAnalyzer analyzer;

    //after grabbing the data, fills my dropdown with the urls
    public void setData(Map<String, HT> pageFrequencies, Map<String, Double> idfScores, TextAnalyzer analyzer) {
        this.pageFrequencies = pageFrequencies;
        this.idfScores = idfScores;
        this.analyzer = analyzer;

        topicDropdown.getItems().addAll(pageFrequencies.keySet());
    }


    @FXML
    private void onFindSimilarClick() {
        String selectedUrl = topicDropdown.getValue();

        //this makes sure a url has been selected
        if (selectedUrl == null) {
            resultLabel.setText("Please select a topic first.");
            return;
        }

        //this gets the 2 most similar pages
        List<Map.Entry<String, Double>> topMatches = analyzer.findMostSimilar(selectedUrl, pageFrequencies, idfScores, 2);

        //This just builds my output text with the urls and sim score.
        //I made it so its formated and only shows 3 decimals instead of a huge number
        StringBuilder sb = new StringBuilder("Most similar to selected topic:\n");
        for (Map.Entry<String, Double> match : topMatches) {
            sb.append(match.getKey()).append(" (score: ").append(String.format("%.3f", match.getValue())).append(")\n");
        }
        resultLabel.setText(sb.toString());
    }
}
