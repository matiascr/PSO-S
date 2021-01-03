package com.mcrg;

import java.util.Arrays;
import java.util.Random;

public class PSO {
	
	private static final double Infinite = Double.POSITIVE_INFINITY;
	
	// Create parameters
	private double
			cognitiveC,
			socialC,      // Components
			minX,
			maxX,         // Max and min x value
			wMin,
			wMax,         // Max and min inertia value
			minV,
			maxV,         // Max vector
			w;            // Inertia
	
	
	private double
			freq,                 // Frequency of replacement (0-1)
			nR,                   // Ratio of particles to be replaced (0-1)
			rangeRatio,           // Range within which new particles appear (0-1)
			gBestValue,           // Global best value
			nReplaceRange;        // Range within to replace
	
	
	private int
			numPar,                 // # of particles
			numDim,                 // # of dimensions
			numIt,                  // # of iterations
			nReplace,               // Number of particles to replace
			nReplaceIt;             // Number of rounds to replace
	
	// Create structures and value storage
	private double[]
			pBestValue,                 // Particle best value
			pValue,                     // Stored values of each f(p)
			gBestPosition,              // Global best position
			gBestValueHistory;          // Global history of best values
	
	private double[][]
			pBestPosition,              // Particle best position
			nowPosition,                // Position coordinates
			V;                          // Velocity
	
	private int[] worst;         // Stores indices of worst particles
	
	public String function;
	static Random rand = new Random();
	private Logger logger;
	
	public PSO(int n) throws WrongParameterException {
		if (n < 1) throw new WrongParameterException("Number of particles has to be a positive integer");
		
		this.numPar = n;                // Number of particles
		this.cognitiveC = 2.05;         // Cognitive component
		this.socialC = 2.05;            // Social component
		this.minX = -5.12;              // Min x value
		this.maxX = 5.12;               // Max x value
		this.wMin = 0.4;                // Min inertia value
		this.wMax = 0.9;                // Max inertia value
		this.minV = 0;                  // Min vector
		this.maxV = 1;                  // Max vector
		this.w = 0.9;                   // Inertia
		this.freq = 0;                  // Frequency of replacement (0-1)
		this.nR = 0.00;                 // Number of particles to be replaced (0-1)
		this.rangeRatio = 0.1;          // Range within which new particles appear (0-1)
		this.nReplace = 0;
		this.nReplaceIt = 0;
		this.nReplaceRange = (Math.abs(minX) + Math.abs(maxX)) * rangeRatio;
		
		this.function = "squaresum";    // Default function
		this.numDim = 3;
		this.numIt = 1000;
		this.logger = new Logger(false);
	}
	
	public double f(double[] x) throws WrongParameterException {
		if (x.length < 1) throw new WrongParameterException("Number of dimensions has to be a positive integer");
		
		// Initialize solution
		double sol = 0;
		// For Rastrigin
		switch (function) {
			case "rastrigin" -> {
				double A = 10;
				sol = (A * numDim);
				for (double v : x) {
					// Rastrigin function: A*n + ∑(x_i^2 - A*cos(2 * PI * x_i)) -> preferably 3 dimensions
					sol += (Math.pow(v, 2) - (A * Math.cos(2 * Math.PI * v)));
				}
				return sol;
			}
			case "squaresum" -> {
				for (double v : x) {
					// Sum of squares: ∑ (x_i^2) -> n dimensions
					sol += Math.pow(v, 2);
				}
				return sol;
			}
			case "sphere" -> {
				for (int i = 0; i < x.length; i++) {
					// Sphere: ∑((x_i-a_i)^2)  -> 3 dimensions
					double[] a = {3.5, -2, 1};
					sol += Math.pow((x[i] - a[i]), 2);
				}
				return sol;
			}
		}
		return sol;
	}
	
	public void initPSO() throws WrongParameterException {
		this.pBestValue = new double[numPar];                // Particle best value
		this.pValue = new double[numPar];                    // Stored values of each f(p)
		this.gBestPosition = new double[numDim];             // Global best position
		this.gBestValueHistory = new double[numIt];          // Global history of best values
		
		this.pBestPosition = new double[numPar][numDim];     // Particle best position
		this.nowPosition = new double[numPar][numDim];       // Position coordinates
		this.V = new double[numPar][numDim];                 // Velocity
		
		this.gBestValue = Infinite;
		// Set all particle best values to Infinite
		for (int p = 0; p < numPar; p++) {
			pBestValue[p] = Infinite;
		}
		
		// Initialize all particles position and velocity to random number within range
		for (int p = 0; p < numPar; p++) {
			for (int i = 0; i < numDim; i++) {
				nowPosition[p][i] = minX + (maxX - minX) * rand.nextDouble();
				V[p][i] = minV + (maxV - minV) * rand.nextDouble();
				
				// Velocity zero or negative (whip back)
				if (rand.nextDouble() < 0) {
					V[p][i] = -V[p][i];
					nowPosition[p][i] = -nowPosition[p][i];
				}
			}
		}
		
		// Load f(x) of each particle in M
		for (int p = 0; p < numPar; p++) {
			pValue[p] = f(nowPosition[p]);
			pValue[p] = -pValue[p];
		}
		
		// Compute replacement number and frequency and range
		this.nReplace = (int) Math.round(numPar * nR);
		this.nReplaceIt = (int) Math.round(numIt * freq);
		this.nReplaceRange = (Math.abs(minX) + Math.abs(maxX)) * rangeRatio;
		
		worst = new int[nReplace];         // Stores indices of worst particles
		// Load worst particles as 1st
		Arrays.fill(worst, 0);
		
		logger.log("Initialized PSO of:");
		logger.log(this.numPar + " particles");
		logger.log(this.numIt + " iterations");
		logger.log("From " + minX + " to " + maxX);
		logger.log("Optimizing " + this.function + " function in " + this.numDim + " dimensions");
		if (this.nReplace != 0 || this.nReplaceIt != 0) {
			logger.log(this.nReplace + " particles to be replaced every " + this.nReplaceIt + " rounds");
			logger.log("Within " + nReplaceRange + " units of the best value.");
		}
	}
	
