package com.mcrg;

import java.util.Random;

public class PSO {

    private static final double Infinite = Double.POSITIVE_INFINITY;

    // Create parameters
    private static double cognitiveC;
    private static double socialC;      // Components
    private static double minX;
    private static double maxX;         // Max and min x value
    private static double wMin;
    private static double wMax;         // Max and min inertia value
    private static double minV;
    private static double maxV;         // Max vector
    private static double w;            // Inertia


    private static double freq;                 // Frequency of replacement (0-1)
    private static double nR;                   // Ratio of particles to be replaced (0-1)
    private static double rangeRatio;           // Range within which new particles appear (0-1)
    private static double gBestValue;           // Global best value
    private static double nReplaceRange;        // Range within to replace


    private static int
            numPar,                 // # of particles
            numDim,                 // # of dimensions
            numIt,                  // # of iterations
            nReplace,               // Number of particles to replace
            nReplaceIt;             // Number of rounds to replace

    // Create structures and value storage
    private static double[]
            pBestValue,                 // Particle best value
            pValue,                     // Stored values of each f(p)
            gBestPosition,              // Global best position
            gBestValueHistory;          // Global history of best values

    private static double[][]
            pBestPosition,              // Particle best position
            nowPosition,                // Position coordinates
            V;                          // Velocity

    private static int[] worst;         // Stores indices of worst particles

    public String function;
    static Random rand = new Random();
    private Logger l;

    public PSO(int n) {
        this.numPar = n;
        this.cognitiveC = 2.05;
        this.socialC = 2.05;            // Components
        this.minX = -5.12;
        this.maxX = 5.12;               // Max and min x value
        this.wMin = 0.4;
        this.wMax = 0.9;                // Max and min inertia value
        this.minV = 0;
        this.maxV = 1;                  // Max vector
        this.w = 0.9;                   // Inertia
        this.freq = 0;                // Frequency of replacement (0-1)
        this.nR = 0.00;                 // Number of particles to be replaced (0-1)
        this.rangeRatio = 0.1;               // Range within which new particles appear (0-1)
        this.nReplace = 0;
        this.nReplaceIt = 0;

        this.function = "squaresum";    // Default function
        this.numDim = 3;
        this.numIt = 1000;
        this.l = new Logger(false);
    }

    public double f(double[] x) {
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

    public void initPSO() {
        this.pBestValue = new double[numPar];                // Particle best value
        this.pValue = new double[numPar];                    // Stored values of each f(p)
        this.gBestPosition = new double[numDim];             // Global best position
        this.gBestValueHistory = new double[numIt];          // Global history of best values

        this.pBestPosition = new double[numPar][numDim];     // Particle best position
        this.nowPosition = new double[numPar][numDim];       // Position coordinates
        this.V = new double[numPar][numDim];                 // Velocity

        worst = new int[nReplace];         // Stores indices of worst particles

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

        // Compute replacement number and frequency
        this.nReplace = (int) Math.round(numPar * nR);
        this.nReplaceIt = (int) Math.round(numIt * freq);
        this.nReplaceRange = (Math.abs(minX) + Math.abs(maxX));

        l.log("Initialized PSO of:");
        l.log(this.numPar + " particles");
        l.log(this.numIt + " iterations");
        l.log("Optimizing " + this.function + " function in " + this.numDim + " dimensions");
        if (this.nReplace != 0 || this.nReplaceIt != 0) {
            l.log(this.nReplace + " particles to be replaced every " + this.nReplaceIt + " rounds");
        }
    }

    public double runPSO() {
        int gen = 1;
        for (int j = 0; j < numIt; j++) {

            if (gen == nReplaceIt) {

                gen = 1;
            } else gen++;


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
                l.log("Round " + j + ": updated best value to " + gBestValueHistory[j]);
            }
        }
        return gBestValueHistory[gBestValueHistory.length - 1];
    }

    // Setters

    public void setLogger(boolean log) {
        this.l = new Logger(log);
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
}
