package net.deuce.moman.ui;

import net.deuce.moman.entity.model.AbstractEntity;

@SuppressWarnings("unchecked")
public interface EntityLabelProvider<E extends AbstractEntity> {
	public String getLabel(E entity);
}
