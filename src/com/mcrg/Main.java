package com.mcrg;

import java.io.IOException;

import static java.lang.System.out;

public class Main {
	
	public static void main(String[] args) throws WrongParameterException, IOException {
		
		// Default settings
		int PSOiterations = 1000;    // nof iterations of PSO
		int benchRounds = 100;
		int runs = 1;
		int numParticles = 1000;
		int numDim = 3;
		double freq = 0.0;
		double nR = 0.0;
		double rangeRatio = 0.0;
		String function = "squaresum";
		boolean log = false;
		
		try {
			// Select parameters
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-f") || args[i].equals("-function")) {
					function = args[i + 1];
				}
				if (args[i].equals("-v") || args[i].equals("-verbose")) {
					log = true;
				}
				if (args[i].equals("-b") || args[i].equals("-bench")) {
					runs = 2;
					benchRounds = Integer.parseInt(args[i + 1]);
				}
				if (args[i].equals("-i") || args[i].equals("-iterations")) {
					PSOiterations = Integer.parseInt(args[i + 1]);
					if (PSOiterations <= 0) {
						throw new WrongParameterException("Number of iterations has to be a positive integer");
					}
				}
				if (args[i].equals("-p") || args[i].equals("-particles")) {
					numParticles = Integer.parseInt(args[i + 1]);
					if (numParticles <= 0) {
						throw new WrongParameterException("Number of particles has to be a positive integer");
					}
				}
				if (args[i].equals("-d") || args[i].equals("-dimensions")) {
					numDim = Integer.parseInt(args[i + 1]);
					if (numDim < 1) {
						throw new WrongParameterException("Dimensions have to be a positive integer");
					}
				}
				if (args[i].equals("-rep") || args[i].equals("-replace")) {
					nR = Double.parseDouble(args[i + 1]);
					if (nR < 0 || 1 < nR) {
						throw new WrongParameterException("Replacement ratio has to be 0-1");
					}
				}
				if (args[i].equals("-freq") || args[i].equals("-frequency")) {
					freq = Double.parseDouble(args[i + 1]);
					if (freq < 0 || 1 < freq) {
						throw new WrongParameterException("Frequency of replacement ratio has to be 0-1");
					}
				}
				
				if (args[i].equals("-r") || args[i].equals("-range")) {
					rangeRatio = Double.parseDouble(args[i + 1]);
					if (rangeRatio < 0 || 1 < rangeRatio) {
						throw new WrongParameterException("Range of replacement ratio has to be 0-1");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		
		Logger logger = new Logger(log);
		
		PSO pso = new PSO(numParticles);
		pso.setLogger(log);
		pso.setFunction(function);
		pso.setNumDim(numDim);
		pso.setNumIt(PSOiterations);
		pso.setFreq(freq);
		pso.setNReplace(nR);
		pso.setRangeRatio(rangeRatio);
		
		if (runs == 1) {
			logger.log("Initializing....");
			pso.initPSO();
			logger.log("Running....");
			double res = pso.runPSO();
			out.printf("%.9f", res);
			logger.log(" is the approximation");
			out.println();
		}
		if (runs == 2) {
			logger.log("Benchmarking....");
			Bench b = new Bench(pso);
			double[] results = b.startBench(benchRounds);
			out.print("Results: {");
			for (int i = 0; i < benchRounds - 1; i++) {
				out.print(results[i] + ", ");
			}
			out.println(results[benchRounds - 1] + "}");
		}
	}
}