package net.deuce.moman.allocation.model;

import java.util.HashMap;
import java.util.Map;

public enum AmountType {
	FIXED("Fixed"), REMAINDER("Remainder"), DEPOSIT_PERCENT("Percent of deposit"),
			REMAINDER_PERCENT("Percent of remainder");
			
	private String label;
	
	private static Map<String, AmountType> cache = new HashMap<String, AmountType>();
	
	static {
		cache.put(FIXED.label, FIXED);
		cache.put(REMAINDER.label, REMAINDER);
		cache.put(DEPOSIT_PERCENT.label, DEPOSIT_PERCENT);
		cache.put(REMAINDER_PERCENT.label, REMAINDER_PERCENT);
	}
	
	public static AmountType fromLabel(String label) {
		return cache.get(label);
	}
	
	private AmountType(String label) {
		this.label = label;
	}
	
	public String label() {
		return label;
	}
}