package net.deuce.moman.rule.ui;

import java.util.List;

import net.deuce.moman.entity.model.rule.Rule;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RuleContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Rule> rules = (List<Rule>) inputElement;
		return rules.toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
