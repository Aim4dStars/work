package com.bt.nextgen.logon.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ValidatePassword {
	
	private List<String> password;

	public List<String> getPassword() {
		return password;
	}

	public void setPassword(List<String> password) {
		this.password = password;
	}
}
