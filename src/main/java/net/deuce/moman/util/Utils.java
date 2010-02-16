package net.deuce.moman.util;

import java.text.NumberFormat;
import java.text.ParseException;

import net.deuce.moman.Constants;

public class Utils {
	
	private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	private static NumberFormat DOUBLE_FORMAT;
	
	static {
		DOUBLE_FORMAT = NumberFormat.getNumberInstance();
		DOUBLE_FORMAT.setMaximumFractionDigits(2);
		DOUBLE_FORMAT.setGroupingUsed(false);
	}

	public static boolean validateCurrency(String value) {
		if (Constants.CURRENCY_VALIDATOR.isValid(value)) {
			return true;
		}
		try {
			CURRENCY_FORMAT.parse(value);
			return true;
		} catch (ParseException e) {
		}
		return false;
	}

	public static Double parseCurrency(String value) {
		if (Constants.CURRENCY_VALIDATOR.isValid(value)) {
			return Constants.CURRENCY_VALIDATOR.validate(value).doubleValue();
		}
		try {
			return CURRENCY_FORMAT.parse(value).doubleValue();
		} catch (ParseException e) {
		}
		return null;
	}
	
	public static String formatDouble(Double value) {
		if (value == null) return null;
		return DOUBLE_FORMAT.format(value);
	}
}
