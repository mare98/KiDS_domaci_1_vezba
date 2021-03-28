package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CrawlerWorker implements Runnable {
	
	private final ConcurrentHashMap<String, Integer> occurrences;
	private final List<BlockingDeque<String>> deques;
	private final Condition condition;
	private final Lock lock;
	private volatile boolean forever;
	
	public CrawlerWorker(ConcurrentHashMap<String, Integer> occurrences, List<BlockingDeque<String>> deques, Lock lock, Condition condition) {
		this.occurrences = occurrences;
		this.deques = deques;
		this.condition = condition;
		this.lock = lock;
		this.forever = true;
	}
	
	@Override
	public void run() {

		BlockingDeque<String> currentDeque = deques.get(Integer.parseInt(Thread.currentThread().getName()));
		System.out.println("Crawler " + Thread.currentThread().getName() + " started");

		while(this.forever) {
			try {
				String urlString;

				while (!currentDeque.isEmpty()) {
					try {
						urlString = currentDeque.poll();

						// STAVLJANJE U MAPU
						assert urlString != null;
						int wordCount = getWordCount(urlString);
						occurrences.put(urlString, wordCount);
					} catch (IOException e) {
//						System.out.println("IOException");
					}
				}

				for (BlockingDeque<String> deque : this.deques) {
					while (deque.size() > 5) {
						try {
							urlString = deque.pop();

							// STAVLJANJE U MAPU
							int wordCount = getWordCount(urlString);
							occurrences.put(urlString, wordCount);
						} catch (IOException e) {
//							System.out.println("IOException");
						}
					}
				}

				this.lock.lock();
				try {
					System.out.println("Thread " + Thread.currentThread().getName() + " awaited on condition");
					condition.await();
				} finally {
					lock.unlock();
				}
				System.out.println("Thread " + Thread.currentThread().getName() + " awakened from condition");


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Crawler " + Thread.currentThread().getName() + " finished");
	}

	private int getWordCount(String urlString) throws IOException {
		URL url = new URL("https://" + urlString);
		URLConnection urlConnection = url.openConnection();
		
		urlConnection.setConnectTimeout(2000);
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {  
			sb.append(line).append("\n");
		}  
		br.close();  
	    String result = sb.toString();
		
		String[] words = result.split("\\s+");
		return words.length;
	}

	public void setForever(boolean forever) {
		this.forever = forever;
	}
}
