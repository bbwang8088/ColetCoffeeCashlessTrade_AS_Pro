package tech.bbwang.www.ws;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class WSAsynResult implements Callable<String> {

	private FutureTask<String> task = null;

	public WSAsynResult(FutureTask<String> tsk) {
		this.task = tsk;
	}

	@Override
	public String call() throws Exception {
		String ret = "";
		while (this.task.isDone() == false) {
			Thread.sleep(1);
		}
		ret = this.task.get();
		return ret;
	}

}
