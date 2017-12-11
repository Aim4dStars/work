package com.bt.nextgen.core.jms;

public class ListenerNotRunning extends Exception{

	ListenerNotRunning(){}
	
	public ListenerNotRunning(String message) {
        super(message);
    }
}
