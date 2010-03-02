package net.deuce.moman.report;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.transaction.model.InternalTransaction;

public class DataSetResult {

	private List<Double> result = new LinkedList<Double>();
	private double minValue = Double.MAX_VALUE;
	private double maxValue = 0.0;
	private List<List<InternalTransaction>> dataPointTransactions = new LinkedList<List<InternalTransaction>>();
	
	public DataSetResult(List<List<InternalTransaction>> dataPointTransactions,
			List<Double> result, double minValue, double maxValue) {
		super();
		if (dataPointTransactions != null) {
			this.dataPointTransactions.addAll(dataPointTransactions);
		}
		this.result.addAll(result);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public List<InternalTransaction> getDataPointTransactions(int index) {
		if (index < dataPointTransactions.size()) {
			return dataPointTransactions.get(index);
		}
		return null;
	}

	public List<Double> getResult() {
		return result;
	}

	public void setResult(List<Double> result) {
		this.result = result;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

}
