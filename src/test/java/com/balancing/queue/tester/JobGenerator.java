package com.balancing.queue.tester;

import java.util.Random;

import com.balancing.queue.Job;
import com.balancing.queue.producer.JobSubmitter;

public class JobGenerator<W> extends JobSubmitter
{

	private int count = 1;
	private int delay = 10;
	Random rand = null;
	public JobGenerator()
	{ 
		rand = new Random();
	}

	@Override
	public boolean sendJob()
	{
		try{
			
			RealJob<W> work = new RealJob<W> ();
			String data = "JOB_"+count;
			work.setWork((W)data);
			super.submit(work);
			count++;
			if(count == 2000){
				delay = 3;
			}
			Thread.sleep(Long.valueOf(""+rand.nextInt(delay)));	
			System.out.println(Thread.currentThread().getName() + " " + data);
			
		}catch(Exception e){
			System.out.println("Error in working " + e.getMessage());
		}
		return true;
	}

	@Override
	public void run()
	{
		while(true){
			if(!sendJob()){
				break;
			}
		}
	
	}

	
	
	
}
