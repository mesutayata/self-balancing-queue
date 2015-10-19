package com.balancing.queue.tester;

import com.balancing.queue.Job;

public class RealJob<W> implements Job
{
	
	private W work;
	
	public W getWork(){
		return work;
	}	
	
	public void setWork(W work){
		this.work=work;
	}
	
	@Override
	public boolean doWork()
	{
		try{
			System.out.println(Thread.currentThread().getName() + " DONE: " + getWork() );
			Thread.sleep(30);	
		}catch(Exception e){
			System.out.println("Error in working " + e.getMessage());
		}
		return true;
	}
	
	
}
