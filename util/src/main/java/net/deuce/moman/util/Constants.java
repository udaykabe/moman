package net.deuce.moman.util;

import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.PercentValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {

	public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
	public static final CurrencyValidator CURRENCY_VALIDATOR = new CurrencyValidator(false, true);
	public static final PercentValidator PERCENT_VALIDATOR = new PercentValidator(false);
	
}
