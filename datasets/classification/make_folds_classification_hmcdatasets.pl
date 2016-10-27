# LS: 19/07/2016
# Strategy: retrieve all examples from train and test, make folds

$ex{"cellcycle"} = 1;
$targetbegin{"cellcycle"} = 78;
$targetend{"cellcycle"} = 255;

$ex{"church"} = 1;
$targetbegin{"church"} = 32;
$targetend{"church"} = 209;

$ex{"derisi"} = 1;
$ex{"eisen"} = 1;
$ex{"gasch1"} = 1;
$ex{"gasch2"} = 1;
$ex{"pheno"} = 1;
$ex{"spo"} = 1;
$ex{"expr"} = 1;
$ex{"seq"} = 1;


# actually targetbegin and targetend not needed anymore because class variables already numeric

#cellcycle -> [78-255]
#church -> [32-209]
#derisi -> [64-241]
#eisen -> [80-244]
#gasch1 -> [174-351]
#gasch2 -> [53-230]
#pheno -> [277-441]
#spo -> [85-262]
#expr -> [560-737]
#seq -> [529-706]

foreach $k (keys %ex) {

#$k = "oes10";
$nb_folds = 10;

# save header
# merge training and test set

open (OUTH, ">$k/$k-header.txt") || die "Can't open OUTH: $k/$k-header.txt";
#open (OUTE, ">$k/$k-examples.txt") || die "Can't open OUTE";
#open (FILE, "$k/$k-train.arff") || die "Can't open FILE";
# just use one particular fold to get the header
open (FILE, "$k/$k-fold-1.arff") || die "Can't open FILE";


$line = <FILE>;
$attr_id = 0;
while (($line !~ /^\@data/) && ($line !~ /^\@DATA/)) {
  if ($line =~ /^\@attribute/) {
    $attr_id++;
    # NOT NECESSARY ANYMORE BECAUSE TARGET VARIABLES ARE ALEADY NUMERIC
    #if (($targetbegin{$k} <= $attr_id) && ($attr_id <= $targetend{$k})) {
    #  if ($line =~ /^\@attribute(.*)\{0,1\}/) {
    #    print OUTH "\@attribute$1numeric\n";
    #  }
    #  else {
    #    die "Error parsing targetattribute $line";
    #  }
    #}
    #else {
    print OUTH $line;    
    #}
  }
  else {
    print OUTH $line;
  }
  $line = <FILE>;
}
print OUTH $line;

#while ($line = <FILE>) {
#  print OUTE $line;
#}

close (FILE);
close (OUTH);

#open (FILE, "$k/$k-test.arff") || die "Can't oen FILE";

#$line = <FILE>;
#while (($line !~ /^\@data/) && ($line !~ /^\@DATA/)) {
#  $line = <FILE>;
#}

#while ($line = <FILE>) {
#  print OUTE $line;
#}

#close (FILE);

#close (OUTE);


# assemble training, valid and test sets

for ($i=0;$i<10;$i++) {
   
   #$i is 0-9 format
   #$r is 1-10 format
   $r = $i+1;
   
   # test set
   open (TEST, ">${k}/${k}_fold${i}.test") || die "Can't open TEST";
   open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
   while ($hline = <HEADER>) {
     print TEST $hline;
   }
   close (HEADER);
   #open (FOLD, "10folds/${k}/iterative/${k}_${r}.arff") || die "Can't open FOLD: 10folds/${k}/iterative/${k}_${r}.arff";
   open (FOLD, "$k/${k}-fold-${r}.arff") || die "Can't open FOLD: $k/${k}-fold-${r}.arff";
   $fline = <FOLD>;
   while (($fline !~ /^\@data/) && ($fline !~ /^\@DATA/)) {
     $fline = <FOLD>;    
   }
   while ($fline = <FOLD>) {
     print TEST $fline;
   }
   
   close (FOLD);
   close (TEST);
   
   # validation set
   
   $v = ($i-1) % 10;
   $w = $v+1;
   if ($v > 9) { die "Wrong fold value"; }
   
   open (VALID, ">${k}/${k}_fold${i}.valid") || die "Can't open VALID";
   open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
   while ($hline = <HEADER>) {
     print VALID $hline;
   }
   close (HEADER);
   #open (FOLD, "10folds/${k}/iterative/${k}_${w}.arff") || die "Can't open 10folds/${k}/iterative/${k}_${w}.arff";
   open (FOLD, "$k/${k}-fold-${w}.arff") || die "Can't open $k/${k}-fold-${r}.arff";
   $fline = <FOLD>;
   while (($fline !~ /^\@data/) && ($fline !~ /^\@DATA/)) {
     $fline = <FOLD>;    
   }
   while ($fline = <FOLD>) {
     print VALID $fline;
   }
   close (FOLD);
   close (VALID);

   # training set
 
   open (TRAIN, ">${k}/${k}_fold${i}.train") || die "Can't open TRAIN";
   
   open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
   while ($hline = <HEADER>) {
     print TRAIN $hline;
   }
   close (HEADER);
   
   for ($j=0;$j<10;$j++) {
     if (($i != $j) && ($v != $j)) {
       $l = $j+1; # Ricardo's format
       #open (FOLD, "10folds/${k}/iterative/${k}_${l}.arff") || die "Can't open 10folds/${k}/iterative/${k}_${w}.arff";
       open (FOLD, "$k/${k}-fold-${l}.arff") || die "Can't open $k/${k}-fold-${l}.arff";
       $fline = <FOLD>;
       while (($fline !~ /^\@data/) && ($fline !~ /^\@DATA/)) {
         $fline = <FOLD>;    
       }
       while ($fline = <FOLD>) {
         print TRAIN $fline;
       }
       close (FOLD);
     }
 
   }
   close (TRAIN);
     
   # trainvalid
   
   open (TRAINVALID, ">${k}/${k}_fold${i}.trainvalid") || die "Can't open TRAINVALID";
   
   open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
   while ($hline = <HEADER>) {
     print TRAINVALID $hline;
   }
   close (HEADER);
   
   for ($j=0;$j<10;$j++) {
     if ($i != $j) {
       $l = $j+1;
       #open (FOLD, "10folds/${k}/iterative/${k}_${l}.arff") || die "Can't open 10folds/${k}/iterative/${k}_${l}.arff";
       open (FOLD, "$k/${k}-fold-${l}.arff") || die "Can't open $k/${k}-fold-${l}.arff";
       $fline = <FOLD>;
       while (($fline !~ /^\@data/) && ($fline !~ /^\@DATA/)) {
         $fline = <FOLD>;    
       }
       while ($fline = <FOLD>) {
         print TRAINVALID $fline;
       }
       close (FOLD);
      }
    }
    close (TRAINVALID);    
     
}    

}