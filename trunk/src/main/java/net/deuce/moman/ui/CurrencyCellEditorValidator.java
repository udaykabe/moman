package net.deuce.moman.ui;


import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.jface.viewers.ICellEditorValidator;

public class CurrencyCellEditorValidator implements ICellEditorValidator {
	
	private static CurrencyCellEditorValidator __instance = new CurrencyCellEditorValidator();
	
	private DecimalFormat format = new DecimalFormat();
	
	private CurrencyCellEditorValidator() {
		format.setGroupingUsed(true);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(0);
	}
	
	public static CurrencyCellEditorValidator instance() {
		return __instance;
	}

	@Override
	public String isValid(Object value) {
		if ( !(value instanceof String) ) {
			return "Not a valid value type: " + value.getClass().getName();
		}
		
		try {
			format.parse((String)value);
		} catch (ParseException e) {
			return "Please enter a currency value";
		}
		return null;
	}

}
