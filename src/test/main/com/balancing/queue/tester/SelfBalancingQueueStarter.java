package com.balancing.queue.tester;

import com.balancing.queue.controller.SelfBalancingQueue;

public class SelfBalancingQueueStarter
{

	public static <W> void main(String[] args)
	{
		
		SelfBalancingQueue managerThread = new SelfBalancingQueue(
				new JobGenerator<W>(), 
				JobConsumer.class, 
				100, 
				3, 
				500);
		Thread thread = new Thread(managerThread);
		thread.start();
	}

}
