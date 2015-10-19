package com.balancing.queue.misc;

import com.balancing.queue.JobQueue;

/**
 * Simply holds some numbers about the rate for 
 * submitting and consuming jobs to the queue.
 *  
 * @author mayata
 *
 */
public class QueueStatistics
{

	// The number of all the submitted jobs to the queue. 
	private long submittedJobs=0;
	
	// The number of all the consumed jobs to the queue.
	private long consumedJobs=0;

	// The number of submitted jobs from the last threshold notification to now. (Still counting)
	private long lastSubmittedJobs=0;

	// The number of submitted jobs from the last threshold notification to current notification.
	private long strictLastSubmittedJobs=0;
	
	// The number of submitted jobs from the last threshold notification to now. (Still counting)
	private long lastConsumedJobs=0;
	
	// The number of submitted jobs from the last threshold notification to current notification.
	private long strictLastConsumedJobs=0;
	
	
	// The number of current jobs in the queue.
	private int currentQueueSize=0;
	
	
	
	public long getSubmittedJobs()
	{
		return submittedJobs;
	}

	public void setSubmittedJobs(long submittedJobs)
	{
		this.submittedJobs = submittedJobs;
	}
	
	/**
	 * Increments submitted and last submitted jobs counter.
	 */
	public void incSubmittedJobs()
	{
		this.submittedJobs++;
		this.lastSubmittedJobs++;
	}

	public long getConsumedJobs()
	{
		return consumedJobs;
	}

	public void setConsumedJobs(long consumedJobs)
	{
		this.consumedJobs = consumedJobs;
	}
	
	/**
	 * Increments consumed jobs counter.
	 */
	public void incConsumedJobs()
	{
		this.consumedJobs++;
		this.lastConsumedJobs++;
	}

	public long getLastSubmittedJobs()
	{
		return lastSubmittedJobs;
	}

	public void setLastSubmittedJobs(long lastSubmittedJobs)
	{
		this.lastSubmittedJobs = lastSubmittedJobs;
	}

	
	public long getStrictLastSubmittedJobs()
	{
		return strictLastSubmittedJobs;
	}

	public void setStrictLastSubmittedJobs(long strictSubmittedJobs)
	{
		this.strictLastSubmittedJobs = strictSubmittedJobs;
	}
	
	public long getLastConsumedJobs()
	{
		return lastConsumedJobs;
	}

	public void setLastConsumedJobs(long lastConsumedJobs)
	{
		this.lastConsumedJobs = lastConsumedJobs;
	}

	public long getStrictLastConsumedJobs()
	{
		return strictLastConsumedJobs;
	}

	public void setStrictLastConsumedJobs(long strictLastConsumedJobs)
	{
		this.strictLastConsumedJobs = strictLastConsumedJobs;
	}

	public int getCurrentQueueSize()
	{
		return currentQueueSize;
	}

	public void resetStrictCounters(){
		strictLastConsumedJobs=0;
		strictLastSubmittedJobs=0;
	}
	
	public void resetLastCounters(){
		lastConsumedJobs=0;
		lastSubmittedJobs=0;
		currentQueueSize=0;
		resetStrictCounters();
	}
	
	
	public void snapshot(JobQueue queue){
		strictLastConsumedJobs = lastConsumedJobs;
		strictLastSubmittedJobs = lastSubmittedJobs;
		this.currentQueueSize = queue.getSize();
	}

	@Override
	public String toString()
	{
		return "QueueStatistics [submittedJobs=" + submittedJobs + ", consumedJobs=" + consumedJobs
				+ ", lastSubmittedJobs=" + lastSubmittedJobs + ", strictLastSubmittedJobs=" + strictLastSubmittedJobs
				+ ", lastConsumedJobs=" + lastConsumedJobs + ", strictLastConsumedJobs=" + strictLastConsumedJobs
				+ ", currentQueueSize=" + currentQueueSize + "]";
	}
	

	
}
