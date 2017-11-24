package com.app.wxapi.utils.threadPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具
 * @author farmer
 *
 */
public class ThreadPoolKit {
	
	/**
	 * 
	 */
	static Map<String, ThreadPoolPro> threadpoolProMap = new HashMap<String, ThreadPoolPro>();
	
	private static ThreadPoolPro threadpoolPro = null;
	
	/**
	 * 
	 * @param name
	 * @param coreSize
	 * @param maxSize
	 * @param queueSize
	 * @param keepAliveTime
	 * @param rejectedExecutionHandler
	 */
	static void init(String name,int coreSize , int maxSize , int queueSize ,int keepAliveTime,RejectedExecutionHandler rejectedExecutionHandler){
		ThreadPoolPro threadpoolPro = threadpoolProMap.get(name);
		if(threadpoolPro != null){
			throw new RuntimeException("线程池"+name+"已经存在");
		}
		threadpoolPro = new ThreadPoolPro(name,coreSize,maxSize,queueSize,keepAliveTime,rejectedExecutionHandler);
		threadpoolProMap.put(name, threadpoolPro);
		if(ThreadPoolPlugin.DEFAULT.equals(name)){
			ThreadPoolKit.threadpoolPro = threadpoolPro;
		}
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static ThreadPoolPro use(String name){
		ThreadPoolPro threadpoolPro = threadpoolProMap.get(name);
		if(threadpoolPro == null){
			throw new RuntimeException("线程池"+name+"不存在");
		}
		return threadpoolPro;
	}
	

	/**
	 * 获取ThreadPoolExecutor
	 * @return
	 */
	public static ThreadPoolExecutor getExecutor(){
		return threadpoolPro.getExecutor();
	}
	
	/**
	 * 
	 * @param runnable
	 * @return
	 */
	public static Future<?> submit(Runnable runnable){
		return threadpoolPro.submit(runnable);
	}
	
	/**
	 * 
	 * @param callable
	 * @return
	 */
	public static Future<?> submit(Callable<?> callable){
		return threadpoolPro.submit(callable);
	}
	
	/**
	 * 
	 * @param callable
	 * @return
	 */
	public static void execute(Runnable runnable){
		threadpoolPro.execute(runnable);
	}
	
    /**
     * @param tasks 实现了{@link Runnable}接口的异步任务列表
     * @param timeout 任务执行超时时间
     * @param timeoutUnit 超时时间的单位
     * @throws InterruptedException 
     */
    public static <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, 
            long timeout, TimeUnit timeoutUnit) throws InterruptedException{
        return threadpoolPro.invokeAll(tasks, timeout, timeoutUnit);
    }
	/**
	 * 关闭
	 */
	public static void destroy() {
		threadpoolPro.destroy();	
	}
	
}