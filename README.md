# PSO-S
Particle Swarm Optimization with selection component

## Justification
...

## Parameters and results of benchmark
https://www.notion.so/matiascr/Particle-Swarm-Optimization-with-selection-component-4f2deb70a81a4de39c98385459256039

## Execution (Main)

-i, -iterations		:		Number of iterations for PSO (default is 1000)

-p, -particles		:		Number of particles to be used by PSO (default is 1000)

-v, -verbose		:		Display updates to execution of algorithm in command line.

-f, -function		:		Function to minimize
						Options are "rastrigin", "squaresum", "sphere" (without quotations)
						
-b, -bench		:		Number of iterations of benchmark
						Input will be the number of runs it will do

-d, -dimensions	:		Number of dimensions to use (sphere only works in 3)

-rep,-replace		:		Proportion of particles to replace (0-1)
						Actual number will be *Input* * *Number of particles*

-freq, -frequency	: 		Proportion of frequency to replace (0-1)
						Actual number will be *Input* * *Number of iterations*
						
-r, -range			:		Distance within which new particles are generated (0-1)
						Actual number will be *Input* * *search space span*
            
## To-Do's

- Benchmark could use some work
- More functions to optimized could be added
- Option to maximize funtions
- More metaheuristic functions to be customized from the command-line app
