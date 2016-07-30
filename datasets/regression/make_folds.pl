# LS: 19/07/2016
# Strategy: retrieve all examples from train and test, make folds

$ex{"oes10"} = 403;
$ex{"oes97"} = 334;
$ex{"rf1"} = 9124;
$ex{"rf2"} = 9126;
$ex{"scm1d"} = 9803;
$ex{"scm20d"} = 8966;
$ex{"water-quality"} = 1060;


foreach $k (keys %ex) {

#$k = "oes10";
$nb_folds = 10;

# save header
# merge training and test set

open (OUTH, ">$k/$k-header.txt") || die "Can't open OUTH";
open (OUTE, ">$k/$k-examples.txt") || die "Can't open OUTE";
open (FILE, "$k/$k-train.arff") || die "Can't open FILE";


$line = <FILE>;
while (($line !~ /^\@data/) && ($line !~ /^\@DATA/)) {
  print OUTH $line;
  $line = <FILE>;
}
print OUTH $line;

while ($line = <FILE>) {
  print OUTE $line;
}

close (FILE);
close (OUTH);

open (FILE, "$k/$k-test.arff") || die "Can't oen FILE";

$line = <FILE>;
while (($line !~ /^\@data/) && ($line !~ /^\@DATA/)) {
  $line = <FILE>;
}

while ($line = <FILE>) {
  print OUTE $line;
}

close (FILE);

close (OUTE);

# make folds

for ($j=0;$j<10;$j++) {
  open ($j, ">${k}/${k}_fold${j}") || die "Can't open FILE";
}

$i=0;
open (EX, "$k/$k-examples.txt") || die "Can't open EX";

while (($line = <EX>) && ($i < $ex{$k})) {
  $mod = $i % 10;
  print $mod $line;
  $i++;
}

close (EX);

for ($j=0;$j<10;$j++) {
  close ($j);
}

# assemble training, valid and test sets

for ($i=0;$i<10;$i++) {
  
  # test set
  open (TEST, ">${k}/${k}_fold${i}.test") || die "Can't open TEST";
  open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
  while ($hline = <HEADER>) {
    print TEST $hline;
  }
  close (HEADER);
  open (FOLD, "${k}/${k}_fold${i}") || die "Can't open FOLD";
  while ($fline = <FOLD>) {
    print TEST $fline;
  }
  close (FOLD);
  close (TEST);
  
  # validation set
  $v = ($i-1) % 10;
  
  open (VALID, ">${k}/${k}_fold${i}.valid") || die "Can't open VALID";
  open (HEADER, "$k/$k-header.txt") || die "Can't open HEADER";
  while ($hline = <HEADER>) {
    print VALID $hline;
  }
  close (HEADER);
  open (FOLD, "${k}/${k}_fold${v}") || die "Can't open FOLD";
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
    if (($i != $j) && ($i != $v)) {
      open (FILE, "${k}/${k}_fold${j}") || die "Can't open FILE";
      while ($line=<FILE>) {
        print TRAIN $line;
      }
      close (FILE);
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
      open (FILE, "${k}/${k}_fold${j}") || die "Can't open FILE";
      while ($line=<FILE>) {
        print TRAINVALID $line;
      }
      close (FILE);
    }
  }
  close (TRAINVALID);    
    
}    

}