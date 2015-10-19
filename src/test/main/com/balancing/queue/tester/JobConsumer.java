package com.balancing.queue.tester;

import com.balancing.queue.Job;
import com.balancing.queue.consumer.Consumer;

public class JobConsumer<W> extends Consumer
{

	@Override
	public void doJob(Job job)
	{
		job.doWork();
		W work = (W)((RealJob)job).getWork();
		System.out.println("DOING..." + work.toString());
		
	}

}
