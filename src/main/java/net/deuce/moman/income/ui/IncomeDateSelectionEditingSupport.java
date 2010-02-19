package net.deuce.moman.income.ui;

import java.util.Date;

import net.deuce.moman.income.model.Income;
import net.deuce.moman.ui.DateSelectionEditingSupport;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

public class IncomeDateSelectionEditingSupport extends DateSelectionEditingSupport {
	
	public IncomeDateSelectionEditingSupport(ColumnViewer viewer, Composite parent) {
		super(viewer, parent);
	}

	protected void doSetValue(Object element, Object value) {
		Income income = (Income)element;
		income.setNextPayday((Date)value);
	}

	@Override
	protected Object doGetValue(Object element) {
		return ((Income)element).getNextPayday();
	}

}
