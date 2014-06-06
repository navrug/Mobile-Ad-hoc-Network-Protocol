package exceptions;

public class WrongMessageType extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public WrongMessageType(){
	    System.out.println("You are trying the operate the wrong kind of message.");
	  }  
	}