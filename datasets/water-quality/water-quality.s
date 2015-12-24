[Data]
File = water-quality-train.arff
TestSet = water-quality-test.arff

[Attributes]
Target = 17-30
Disable = 17-30

%[Output]
%WritePredictions = Test

%[Ensemble]
%Iterations = 500
%EnsembleMethod = RForest