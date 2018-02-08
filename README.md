# Conditional-DFA
Replication package for TACAS 2018 paper

Use JDK 1.7.
Import it as an Eclipse Java project, and use classes compiled by Eclipse in bin directory.
Make sure to add artifacts folder as a source folder.
Add provided jars in lib folder to the class path. Also, add Z3 jar to the class path.

Experiment set up description
------------------------------------------
Step 0: create required file structure:
ConditionalTACAS
	conditions
		paths
	resultsRD
		invariants
		time
	resultsVA
		combined
		    c1
		    c2
		domains
			dom4.txt
		invariants
			c1
			c2
		satunsat
			c1
			c2
		smt2Files
			c1
			c2
		time
------------------------------------------
Step 1: generate partitions
./runPartition.bash ./ConditionalTACAS/conditions/ classToMethodCond.txt

It will populate conditions and conditions/paths directories with graphs and paths for each class-method pair
------------------------------------------
For RD data:
------------------------------------------
Step 2: run full (f), conditional (c2)  and pseudo-conditional (c1) analysis
./runAnalysisRD.bash ./ConditionalTACAS/ classToMethodCond.txt f y
./runAnalysisRD.bash ./ConditionalTACAS/ classToMethodCond.txt c1 y
./runAnalysisRD.bash ./ConditionalTACAS/ classToMethodCond.txt c2 y

run 2 more times (for average) with the last argument set to "n", i.e., do not print invariants.
It will populate resutlsRD/invariants and reslutsRD/time directories
------------------------------------------
Step 3: combine and compare the time and invariants between c1 and f, and c2 and f
./runCombineRD.bash ./ConditionalTACAS/ classToMethodCond.txt  c1
./runCombineRD.bash ./ConditionalTACAS/ classToMethodCond.txt  c2

It will produce 2 files in resultsRD/time : time_c1 and time_c2
This is the data that is found in TACASData.xlsx in "RD c1 vs f", and "RD c2 vs f" tabs.
"RD c1 compare" and "RD c2 compare" tabs have the same informations in  %inv and %time columns

------------------------------------------
For VA data:
------------------------------------------
Step 2: run full (f), conditional (c2) and pseudo-conditional (c1) analyses.
y or no for writing invariant to a file.
Modify the script by modifying the path to Z3 build with Java extension.

./runAnalysisVA.bash ./ConditionalTACAS/ classToMethodCond.txt f y
./runAnalysisVA.bash ./ConditionalTACAS/ classToMethodCond.txt c1 y
./runAnalysisVA.bash ./ConditionalTACAS/ classToMethodCond.txt c2 y

It will produce data in resultsVA/invariants resultsVA/invariants/c1, resultsVA/invariants/c2
which will contains the computed invariants produces by each of the analysis. The invariants
are written in smt2 format.
It will also produce the runtime data in resultsVA/time
------------------------------------------
Step 3: for the same method combine the runs of conditional and pseudo-conditional analyses.
./runCombineVA.bash ./ConditionalTACAS/ classToMethodCond.txt c1
./runCombineVA.bash ./ConditionalTACAS/ classToMethodCond.txt c2

It will produce time/time_c1 and time/time_c2 files, which contains the time data
of each conditional path. An entry look like:
test.BallonFactory	1	1f2t	1	5273	5157
test.BallonFactory	1	1f2f	2	5273	5158
test.BallonFactory	1	1t2t	3	5273	5244
test.BallonFactory	1	1t2f	4	5273	5258
Where the first two columns are class and its method id, next is the path encoding
followed by the order in which this path completed. The one before last column is time
in ms for the full analysis and the last column is the time for that conditional analysis.
This data is in "VA c1 vs f time" and "VA c2 vs f time" tabs. NOTE that if you
want to "reset" experiments delete time files, otherwise the data will be appened to the old data

Also it will populate resultsVA/combined/c1 and resultsVA/combined/c2, which contain
invariants aggregated in the order in which conditional analyses complete its execution.
test.BallonFactory_1_1_dom4.txt
test.BallonFactory_1_2_dom4.txt
test.BallonFactory_1_3_dom4.txt
test.BallonFactory_1_4_dom4.txt
The first file only contain invariants from 1f2t, the second combined invariants of
1f25 and 1f2f, and so on. That is each file contains that incremental invariants.
------------------------------------------
Step 4: create smt2 file that compare the results of the full analysis with each
incremental data.
./runSmt2Format.bash ./ConditionalTACAS/ classToMethodCond.txt c1
./runSmt2Format.bash ./ConditionalTACAS/ classToMethodCond.txt c2

It will populate resultsVA/smt2Files/c1 and resultsVA/smt2File/c2 directories with
smt2 formulas with c1 -> f && f -> c1 queries for each computed invariant.
------------------------------------------
Step 5: run z3 on the generated smt2 queries. run the following script where
z3 is locate, i.e., inside its build directory.

./runSatUnsat.bash ~/git/Conditional-DFA/ConditionalTACAS/ ~/git/Conditional-DFA/classToMethodCond.txt c1
./runSatUnsat.bash ~/git/Conditional-DFA/ConditionalTACAS/ ~/git/Conditional-DFA/classToMethodCond.txt c2

This step will produce sat/unsat result for each query in resultsVA/satunsat/c1 and resultsVA/satunsat/c2
------------------------------------------
Step 6 run the sat/unsat count script (delete old statunast_dom4.txt files, otherwise data will be appended)
./runCountSatUnsat.bash ./ConditionalTACAS/ classToMethodCond.txt c1
./runCountSatUnsat.bash ./ConditionalTACAS/ classToMethodCond.txt c2

It will generate resultsVA/satunsat/c1/satunsat_dom4.txt and resultsVA/satunsat/c2/satunsat_dom4.txt files
with the following entries
test.BallonFactory	1	1	44	0	7	0
test.BallonFactory	1	2	46	0	5	0
test.BallonFactory	1	3	51	0	0	0
test.BallonFactory	1	4	51	0	0	0
test.BallonFactory	1	5	51	0	0	0
test.BallonFactory	1	6	51	0	0	0
test.BallonFactory	1	7	51	0	0	0
test.BallonFactory	1	8	51	0	0	0
where the first and second columns are class and method, the third column is path id,
the forth row is sat/sat count, next is unsat/sat count, followed by sat/unsat and
the last column is unsat/unsat. 
sat/sat - conditional and full compute exact same invariants
unsat/sat - conditional over-approximates full (which should not happened, i.e., that column should be all 0s)
sat/unsat - conditional under-approximates full - happens when not all variants are computed, i.e., true but no false branch
unsat/unsat - conditional and full are incomparable (which should not happened)

This data is in "VA c1 vs f satunsat" and "VA c2 vs f satunsat" tabs of the spreadsheet.


