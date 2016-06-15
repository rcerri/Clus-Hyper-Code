[Data]
File = flags-train.arff
TestSet = flags-test.arff

[Output]
%WritePredictions = Test

[Hierarchical]
Type = TREE
%WType = ExpAvgParentWeight
%HSeparator = /
DisableLabels = 2-3,7
%WParam = 1.0

[Ensemble]
Iterations = 5
EnsembleMethod = RForest
