package com.example.proj1;

import java.util.*;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class TextAnalyzer {

    //This just creates a big list of skip or common words
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "the", "of", "and", "a", "to", "in", "is", "it", "that", "was",
            "for", "on", "are", "as", "with", "his", "they", "at", "be",
            "this", "from", "or", "an", "by", "not", "but", "what", "all",
            "were", "when", "we", "there", "can", "which", "their", "if",
            "will", "other", "about", "out", "many", "then", "them", "these",
            "so", "some", "her", "would", "make", "like", "him", "into",
            "time", "has", "look", "two", "more", "write", "go", "see",
            "number", "no", "way", "could", "people", "my", "than", "first",
            "water", "been", "call", "who", "its", "now", "find", "long",
            "down", "day", "did", "get", "come", "made", "may", "part"
    ));


    // IDF(word) = log(totalPages / pagesContainingWord); rare words score higher
    public Map<String,Double> computeIDF(Map<String, HT> pageFrequencies){

        // Collect every unique word across all pages
        Set<String> allWords = new HashSet<>();
        for (HT table : pageFrequencies.values()) {
            for (Object key : table.keys()) {
                allWords.add((String) key);
            }
        }

        Map<String, Double> idfScores =  new HashMap<>();

        // Count how many pages contain this word, just if it's there not total
        int totalPages = pageFrequencies.size();
        for(String word : allWords){
            int pagesContainingWord = 0;
            for(HT table : pageFrequencies.values()){
                if(table.getCount(word) > 0) {
                    pagesContainingWord++;
                }
            }
            //This is my math for idf so log(10/3) = idf score
            double idf = Math.log((double)totalPages/(double)pagesContainingWord);
            idfScores.put(word, idf);
        }
        return idfScores;
    }

    /*
     * Builds my word count table or my TF part data
     * This turns all the text into lowercase and strips
     * punctuation so words are not mistaken for others
     */

    public HT buildFrequencyTable(String text) {
        HT ht = new HT();
        String[] words = text.toLowerCase().split("[^a-z0-9]+");

        //skips empty strings and skip words and moves to next
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (STOPWORDS.contains(word)) continue;
            ht.increment(word);
        }
        return ht;
    }

    /*
     * Measures how similar two pages are by treating each as a vector of
     * TF-IDF weighted word scores, then computing the cosine of the angle
     * between them. Result ranges 0 (completely unrelated) to 1 (identical).
     * not after finding tf and idf multiply together
     * tf = 5 idf = 1.204 tf-idf = 6.02 score/weight
     */
    public double cosineSimilarity(HT pageA, HT pageB, Map<String, Double> idfScores) {

        // Dot product: for every word both pages share, multiply their
        // TF-IDF weights together and sum the results
        double dotProduct = 0.0;
        for (Object keyObj : pageA.keys()) {
            String word = (String) keyObj;
            int countA = pageA.getCount(word);
            int countB = pageB.getCount(word);

            // word must appear in both pages to contribute
            if (countB > 0) {
                double idf = idfScores.get(word);
                double tfidfA = countA * idf;
                double tfidfB = countB * idf;
                dotProduct += tfidfA * tfidfB;
            }
        }

        // Page A's vector: square each of A's own weights and
        // sum them, independent of page B
        double sumSquaresA = 0.0;
        for (Object keyObj : pageA.keys()) {
            String word = (String) keyObj;
            int countA = pageA.getCount(word);
            double idf = idfScores.get(word);
            double tfidfA = countA * idf;
            sumSquaresA += tfidfA * tfidfA;
        }
        double magnitudeA = Math.sqrt(sumSquaresA);

        // Same idea for page B
        double sumSquaresB = 0.0;
        for (Object keyObj : pageB.keys()) {
            String word = (String) keyObj;
            int countB = pageB.getCount(word);
            double idf = idfScores.get(word);
            double tfidfB = countB * idf;
            sumSquaresB += tfidfB * tfidfB;
        }
        double magnitudeB = Math.sqrt(sumSquaresB);

        // This returns the cosine sim
        return dotProduct / (magnitudeA * magnitudeB);
    }

    /*
     * Compares one page against every other page in the control file and
     * returns the topN most similar, sorted highest-similarity first.
     */
    public List<Map.Entry<String, Double>> findMostSimilar(String targetUrl, Map<String, HT> pageFrequencies, Map<String, Double> idfScores, int topN) {

        // gets hashtable for all word counts put in
        HT targetTable = pageFrequencies.get(targetUrl);
        List<Map.Entry<String, Double>> scores = new ArrayList<>();

        // this goes through all pages, skips my already target page.
        for (Map.Entry<String, HT> entry : pageFrequencies.entrySet()) {
            String otherUrl = entry.getKey();

            if (otherUrl.equals(targetUrl)) continue; // skip comparing a page to itself

            HT otherTable = entry.getValue();
            // calc for cosine sim
            double similarity = cosineSimilarity(targetTable, otherTable, idfScores);

            // adds all sim scores to a table
            scores.add(Map.entry(otherUrl, similarity));
        }

        // Sorts highest similarity first
        scores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        //this grabs the top 2 scores
        if (scores.size() > topN) {
            return scores.subList(0, topN);
        }
        return scores;
    }
}