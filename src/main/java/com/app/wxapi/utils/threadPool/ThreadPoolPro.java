package com.app.wxapi.utils.threadPool;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolPro {
	
	private ThreadPoolExecutor executor = null;
	
	/**
	 * 
	 * @param coreSize
	 * @param maxSize
	 * @param queueSize
	 * @param keepAliveTime
	 * @param rejectedExecutionHandler
	 */
	public ThreadPoolPro(String name,int coreSize , int maxSize , int queueSize ,int keepAliveTime,RejectedExecutionHandler rejectedExecutionHandler) {
		BlockingQueue<Runnable> queue = null;
		if(queueSize > 0){
			queue = new LinkedBlockingQueue<Runnable>(queueSize);
		}else{
			queue = new SynchronousQueue<Runnable>();
		}
		executor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS, queue, new DefaultThreadFactory(name), rejectedExecutionHandler);
	}

	/**
	 * 获取ThreadPoolExecutor
	 * @return
	 */
	public ThreadPoolExecutor getExecutor(){
		return executor;
	}
	
	/**
	 * 
	 * @param runnable
	 * @return
	 */
	public Future<?> submit(Runnable runnable){
		return executor.submit(runnable);
	}
	
	/**
	 * 
	 * @param callable
	 * @return
	 */
	public Future<?> submit(Callable<?> callable){
		return executor.submit(callable);
	}
	
	/**
	 * 
	 * @param callable
	 * @return
	 */
	public void execute(Runnable runnable){
		executor.execute(runnable);
	}
	
    /**
     * @param tasks 实现了{@link Runnable}接口的异步任务列表
     * @param timeout 任务执行超时时间
     * @param timeoutUnit 超时时间的单位
     * @throws InterruptedException 
     */
    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, 
            long timeout, TimeUnit timeoutUnit) throws InterruptedException{
        return executor.invokeAll(tasks, timeout, timeoutUnit);
    }
	/**
	 * 关闭
	 */
	public void destroy() {
		executor.shutdown();	
	}
}