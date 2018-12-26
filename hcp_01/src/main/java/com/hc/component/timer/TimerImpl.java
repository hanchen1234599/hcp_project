package com.hc.component.timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hc.share.util.Trace;

public class TimerImpl implements Timer {
	private ScheduledExecutorService timerExec = null;
	private ConcurrentHashMap<String, ScheduledFuture<?>> tasks = null;
	
	public TimerImpl( int nThread ) {
		timerExec = Executors.newScheduledThreadPool(nThread);
		tasks = new ConcurrentHashMap<>();
	}
	/**
	 *  添加单次定时器
	 * @param name 定时器名字
	 * @param run 定时器到时执行内容
	 * @param milliseSecond 定时器时间
	 */
	public void registerOnceTask( String name, Runnable run, long milliseSecond ) {
		this.tasks.put(name, timerExec.schedule(()->{
			tasks.remove(name);
			run.run();
		}, milliseSecond, TimeUnit.MILLISECONDS));
	}
	/**
	 *  添加单次定时器
	 * @param name 定时器名字
	 * @param run 定时器到时执行内容
	 * @param milliseSecond 定时器时间
	 * @param exec 内容执行器
	 */
	public void registerOnceTask( String name, Runnable run, long milliseSecond, ExecutorService exec ) {
		this.tasks.put(name, timerExec.schedule(()->{
			this.tasks.remove(name);
			try {
				exec.execute(run);
			} catch (Exception e) {
				Trace.warn(name + "定时器内执行异常" + e);
			}
		}, milliseSecond, TimeUnit.MILLISECONDS));
	}
	/**
	 * 添加循环定时器
	 * @param name 定时器名字
	 * @param run 定时器到时执行内容
	 * @param milliseSecond 定时器时间
	 */
	public void registerLoopTask(String name, Runnable run, long milliseSecond) {
		this.tasks.put(name, timerExec.scheduleAtFixedRate(()->{
			run.run();
		}, milliseSecond, milliseSecond, TimeUnit.MILLISECONDS));
	}
	/**
	 * 添加循环定时器
	 * @param name 定时器名称
	 * @param run 定时器到时执行内容
	 * @param milliseSecond 定时器时间
	 * * @param exec 内容执行器
	 */
	public void registerLoopTask(String name, Runnable run, long milliseSecond, ExecutorService exec) {
		this.tasks.put(name, timerExec.scheduleAtFixedRate(()->{
			try {
				exec.execute(run);
			} catch (Exception e) {
				Trace.warn(name + "定时器内执行异常" + e);
			}
		}, milliseSecond, milliseSecond, TimeUnit.MILLISECONDS));
	}
	/**
	 * 取消定时器
	 * @param name 定时器名称
	 */
	public void cancleTask(String name) {
		ScheduledFuture<?> future = this.tasks.get(name);
		if(future != null) {
			future.cancel(false);
		}
	}
}

