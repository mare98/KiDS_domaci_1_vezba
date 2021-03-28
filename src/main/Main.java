package main;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

	private static final int THREAD_COUNT = 7;

	public static void main(String[] args) {
		List<BlockingDeque<String>> deques;
		ConcurrentHashMap<String, Integer> occurrences = new ConcurrentHashMap<>();
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		Thread[] threads = new Thread[THREAD_COUNT];
		CrawlerWorker[] workers = new CrawlerWorker[THREAD_COUNT];

		deques = IntStream.range(0, THREAD_COUNT).
				<BlockingDeque<String>>mapToObj(i -> new LinkedBlockingDeque<>()).collect(Collectors.toList());

		IntStream.range(0, THREAD_COUNT).forEach(i -> {
			workers[i] = new CrawlerWorker(occurrences, deques, lock, condition);
			threads[i] = new Thread(workers[i], String.valueOf(i));
		});

		for(Thread thread : threads) {
			thread.start();
		}

		Scanner sc = new Scanner(System.in);
		while(true) {

			System.out.println("1: Enter filename \n2: Print occurrences map\n0: Exit");
			int option = 0;
			try {
				option = sc.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Wrong input");
				sc.nextLine();
				continue;
			}
			if(option == 0) {
				break;
			}
			if(option == 1) {
				String filename = sc.next();
				if (filename.equals("exit")) {
					break;
				}
				List<String> urlList = readFromFile("src/" + filename + ".txt");

				// PUNJENJE DEQUES WITH URLS
				if(urlList == null) {
					continue;
				}
				int ratio = urlList.size() / THREAD_COUNT;
				for(int i = 0; i < THREAD_COUNT; i++) {
					for(int j = 0; j < ratio; j++) {
						deques.get(i).add(urlList.get(i*ratio + j));
					}
				}
				for(int i = THREAD_COUNT * ratio; i < urlList.size(); i++) {
					deques.get(THREAD_COUNT - 1).add(urlList.get(i));
				}

				lock.lock();
				try {
					condition.signalAll();
				} finally {
					lock.unlock();
				}

			} else if( option == 2 ) {
				System.out.println("Printing map: ");
				for(Entry<String, Integer> entry : occurrences.entrySet()) {
					System.out.println(entry.getKey() + ": " + entry.getValue());
				}
			} else {
				System.out.println("Nepodrzana opcija");
			}
		}
		sc.close();
		for (CrawlerWorker worker : workers) {
			worker.setForever(false);
		}
		
	}
	private static List<String> readFromFile(String filename) {
		// CITANJE URLS IZ FAJLA
		BufferedReader br = null;
		List<String> urlList = null;
		try {
			br = new BufferedReader(new FileReader(filename));

			urlList = new ArrayList<>();
			String line;
			while((line = br.readLine()) !=  null) {
				urlList.add(line);
			}

		} catch (FileNotFoundException e) {
			System.out.println("Los fajl");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return urlList;
	}
}
