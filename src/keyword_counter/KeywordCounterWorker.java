package keyword_counter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class KeywordCounterWorker extends RecursiveTask<ConcurrentHashMap<String, Integer>> {

    private final int MIN_SIZE;

    public KeywordCounterWorker(int min_size) {
        this.MIN_SIZE = min_size;
    }

    @Override
    protected ConcurrentHashMap<String, Integer> compute() {
        ConcurrentHashMap<String, Integer> keywordCount = new ConcurrentHashMap<>();



        return keywordCount;
    }

}
