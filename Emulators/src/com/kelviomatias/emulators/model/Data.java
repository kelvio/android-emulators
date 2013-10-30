package com.kelviomatias.emulators.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

@Root(name = "data")
public class Data {

	
	@ElementListUnion({
        @ElementList(type = Console.class, entry = "console", required = false, inline = true)
    })
	private List<Console> consoles = new ArrayList<Console>();

	public List<Console> getConsoles() {
		return consoles;
	}

	public void setConsoles(List<Console> consoles) {
		this.consoles = consoles;
	}
	
	
	
}
