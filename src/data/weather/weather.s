[General] % seed of random generator
RandomSeed = 0 
[Data] % training data
File = weather.arff % data used for evaluation (file name / proportion)
TestSet = None % data used for tree pruning (file name / proportion)
PruneSet = None % number of folds in cross-validation (clus -xval ...)
XVal = 10 
[Attributes] % index of target attributes
Target = 5 % Disables some attributes (e.g., "5,7-8")
Disable = 4 % Sets the index of the key attribute
Key = None % Normalize numeric attributes
Weights = Normalize 
[Model] % at least 2 examples in each subtree
MinimalWeight = 2.0 
[Tree] % f-test stopping criterion for regression
FTest = 1.0 % Convert the tree to a set of rules
ConvertToRules = No 
[Constraints] % 
Syntactic = None % 
MaxSize = Infinity % 
MaxError = Infinity % 
MaxDepth = Infinity 
file with syntactic constraints (a partial tree)
maximum size for Garofalakis pruning
maximum error for Garofalakis pruning
Stop building the tree at the given depth
[Output]
AllFoldModels = Yes
% Output model in each cross-validation fold
AllFoldErrors = No
% Output error measures for each fold
TrainErrors = Yes
% Output training error measures
UnknownFrequency = No
% proportion of missing values for each test
BranchFrequency = No
% proportion of instances for which test succeeds
WritePredictions = {Train,Test}
% write test set predictions to file
[Beam]
SizePenalty = 0.1
BeamWidth = 10
MaxSize = Infinity
% size penalty parameter used in the beam heuristic
% beam width
% Sets the maximum size constraint
