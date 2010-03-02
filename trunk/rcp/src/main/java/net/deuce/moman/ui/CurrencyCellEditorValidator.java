package net.deuce.moman.ui;


import net.deuce.moman.Constants;

import org.eclipse.jface.viewers.ICellEditorValidator;

public class CurrencyCellEditorValidator implements ICellEditorValidator {
	
	private static CurrencyCellEditorValidator __instance = new CurrencyCellEditorValidator();
	
	private CurrencyCellEditorValidator() {
	}
	
	public static CurrencyCellEditorValidator instance() {
		return __instance;
	}

	@Override
	public String isValid(Object value) {
		if ( !(value instanceof String) ) {
			return "Not a valid value type: " + value.getClass().getName();
		}
		
		if (!Constants.CURRENCY_VALIDATOR.isValid((String)value)) {
			return "Please enter a currency value";
		}
		
		if (Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue() < 0) {
			return "Please enter a non-negative value";
		}
		return null;
	}

}
