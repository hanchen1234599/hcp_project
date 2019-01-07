package share.server.service;

import java.util.HashMap;

public class GateMap {
	private HashMap<Integer, Gate> gates = new HashMap<>();

	public HashMap<Integer, Gate> getGates() {
		return gates;
	}

	public void setGates(HashMap<Integer, Gate> gates) {
		this.gates = gates;
	}
}
