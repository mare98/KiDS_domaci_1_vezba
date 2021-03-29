package keyword_counter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class KeywordCounterTest {

    public static void main(String[] args) {
        ForkJoinPool threadPool = new ForkJoinPool();

        Future<ConcurrentHashMap<String, Integer>> keywordCount = threadPool.submit(new KeywordCounterWorker(10));

        try {
            keywordCount.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }
    private static class GenerateFiles {
        public static void main(String[] args) {
            try {
                URL url = new URL("https://randommer.io/Text");
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setRequestMethod("POST");
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
