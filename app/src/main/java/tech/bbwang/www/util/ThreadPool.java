package tech.bbwang.www.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private final static int THREAD_MAX = 6;
	public static ExecutorService service = Executors.newFixedThreadPool(THREAD_MAX);
}
