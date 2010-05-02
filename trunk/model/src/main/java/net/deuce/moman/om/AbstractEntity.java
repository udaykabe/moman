package net.deuce.moman.om;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Comparator;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public abstract class AbstractEntity<E> implements Comparator<E>, Comparable<E> {

	private Long id;
  private String uuid;
	
	private transient Comparator<E> forwardComparator;
	private transient Comparator<E> reverseComparator;
	
	public AbstractEntity() {
	}
	
	public boolean evaluateBoolean(Boolean b) {
		return b != null && b;
	}
	
	public Comparator<E> getForwardComparator() {
		if (forwardComparator == null) {
			forwardComparator = new Comparator<E>() {
				
				public int compare(E o1, E o2) {
					return AbstractEntity.this.compare(o1, o2);
				}
			};
		}
		return forwardComparator;
	}
	
	public Comparator<E> getReverseComparator() {
		if (reverseComparator == null) {
			reverseComparator = new Comparator<E>() {
				
				public int compare(E o1, E o2) {
					return -AbstractEntity.this.compare(o1, o2);
				}
			};
		}
		return reverseComparator;
	}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String toString() {
		return String.format("%s %s", getClass().getSimpleName(), id);
	}
	
	protected boolean propertyChanged(Object s1, Object s2) {
		if (s1 == s2) return false;
		return ( (s1 != null && !s1.equals(s2)) || (s2 != null && !s2.equals(s1)) );
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity<E> other = (AbstractEntity<E>) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	protected String getRootName() {
		String name = getClass().getSimpleName();
		return name.substring(0,1).toLowerCase() + name.substring(1);
	}

  protected int compareObjects(Comparable o1, Comparable o2) {
    if (o1 != null && o2 == null) return -1;
    if (o1 == null && o2 != null) return 1;
    if (o1 == null && o2 == null) return 0;
    return o1.compareTo(o2);
  }
	
}
