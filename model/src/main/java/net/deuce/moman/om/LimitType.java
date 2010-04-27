package net.deuce.moman.om;

import java.util.HashMap;
import java.util.Map;

public enum LimitType {
	NONE("None"), FIXED("Fixed amount"), DEPOSIT_PERCENT("Percent of deposit"),
			TARGET_ENVELOPE_BALANCE("Target envelope balance");
			
	private String label;
	
	private static Map<String, LimitType> cache = new HashMap<String, LimitType>();
	
	static {
		cache.put(NONE.label, NONE);
		cache.put(FIXED.label, FIXED);
		cache.put(DEPOSIT_PERCENT.label, DEPOSIT_PERCENT);
		cache.put(TARGET_ENVELOPE_BALANCE.label, TARGET_ENVELOPE_BALANCE);
	}
	
	public static LimitType fromLabel(String label) {
		return cache.get(label);
	}
	
	private LimitType(String label) {
		this.label = label;
	}
	
	public String label() {
		return label;
	}
}