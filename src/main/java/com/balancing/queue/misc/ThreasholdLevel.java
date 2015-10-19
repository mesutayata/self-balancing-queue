package com.balancing.queue.misc;

public enum ThreasholdLevel
{
	MIN(0.2),
	SAFE(0.6),
	WARNING(0.7), 
	CRITICAL(0.9);

	private double level;

	private ThreasholdLevel(double level)
	{
		this.level = level;
	}

	public double getLevel()
	{
		return level;
	}
	
}
