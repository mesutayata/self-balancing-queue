package com.balancing.queue.util;

import org.apache.log4j.Logger;

/**
 * A checker for the inputs through the jvm.
 * 
 */
public class Validator
{

	private static final Logger logger = Logger.getLogger(Validator.class);

	/**
	 * A private constructor to prevent from non-necessary creation. Because this class is a kind of utility
	 * class which has only static methods.
	 */
	public Validator()
	{

	}

	/**
	 * Returns true if the arguments are valid.
	 * TODO : will be implemented.
	 * 
	 * @param args
	 * @return boolean
	 */
	public boolean validate(String[] args){
		return true;
	}

}