	public double runPSO() throws WrongParameterException {
		int step = 1;
		for (int j = 0; j < numIt; j++) {
			boolean replaced = false;
			// Do replacement of worst particles
			if (nReplaceIt != 0 && step % nReplaceIt == 0) {
				// Replace the worst with new ones
				for (int p = 0; p < worst.length; p++) {
					for (int d = 0; d < numDim; d++) {
						// Assign new position to new particles
						nowPosition[p][d] = (gBestPosition[d]) + nReplaceRange * rand.nextDouble();
						// Assign new vectors to new particles
						V[p][d] = (rand.nextDouble() * socialC * (gBestPosition[d] - nowPosition[p][d]));
					}
				}
				replaced = true;
			}
			step++;
			
			
			// Update position
			for (int p = 0; p < numPar; p++) {
				for (int i = 0; i < numDim; i++) {
					nowPosition[p][i] = nowPosition[p][i] + V[p][i];
					
					if (nowPosition[p][i] > maxX) {
						nowPosition[p][i] = maxX;
					} else if (nowPosition[p][i] < minX) {
						nowPosition[p][i] = minX;
					}
				}
			}
			
			// Update values
			for (int p = 0; p < numPar; p++) {
				pValue[p] = f(nowPosition[p]);
//                valueOfP[p] = -valueOfP[p];
				
				// Update cognitive best
				if (pValue[p] < pBestValue[p]) {
					pBestValue[p] = pValue[p];
					if (numDim >= 0) System.arraycopy(nowPosition[p], 0, pBestPosition[p], 0, numDim);
				}
				if (pValue[p] < gBestValue) {
					gBestValue = pValue[p];
					if (numDim >= 0) System.arraycopy(nowPosition[p], 0, gBestPosition, 0, numDim);
				}
			}
			
			// Update social worst
			for (int i = 0; i < pValue.length; i++) {
				isWorst(i);
			}
			
			// Update social best
			gBestValueHistory[j] = gBestValue;
			
			// Update inertia within bounds
			w = wMax - ((wMax - wMin) / numIt) * j;
			
			// Update vector of particle
			for (int p = 0; p < numPar; p++) {
				for (int i = 0; i < numDim; i++) {
					
					// Random factors
					double r1 = rand.nextDouble();
					double r2 = rand.nextDouble();
					V[p][i] = (w * V[p][i]) + (r1 * cognitiveC * (pBestPosition[p][i] - nowPosition[p][i])) + (r2 * socialC * (gBestPosition[i] - nowPosition[p][i]));
					
					// Check and adjust bounds of velocity
					if (V[p][i] > maxV) {
						V[p][i] = maxV;
					} else if (V[p][i] < minV) {
						V[p][i] = minV;
					}
				}
			}
			
			// Log best result history
			if (j >= 1 && gBestValueHistory[j - 1] != gBestValueHistory[j]) {
				logger.log("Round " + j + ": updated best value to " + gBestValueHistory[j]);
				if (replaced){
					logger.log("After a replacement");
				}
			}
		}
		return gBestValueHistory[gBestValueHistory.length - 1];
	}
	
	// Setters
	
	public void setLogger(boolean log) {
		this.logger = new Logger(log);
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public void setNumDim(int numDim) {
		this.numDim = numDim;
	}
	
	public void setNumIt(int numIt) {
		this.numIt = numIt;
	}
	
	public void setFreq(double freq) {
		this.freq = freq;
	}
	
	public void setNReplace(double nR) {
		this.nR = nR;
	}
	
	public void setRangeRatio(double rangeRatio) {
		this.rangeRatio = rangeRatio;
	}
	
	private boolean isWorse(int a, int b) {
		return pValue[a] > pValue[b];
	}
	
	private void isWorst(int a) {
		for (int i = 0; i < worst.length; i++) {
			if (isWorse(a, worst[i])) {
				this.worst[i] = a;
				break;
			}
		}
	}
}
