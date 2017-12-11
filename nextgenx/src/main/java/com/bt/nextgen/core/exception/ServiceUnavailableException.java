package com.bt.nextgen.core.exception;

/**
 * The ServiceUnavailableException is thrown if the requested service is unavailable temporarily. 
 */
public class ServiceUnavailableException extends ServiceException 
{

  private int retryTime = -1;

  public ServiceUnavailableException() 
  {
  	super();
  }
  
  public ServiceUnavailableException(String message) 
  {
    super(message);
  }

  public ServiceUnavailableException(String message, Throwable cause) 
  {
    super(message, cause);
  }

  public ServiceUnavailableException(Throwable cause)
  {
    super(cause.getMessage(), cause);
  }

  public int getRetryTime() 
  {
    return retryTime;
  }

  public void setRetryTime(int retryTime) 
  {
    this.retryTime = retryTime;
  }

}