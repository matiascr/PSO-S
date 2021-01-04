# PSO-S
Particle Swarm Optimization with selection component

## Justification
Modification of traditional PSO algorithm to include possibility of death of a number of the worst performing particles to be replaced by particles in the vicinity of the best performing one, awarding better accuracy of the results obtained.
Fine tuning of the replacement parameters (frequency, number and range) is required in order to avoid over-localization, especially in functions in which many local minima/maxima exist. This is also required to avoid the diminishing returns of better accuracy at the cost of too long an execution, possibly due to a high frequency of replacement, or too large a number of particles to replace.

## Parameters and results of benchmark
https://www.notion.so/matiascr/Particle-Swarm-Optimization-with-selection-component-4f2deb70a81a4de39c98385459256039

## Execution (Main)
```
-i, -iterations			:		Number of iterations for PSO (default is 1000)

-p, -particles			:		Number of particles to be used by PSO (default is 1000)

-v, -verbose			:		Display updates to execution of algorithm in command line.

-f, -function			:		Function to minimize
						Options are "rastrigin", "squaresum", "sphere" (without quotations)
						
-b, -bench			:		Number of iterations of benchmark
						Input will be the number of runs it will do

-d, -dimensions			:		Number of dimensions to use (sphere only works in 3)

-rep,-replace			:		Proportion of particles to replace (0-1)
						Actual number will be Input * Number of particles

-freq, -frequency		: 		Proportion of frequency to replace (0-1)
						Actual number will be Input * Number of iterations
						
-r, -range			:		Distance within which new particles are generated (0-1)
						Actual number will be Input * Search space span
```

### Example execution
```
$ java com.mcrg.Main -p 1000 -i 1000 -v -rep 0.1 -freq 0.001 -range 0.1    
Initializing....
Initialized PSO of:
1000 particles
1000 iterations
From -5.12 to 5.12
Optimizing squaresum function in 3 dimensions
100 particles to be replaced every 1 rounds
Within 1.024 units of the best value.
Running....
Round 3: updated best value to 0.0011319264608599822
After a replacement
Round 5: updated best value to 4.8350343675586225E-4
After a replacement
Round 25: updated best value to 2.1255066595204586E-4
After a replacement
Round 67: updated best value to 1.361837991931866E-4
After a replacement
Round 176: updated best value to 6.382214447582556E-5
After a replacement
Round 350: updated best value to 5.985694964971576E-5
After a replacement
Round 434: updated best value to 5.198685380082983E-5
After a replacement
0.000051987 is the approximation
```
            
## To-Do's

- Benchmark could use some work
- More functions to optimized could be added
- Option to maximize funtions
- More metaheuristic functions to be customized from the command-line app
