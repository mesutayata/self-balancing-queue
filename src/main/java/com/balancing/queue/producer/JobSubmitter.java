package com.balancing.queue.producer;

import com.balancing.queue.Job;
import com.balancing.queue.JobQueue;

public abstract class JobSubmitter implements Runnable 
{


	private JobQueue jobQueue = null;


	public JobSubmitter(){
		super();
	}
	
	public void setJobQueue(JobQueue jobQueue)
	{
		this.jobQueue = jobQueue;
	}

	public boolean submit(Job job){
		return this.jobQueue.submit(job); 
	}
	
	public abstract boolean sendJob();
	
	
}
