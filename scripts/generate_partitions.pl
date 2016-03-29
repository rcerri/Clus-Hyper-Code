#Output is of the following form:
#
#1 2,5 3 4
#1 2 3,5 4
#1 2 3 4,5
#1,2,5 3 4
#1,2 3,5 4
#1,2 3 4,5
#1,3,5 2 4
#1,3 2,5 4
#
#Each line represents a partitioning. Targets separated by comma should be in the same cluster; clusters are seperated by space.
#For example: "1,3 2,5 4" means that targets 1 and 3 should be clustered together, targets 2 and 5 should also be clustered, and target 4 is separate
#
#To run the water-quality dataset with 6 elements, run 'perl generate_partitions.pl 6 17' and also use setting 'Disable = 17-30'.

$nb_partitions = $ARGV[0]; # number of elements in the set (runs smoothly on my laptop for sets up to 13 elements)
$starting_index = $ARGV[1]; # index of first target (e.g. 17 for water-quality; use 1 if not needed)

$partitions[0] = ["$starting_index"]; # array of strings
$partitions[1] = ["$starting_index"];

for ($i=2;$i<=$nb_partitions;$i++) {
    $newindex = $starting_index + $i - 1;
    $partitions[$i] = ();
    # take all elements from partition with i-1 elements, and add new cluster with single element
    foreach $part (@{ $partitions[$i-1] }) {
        $newpart = $part . " " . $newindex;
        push(@{ $partitions[$i] },$newpart);
    }
      
    # take all elements from partition with i-1 elements, and add new element to each cluster
    foreach $part (@{ $partitions[$i-1] }) {
        @clusters = split(' ',$part);
        $nbclusters = @clusters;
        for ($cl=0;$cl<$nbclusters;$cl++) {
            $old = $clusters[$cl];
            $clusters[$cl] = $clusters[$cl] . "," . $newindex;
            $newpart = join(" ",@clusters);
            push(@{ $partitions[$i] },$newpart);
            $clusters[$cl] = $old;
        }        
    }
}

@bell = qw(1 1 2 5 15 52 203 877 4140 21147 115975 678570 4213597 27644437 190899322 1382958545 10480142147 82864869804 682076806159 5832742205057);
$nb_partitions_generated = @{$partitions[$nb_partitions]};
#print "all partitions with $nb_partitions elements:\n";
foreach $part (@{$partitions[$nb_partitions]}) {
    print "$part\n";
}
#print "number of partitions generated: $nb_partitions_generated (check: Bell number = $bell[$nb_partitions])\n";
