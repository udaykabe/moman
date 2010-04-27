package net.deuce.moman.undo;

import net.deuce.moman.entity.model.EntityProperty;

public interface UndoAdapter {

	public void executeChange(EntityProperty property, Object value);
}
