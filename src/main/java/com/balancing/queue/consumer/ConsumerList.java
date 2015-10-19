package com.balancing.queue.consumer;

import java.util.ArrayList;
import java.util.List;

public class ConsumerList
{
	private List <Consumer> consumerList = new ArrayList <Consumer> (); 
	
	public  int getSize()
	{
		return consumerList.size();
	}

	public Consumer pollLastConsumer()
	{
		Consumer consumer = consumerList.remove(consumerList.size()-1);
		return consumer;
	}
	
	public void submitConsumer(Consumer consumer)
	{
		this.consumerList.add(consumer);
	}

}
