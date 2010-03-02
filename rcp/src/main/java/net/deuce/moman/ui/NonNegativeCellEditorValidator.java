package net.deuce.moman.ui;


import org.eclipse.jface.viewers.ICellEditorValidator;

public class NonNegativeCellEditorValidator implements ICellEditorValidator {
	
	private static NonNegativeCellEditorValidator __instance = new NonNegativeCellEditorValidator();
	
	private NonNegativeCellEditorValidator() {
	}
	
	public static NonNegativeCellEditorValidator instance() {
		return __instance;
	}

	@Override
	public String isValid(Object value) {
		if ( !(value instanceof String) ) {
			return "Not a valid value type: " + value.getClass().getName();
		}
		
		if (!((String)value).matches("^[1-9][0-9]*$")) {
			return "Please enter a non zero integer value";
		}
		return null;
	}

}
