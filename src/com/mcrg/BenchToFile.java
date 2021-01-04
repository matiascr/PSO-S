package com.mcrg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class BenchToFile {
	public int nRuns;
	
	public BenchToFile(int nRuns) {
		this.nRuns = nRuns;
	}
	
	public void totalBench() throws WrongParameterException, IOException {
		String[] fs = {"rastrigin", "sphere", "squaresum"};
		double[] freqs = {0, 0.1};
		double[] reps = {0, 0.1};
		double[] ranges = {0, 0.1, 0.5};
		
		List<String[]> list = new ArrayList<>();
		
		for (int i = 0; i < 3; i++) {
			PSO pso = new PSO(1000);
			pso.setLogger(false);
			pso.setFunction(fs[i]);
			pso.setNumDim(3);
			pso.setNumIt(1000);
			pso.setFreq(0.1);
			pso.setNReplace(0.1);
			pso.setRangeRatio(0.1);
			Bench b = new Bench(pso);
			double[] results = b.startBench(nRuns);
			String[] resString = new String[results.length];
			for (int j = 0; j < results.length; j++) {
				resString[j] = String.valueOf(results[j]);
			}
			list.add(resString);
		}

		CsvWriterSimple writer = new CsvWriterSimple();
//		writer.convertToCsvFormat(resString);
		writer.writeToCsvFile(list, new File("/Users/matias/Desktop/totalbench-rep0.1,0.1,0.1.csv"));
	}
	
	public void bench(double[] results) throws IOException {
		String[] resString = new String[results.length];
//		String[] header = new String[results.length];
		for (int i = 0; i < results.length; i++) {
			resString[i] = String.valueOf(results[i]);
//			header[i] = String.valueOf(i + 1);
		}
		List<String[]> list = new ArrayList<>();
//		list.add(header);
		list.add(resString);
		out.println("Converted");
		CsvWriterSimple writer = new CsvWriterSimple();
		writer.convertToCsvFormat(resString);
		writer.writeToCsvFile(list, new File("/Users/matias/Desktop/bench.csv"));
		out.println("File made");
	}
	
	public static void main(String[] args) throws IOException, WrongParameterException {
		BenchToFile btf = new BenchToFile(100);
		btf.totalBench();
	}
}
