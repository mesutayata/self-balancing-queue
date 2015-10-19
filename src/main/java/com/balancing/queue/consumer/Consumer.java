package com.balancing.queue.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.balancing.queue.Job;
import com.balancing.queue.JobQueue;

public abstract class Consumer implements Runnable
{
	private JobQueue jobQueue = null;
	private boolean exitTask = false;
	
	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	public abstract void doJob(Job job);
	
	
	public void run(){
		while(true){
			Job job = null;
			while((job = jobQueue.pollJob()) != null){
				doJob(job);
			}
			waitNotification();
			if(exitTask){
				break;
			}
		}
		
		logger.info(Thread.currentThread().getName() + "_prev_ stopped . . .");
	}


	private void waitNotification()
	{
		synchronized (this.jobQueue)
		{
			try
			{
				this.jobQueue.wait();
			}
			catch (InterruptedException e)
			{
				logger.error("Exception on sync: ", e);
			}
		}
	}
	
	public void exit(){
		exitTask = true;
	}

	public void setJobQueue(JobQueue jobQueue)
	{
		this.jobQueue = jobQueue;
	}
	
	
	
}
