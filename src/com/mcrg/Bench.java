package com.mcrg;

public class Bench {
	PSO pso;
	
	public Bench(PSO pso) {
		this.pso = pso;
	}
	
	public double[] startBench(int rounds) throws WrongParameterException {
		double[] results = new double[rounds];
		for (int r = 0; r < rounds; r++) {
			PSO current = this.pso;
			current.initPSO();
			results[r] = current.runPSO();
		}
		return results;
	}
}
