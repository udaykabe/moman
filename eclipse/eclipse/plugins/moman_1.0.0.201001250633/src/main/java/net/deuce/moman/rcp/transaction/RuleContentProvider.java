package net.deuce.moman.rcp.transaction;

import java.util.List;

import net.deuce.moman.model.rules.Rule;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RuleContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Rule> rules = (List<Rule>) inputElement;
		return rules.toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
