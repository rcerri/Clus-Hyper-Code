[Data]
File = flags-train.arff
TestSet = flags-test.arff

[Attributes]
% example with first 2 targets disabled
Target = 22-26
Disable = 20-26
Weights = 1

[Output]
OutputMultiLabelErrors = Yes

[Ensemble]
Iterations = 10
EnsembleMethod = RForest