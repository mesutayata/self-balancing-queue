package com.balancing.queue.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.balancing.queue.JobQueue;
import com.balancing.queue.consumer.Consumer;
import com.balancing.queue.consumer.ConsumerList;
import com.balancing.queue.misc.ConsumerCreationFactor;
import com.balancing.queue.misc.QueueStatistics;
import com.balancing.queue.misc.ThreasholdLevel;
import com.balancing.queue.producer.JobSubmitter;

public class SelfBalancingQueue  implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(SelfBalancingQueue.class);
	private static final int DEFAULT_CAPACITY = 10000;
	
	private int maxConsumersSize;
	private int minConsumersSize;
	private ConsumerList consumerList = null;
	private JobQueue jobQueue = null;
	private JobSubmitter jobSubmitter = null;
	private Class consumerClass = null;
	private Object managerThreadSyncObject = null;

	
	public SelfBalancingQueue(JobSubmitter jobSubmitter, 
			Class consumerClass, 
			int maxConsumersSize, 
			int minConsumersSize, 
			int capacity)
	{
		super();
		managerThreadSyncObject = new Object();
		jobQueue = new JobQueue(capacity, managerThreadSyncObject); 
		this.consumerClass = consumerClass;
		this.maxConsumersSize = maxConsumersSize;
		this.minConsumersSize = minConsumersSize;
		this.jobSubmitter=jobSubmitter;
		this.jobSubmitter.setJobQueue(jobQueue);
	}


	@Override
	public void run()
	{
		initializeThreads();
		
		while(true){
			waitforQueueNotifications();
			check();
		}
	}


	private void initializeThreads()
	{
		consumerList = new ConsumerList();
		for(int i=0;i<minConsumersSize;i++){
			submitConsumer(jobQueue);
		}

		Thread thread = new Thread(this.jobSubmitter);
		thread.setName("Producer");
		thread.start();
	}

	
	public void submitConsumer(JobQueue jobQueue){
		Consumer consumer = null;
		try
		{
			consumer = (Consumer) this.consumerClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			logger.error("Consumer creation got exception: ", e);
		}
		consumer.setJobQueue(jobQueue);
		logger.info("Submitting a new consumer..., new size: " + (consumerList.getSize()+1));
		Thread thread = new Thread(consumer);
		thread.setName("Consumer_"+consumerList.getSize());
		thread.start();
		consumerList.submitConsumer(consumer);
	}
	
	public void stopConsumer(){
		logger.info("Stopping a consumer..., remaining size: " + (consumerList.getSize()-1));
		((Consumer)consumerList.pollLastConsumer()).exit();
	}
	
	

	private void waitforQueueNotifications()
	{
		synchronized (managerThreadSyncObject)
		{
			try
			{
				managerThreadSyncObject.wait();
			}
			catch (InterruptedException e)
			{
				logger.error("Got exception while waiting on managerThreadSyncObject", e);
			}
		}
	}


	private void check()
	{
		QueueStatistics statistics = getJobQueue().getStatistics();
		int queueSize = statistics.getCurrentQueueSize();
		long consumedJobs = statistics.getStrictLastConsumedJobs();
		long submittedJobs = statistics.getStrictLastSubmittedJobs();
		int currentConsumerCount = getConsumerList().getSize();
		logger.info(statistics.toString());
		double coFactor = findCoFactor(queueSize, submittedJobs, consumedJobs);
		createConsumers(queueSize, currentConsumerCount, coFactor);

//		if((jobQueue.getSize() < (MIN_LEVEL*DEFAULT_CAPACITY)) && 
//				(consumerList.getSize()>minConsumersSize) && 
//				(consumerList.getSize()>(statistics.getAverage() - jobQueue.getCapacity()*0.1))){
//			stopConsumer();
//		}
	
	}


	private double findCoFactor(int queueSize, long submittedJobs, long consumedJobs) {

		double factor=1.0, coFactor = 1.0;
		if(submittedJobs == 0 && consumedJobs == 0){
			factor = 1;
		}else if(submittedJobs == 0){
			submittedJobs = 1;
		}
		factor = ( ((double)consumedJobs) / ((double)submittedJobs) );
		logger.info("Consuming/producing ratio: " + factor);
		if(factor != 0){
			coFactor = 1/factor;			
		}
		
		if(queueSize >= (ThreasholdLevel.CRITICAL.getLevel() * getJobQueue().getCapacity())){
			logger.warn("Queue size is above critical level");
			if(coFactor < ConsumerCreationFactor.CRITICAL.getLevel()){
				coFactor = ConsumerCreationFactor.CRITICAL.getLevel() ;
			}
		} else if(queueSize >= (ThreasholdLevel.WARNING.getLevel() * getJobQueue().getCapacity())){
			logger.warn("Queue size is above warning level");
			if(coFactor < ConsumerCreationFactor.WARNING.getLevel()){
				coFactor = ConsumerCreationFactor.WARNING.getLevel() ;
			}
		} else if(queueSize >= (ThreasholdLevel.SAFE.getLevel() * getJobQueue().getCapacity())){
			logger.warn("Queue size is above safe level");
			if(coFactor < ConsumerCreationFactor.SAFE.getLevel()){
				coFactor = ConsumerCreationFactor.SAFE.getLevel() ;
			}
		}else {
			logger.info("Queue size is in the safe band.");
		}
		
		return coFactor;
	}


	private void createConsumers(int queueSize,
			int currentConsumerCount, double coFactor) {
		
		if(currentConsumerCount==maxConsumersSize){
			logger.warn("Can not create new consumer, Max Consumers count already reached..");	
		}else{
			
			int newConsumersCount = ((int)(coFactor*currentConsumerCount-currentConsumerCount));
			
			if((newConsumersCount	+	currentConsumerCount) > maxConsumersSize){
				newConsumersCount = maxConsumersSize - currentConsumerCount;
			}

			if(newConsumersCount > 0 ){
				getJobQueue().getStatistics().resetLastCounters();
				logger.info("Submitting " + newConsumersCount + " Consumers.");
				for(int i=0; i < newConsumersCount; i++){
					submitConsumer(jobQueue);	
				}
			}	
		}
	}


	public int getMaxConsumersSize() {
		return maxConsumersSize;
	}


	public int getMinConsumersSize() {
		return minConsumersSize;
	}


	public ConsumerList getConsumerList() {
		return consumerList;
	}


	public JobQueue getJobQueue() {
		return jobQueue;
	}



	public Class getConsumerClass() {
		return consumerClass;
	}


	
}
