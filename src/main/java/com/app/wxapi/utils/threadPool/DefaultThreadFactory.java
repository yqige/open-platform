package com.app.wxapi.utils.threadPool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 * 
 * @author farmer
 *
 */
public class DefaultThreadFactory implements ThreadFactory {
	
	private String _namePrefix;
	private AtomicInteger _number = new AtomicInteger(1);
	private ThreadGroup _threadGroup;

	public DefaultThreadFactory() {
		this("default");
	}

	public DefaultThreadFactory(String namePrefix) {
		this._namePrefix = namePrefix;
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (null != threadGroup.getParent()) {
            threadGroup = threadGroup.getParent();
        }
		_threadGroup = new ThreadGroup(threadGroup, _namePrefix + "-pool");
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(_threadGroup, r);
		thread.setName(_threadGroup.getName() + "-" + _number.getAndIncrement());
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (Thread.NORM_PRIORITY != thread.getPriority()) {
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}

}
