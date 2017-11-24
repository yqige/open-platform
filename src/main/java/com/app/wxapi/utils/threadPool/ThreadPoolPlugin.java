package com.app.wxapi.utils.threadPool;

import com.jfinal.plugin.IPlugin;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * JFinal 线程池插件
 * @author farmer
 *
 */
public class ThreadPoolPlugin implements IPlugin{

	final static String DEFAULT = "default";
	//线程池名称
	private String name = DEFAULT;
	//初始线程数
	private int coreSize = 8;
	//最大线程数
	private int maxSize = 32;
	//队列大小
	private int queueSize = 1024;
	//线程保持时间
	private int keepAliveTime = 10;
	//策略
	private RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    };
    public ThreadPoolPlugin() {

    }
	/**
	 * @param name
	 * 线程池名称
	 * @param coreSize
	 * 线程池默认大小
	 * @param maxSize
	 * 最大线程数
	 * @param queueSize
	 * 队列数
	 * @param keepAliveTime
	 * 线程空闲时间
	 */
	public ThreadPoolPlugin(int coreSize , int maxSize , int queueSize ,int keepAliveTime) {
		this(DEFAULT, coreSize, maxSize, queueSize, keepAliveTime);
	}
	
	/**
	 * 
	 * @param coreSize
	 * @param maxSize
	 * @param queueSize
	 * @param keepAliveTime
	 * @param rejectedExecutionHandler
	 */
	public ThreadPoolPlugin(int coreSize , int maxSize , int queueSize ,int keepAliveTime,RejectedExecutionHandler rejectedExecutionHandler) {
		this(DEFAULT, coreSize, maxSize, queueSize, keepAliveTime,rejectedExecutionHandler);
	}
	
	/**
	 * @param name
	 * 线程池名称
	 * @param coreSize
	 * 线程池默认大小
	 * @param maxSize
	 * 最大线程数
	 * @param queueSize
	 * 队列数
	 * @param keepAliveTime
	 * 线程空闲时间
	 */
	public ThreadPoolPlugin(String name , int coreSize , int maxSize , int queueSize ,int keepAliveTime) {
		this(name, coreSize, maxSize, queueSize, keepAliveTime,new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * @param name
	 * @param coreSize
	 * @param maxSize
	 * @param queueSize
	 * @param keepAliveTime
	 * @param rejectedExecutionHandler
	 * 队列满时处理策略
	 */
	public ThreadPoolPlugin(String name , int coreSize , int maxSize , int queueSize ,int keepAliveTime,RejectedExecutionHandler rejectedExecutionHandler) {
		this.name = name;
		this.coreSize = coreSize;
		this.maxSize = maxSize;
		this.queueSize = queueSize;
		this.keepAliveTime = keepAliveTime;
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}

	
	@Override
	public boolean start() {
		ThreadPoolKit.init(name, coreSize, maxSize, queueSize, keepAliveTime, rejectedExecutionHandler);
		return true;
	}

	@Override
	public boolean stop() {
		for(Map.Entry<String,ThreadPoolPro> entry : ThreadPoolKit.threadpoolProMap.entrySet()){
			entry.getValue().destroy();
		}
		return true;
	}

}
