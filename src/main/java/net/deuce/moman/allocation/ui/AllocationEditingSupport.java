package net.deuce.moman.allocation.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AmountType;
import net.deuce.moman.allocation.model.LimitType;
import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.ui.CurrencyCellEditorValidator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class AllocationEditingSupport extends EditingSupport {
	
	private CellEditor editor;
	private int column;
	private EntityMonitor<Allocation> allocationMonitor = new EntityMonitor<Allocation>();

	public AllocationEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		
		String[] values;
		
		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.CHECK);
			break;
		case 1:
		case 4:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			editor.getControl().setFont(Constants.STANDARD_FONT);
			editor.setValidator(CurrencyCellEditorValidator.instance());
			break;
		case 2:
			values = new String[AmountType.values().length];
			for (int i=0; i<AmountType.values().length; i++) {
				values[i] = AmountType.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		case 5:
			values = new String[LimitType.values().length];
			for (int i=0; i<LimitType.values().length; i++) {
				values[i] = LimitType.values()[i].label();
			}
			editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
			editor.getControl().setFont(Constants.STANDARD_FONT);
			break;
		default:
			editor = null;
		}
		this.column = column;
	}

	public EntityMonitor<Allocation> getAllocationMonitor() {
		return allocationMonitor;
	}

	public void setAllocationMonitor(EntityMonitor<Allocation> allocationMonitor) {
		this.allocationMonitor = allocationMonitor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Allocation allocation = (Allocation)element;
	
		switch (this.column) {
		case 0: return allocation.isEnabled();
		case 1: 
			AmountType type = allocation.getAmountType();
			if (type == AmountType.FIXED || type == AmountType.REMAINDER) {
				return Constants.CURRENCY_VALIDATOR.format(allocation.getAmount());
			}
			return Constants.PERCENT_VALIDATOR.format(allocation.getAmount());
		case 2: return allocation.getAmountType().ordinal();
		case 4:
			LimitType lType = allocation.getLimitType();
			if (lType == LimitType.DEPOSIT_PERCENT) {
				return Constants.PERCENT_VALIDATOR.format(allocation.getLimit());
			}
			return Constants.CURRENCY_VALIDATOR.format(allocation.getLimit());
		case 5: return allocation.getLimitType().ordinal();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
		if (value != null) {

			Allocation allocation = (Allocation)element;
			
			switch (this.column) {

			case 0: allocation.executeChange(Allocation.Properties.enabled, value); break;
			case 1: 
				AmountType type = allocation.getAmountType();
				Double amount = null;
				if (type == AmountType.FIXED || type == AmountType.REMAINDER) {
					amount = Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue();
				} else if (Constants.PERCENT_VALIDATOR.validate((String)value).doubleValue() <= 1.0) {
					amount = Constants.PERCENT_VALIDATOR.validate((String)value).doubleValue();
				}
				if (amount != null) {
					allocation.executeChange(Allocation.Properties.amount, amount);
					allocationMonitor.fireEntityChanged(allocation, Allocation.Properties.amount);
				}
				break;
			case 2: 
				amount = null;
				allocation.executeChange(Allocation.Properties.amountType, AmountType.values()[(Integer)value]);
				if (allocation.getAmount() > 1.0 && (allocation.getAmountType() == AmountType.DEPOSIT_PERCENT ||
						allocation.getAmountType() == AmountType.REMAINDER_PERCENT)) {
					amount = 1.0;
				} else if (allocation.getAmountType() == AmountType.REMAINDER) {
					amount = 0.0;
				}
				if (amount != null) {
					allocation.executeChange(Allocation.Properties.amount, amount);
				}
				break;
			case 4:
				LimitType lType = allocation.getLimitType();
				Double limit;
				if (lType == LimitType.DEPOSIT_PERCENT && Constants.PERCENT_VALIDATOR.validate((String)value).doubleValue() <= 1.0) {
					limit = Constants.PERCENT_VALIDATOR.validate((String)value).doubleValue();
				} else {
					limit = Constants.CURRENCY_VALIDATOR.validate((String)value).doubleValue();
				}
				allocation.executeChange(Allocation.Properties.limit, limit);
				allocationMonitor.fireEntityChanged(allocation, Allocation.Properties.limit);
				break;
			case 5:
				limit = null;
				allocation.executeChange(Allocation.Properties.limitType, LimitType.values()[(Integer)value]);
				if (allocation.getLimit() > 1.0 && allocation.getLimitType() == LimitType.DEPOSIT_PERCENT) {
					limit = 1.0;
				} else if (allocation.getLimitType() == LimitType.NONE) {
					limit = 0.0;
				}
				if (limit != null) {
					allocation.executeChange(Allocation.Properties.limit, limit);
				}
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
