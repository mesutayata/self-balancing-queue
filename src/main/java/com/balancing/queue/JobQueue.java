package com.balancing.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.balancing.queue.misc.QueueStatistics;
import com.balancing.queue.misc.ThreasholdLevel;

public class JobQueue
{
	
	private static final Logger logger = LoggerFactory.getLogger(JobQueue.class);
	
	private LinkedBlockingQueue <Job> queue = null;
	private Object freeSpaceSyncObject = null;
	private Object managerThreadSyncObject = null;
	private QueueStatistics statistics;
	private int capacity = 0;
	
	
	public JobQueue(int capacity, Object managerThreadSyncObject)
	{
		queue = new LinkedBlockingQueue<Job>(capacity);
		statistics = new QueueStatistics();
		freeSpaceSyncObject  = new Object();
		
		this.managerThreadSyncObject=managerThreadSyncObject;
		this.capacity=capacity;
	}

	public boolean submit(Job job){
		
		// Try adding job to the queue.
		boolean result = queue.offer(job);
		
		if(!result){
			/*
			 * The queue is already full, handle it differently.
			 * Normally this is less hard probability to occur because 
			 * queue is already being checked in different threshold levels.
			 * However, here we ensure that there is no job lost.
			 */
			result = handleFullQueue(job);
		}else{
			// increase the submitted jobs by one.
			statistics.incSubmittedJobs();
		}
		
		// checks whether the queue is in safe levels.
		checkForSafe();
		
		/*
		 * notifies consumers because there may be consumers
		 * waiting for a new job to enter the queue. 
		 */
		notifyConsumers();
		
		return result;
	}

	/*
	 * Notifies consumers because there may be consumers
	 * waiting for a new job to enter the queue. 
	 * Note that all consumers are synchronized among this object.
	 */
	private void notifyConsumers()
	{
		synchronized (this)
		{
			this.notifyAll();			
		}
	}

	/*
	 * checks whether the queue is in safe levels.
	 */
	private void checkForSafe()
	{
		if(this.getSize() == (ThreasholdLevel.SAFE.getLevel() * this.getCapacity())){
			statistics.snapshot(this);
			notifyManagerThread();
		}else if( this.getSize() == (ThreasholdLevel.WARNING.getLevel() * this.getCapacity())){
			statistics.snapshot(this);
			notifyManagerThread();
		}else if(this.getSize() == (ThreasholdLevel.CRITICAL.getLevel() *this.getCapacity())){
			statistics.snapshot(this);
			notifyManagerThread();
		}
		
	}

	/*
	 * Handles the job submit request when queue is full.
	 * 
	 * Notifies the main thread to monitor and get some actions,
	 * Then wait for the queue to be emptied for some space.
	 * 
	 * Normally this is less hard probability to occur because 
	 * queue is already being checked in different threshold levels.
	 * However, here we ensure that there is no job lost.
	 */
	private boolean handleFullQueue(Job job)
	{
		boolean result;
		logger.warn("Queue is full, waiting for space");
		
		statistics.snapshot(this);
		notifyManagerThread();
		waitForSpace();
		result = queue.offer(job);
		if(result){
			logger.info("Waiting message submitted now");
		}else{
			logger.error("Could not submitted message, job=" + job.toString());
		}
		return result;
	}
	
	/*
	 * The queue is already full, handle it differently.
	 * Normally this is less hard probability to occur because 
	 * queue is already being checked in different threshold levels.
	 * However, here we ensure that there is no job lost.
	 * 
	 */
	private void waitForSpace()
	{
		synchronized (freeSpaceSyncObject)
		{
			try
			{
				freeSpaceSyncObject.wait();
			}
			catch (InterruptedException e)
			{
				logger.error("Exception while waiting. ", e);
			}				
		}
	}
	
	/*
	 * Polls a job from the queue.
	 * Synchronized because many consumers tries to 
	 * poll a job at the same time. And we want each job to be done once.
	 * 
	 * If the queue below the critical level, it just notifies 
	 * for free space. This notification is only useful when the queue 
	 * job submission is locked, because queue is full, which is very unlikely. 
	 */
	public synchronized Job pollJob(){
		if(this.getSize() < (ThreasholdLevel.CRITICAL.getLevel()
				*this.getCapacity())){
			notifyFreeSpace();
		}
		// Increase the number of consumed jobs.
		statistics.incConsumedJobs();
		return queue.poll();
	}


	public LinkedBlockingQueue<Job> getQueue()
	{
		return queue;
	}
	
	public int getSize(){
		return queue.size();
	}

	public long getCapacity()
	{
		return capacity;
	}
	
	/*
	 * Notifies the queue for free space so that it can accept 
	 * new job submissions.
	 */
	public void notifyFreeSpace(){
		synchronized(freeSpaceSyncObject){
			freeSpaceSyncObject.notify();	
		}
	}

	/*
	 * Notifies manager thread for an event. 
	 * This can be fired after i.e. a threshold level exceeded. 
	 * 
	 * @See ManagerThread
	 */
	public void notifyManagerThread(){
		synchronized(managerThreadSyncObject){
			managerThreadSyncObject.notify();	
		}
	}

	/*
	 * Returns the statistics object.
	 * It includes the information on how frequently 
	 * the queue is used both by producer and consumers.
	 * 
	 * @See QueueStatistics
	 */
	public QueueStatistics getStatistics(){
		return statistics;
	}
	
	
}


