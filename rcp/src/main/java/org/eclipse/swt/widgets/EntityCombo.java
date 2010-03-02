package org.eclipse.swt.widgets;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.service.EntityService;
import net.deuce.moman.ui.EntityLabelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

@SuppressWarnings("unchecked")
public class EntityCombo<E extends AbstractEntity> extends Combo {
	
	private E entity;

	public EntityCombo(Composite parent, final EntityService<E> service,
			final EntityLabelProvider labelProvider) {
		super(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		setFont(Constants.COMBO_FONT);
		
		final List<E> entities = service.getOrderedEntities(false);
		
		for (E entity : entities) {
			add(labelProvider.getLabel(entity));
		}
		
		addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = getSelectionIndex();
				entity = entities.get(index);
			}
		});
		
		if (entities.size() > 0) {
			select(0);
			entity = entities.get(0);
		}
		
	}

	public E getEntity() {
		return entity;
	}

}