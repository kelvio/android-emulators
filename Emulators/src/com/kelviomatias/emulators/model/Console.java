package com.kelviomatias.emulators.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

@Root(name = "console")
public class Console implements Serializable {

	@ElementListUnion({ @ElementList(type = Emulator.class, entry = "emulator", required = false, inline = true) })
	private List<Emulator> emulators = new ArrayList<Emulator>();

	@Attribute
	private String name;

	public List<Emulator> getEmulators() {
		return emulators;
	}

	public void setEmulators(List<Emulator> emulators) {
		this.emulators = emulators;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
