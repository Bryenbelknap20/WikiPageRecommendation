package com.example.proj1;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// ControlFileReader will read the list of urls from control.txt
public class ControlFileReader {

    // I needed to put my control.txt in recource path
    // Turns url into clean list/array
    public List<String> read(String resourcePath) throws IOException {
        List<String> urls = new ArrayList<>();

        // Turns the bytes into chars and allows to read the control file
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Cleans up the line/url so it wont be read wrong
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                urls.add(line);
            }
        }

        // If this cant read any urls causes error to understand where program went wrong
        if (urls.isEmpty()) {
            throw new IOException("Control file was empty or unreadable.");
        }

        return urls;
    }
}