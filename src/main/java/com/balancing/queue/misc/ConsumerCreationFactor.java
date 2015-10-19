package com.balancing.queue.misc;

public enum ConsumerCreationFactor
{
	
	SAFE(1.2),
	WARNING(1.5), 
	CRITICAL(2.0);

	private double level;

	private ConsumerCreationFactor(double level)
	{
		this.level = level;
	}

	public double getLevel()
	{
		return level;
	}
	
}
