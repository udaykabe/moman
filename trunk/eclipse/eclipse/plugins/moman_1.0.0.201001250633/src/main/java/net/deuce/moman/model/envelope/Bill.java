package net.deuce.moman.model.envelope;


public class Bill extends Envelope {
	
	private static final long serialVersionUID = 1L;

	private boolean enabled;
	private int dueDay;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			getMonitor().fireEntityChanged(this);
		}
	}
	public int getDueDay() {
		return dueDay;
	}
	public void setDueDay(int dueDay) {
		if (this.dueDay != dueDay) {
			this.dueDay = dueDay;
			getMonitor().fireEntityChanged(this);
		}
	}
	public float getAmount() {
		return getBudget();
	}
	public void setAmount(float amount) {
		setBudget(amount);
	}
	@Override
	public int compareTo(Envelope env) {
		if (env instanceof Bill) {
			Bill bill = (Bill)env;
			if (!this.getFrequency().equals(bill.getFrequency())) {
				return this.getFrequency().compareTo(bill.getFrequency());
			}
			return new Integer(this.dueDay).compareTo(new Integer(bill.getDueDay()));
		}
		return super.compareTo(env);
	}
	
}	