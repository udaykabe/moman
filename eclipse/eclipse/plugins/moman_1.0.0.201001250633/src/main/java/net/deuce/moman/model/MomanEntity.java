package net.deuce.moman.model;

public class MomanEntity {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("%s %s", getClass().getSimpleName(), id);
	}
	
	protected boolean propertyChanged(Object s1, Object s2) {
		if (s1 == s2) return false;
		return s1 != s2 && ( (s1 != null && !s1.equals(s2)) || (s2 != null && !s2.equals(s1)) );
	}
}
