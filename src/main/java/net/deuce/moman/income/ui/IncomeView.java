package net.deuce.moman.income.ui;

import java.util.Date;

import net.deuce.moman.Constants;
import net.deuce.moman.income.command.Delete;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.DateSelectionDialog;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class IncomeView extends AbstractEntityTableView<Income> {
	
	public static final String ID = IncomeView.class.getName();
	
	public IncomeView() {
		super(ServiceNeeder.instance().getIncomeService());
	}
	
	@Override
	protected TableViewer createTableViewer(Composite parent) {
 		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
 		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Name");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Frequency");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 3));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Next Payday");
 	    column.getColumn().setWidth(100);
 	    
	    tableViewer.setContentProvider(new IncomeContentProvider());
	    tableViewer.setLabelProvider(new IncomeLabelProvider());
		return tableViewer;
	}

	@Override
	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
		Income income = (Income)selection.getFirstElement();
		
		DateSelectionDialog dialog = new DateSelectionDialog(shell, income.getNextPayday());
		dialog.open();
		Date date = dialog.getDate();
        if (date != null) {
        	income.setNextPayday(date);
        }
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{4};
	}

}
