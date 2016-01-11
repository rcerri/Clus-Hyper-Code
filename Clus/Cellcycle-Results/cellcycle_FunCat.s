[General]
Compatibility = MLJ08

[Data]
File = /Users/cerri/Dropbox/Doutorado/HMC/Datasets/Datasets_KUL/Funcat/Cellcycle/datasets_FUN/cellcycle_FUN/cellcycle_FUN.train.arff.zip
PruneSet = /Users/cerri/Dropbox/Doutorado/HMC/Datasets/Datasets_KUL/Funcat/Cellcycle/datasets_FUN/cellcycle_FUN/cellcycle_FUN.valid.arff.zip
TestSet = /Users/cerri/Dropbox/Doutorado/HMC/Datasets/Datasets_KUL/Funcat/Cellcycle/datasets_FUN/cellcycle_FUN/cellcycle_FUN.test.arff.zip

[Attributes]
ReduceMemoryNominalAttrs = yes

[Hierarchical]
Type = TREE
WType = ExpAvgParentWeight
HSeparator = /
ClassificationThreshold = [0,2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,70,72,74,76,78,80,82,84,86,88,90,92,94,96,98,100]

[Tree]
ConvertToRules = No
FTest = [0.001,0.005,0.01,0.05,0.1,0.125]
Heuristic = Dummy

[Model]
MinimalWeight = 5.0
