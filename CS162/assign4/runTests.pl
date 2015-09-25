#!/usr/bin/perl

sub toInt($) {
    my $param = shift();
    return ($param) ? 1 : 0;
}
    
# -dirname
# -filename
sub runTest($$) {
    my ($dir, $file) = @_;
    my $path = "$dir/$file";
    my $output = `scala -cp scala-parser-combinators.jar:. Checker $path 2>&1`;
    my $isErr = toInt($output =~ /Illtyped/m);
    my $isGood = toInt($output =~ /well\-typed/m);
    my $expectedErr = toInt($file =~ /^bad/);
    print "$path: ";
    if (($isGood == 0 && $isErr == 1) ||
        ($isGood == 1 && $isErr == 0)) {
        if ($expectedErr == $isErr) {
            print "pass!\n";
        } else {
            print "----FAIL----\n";
        }
    } else {
        print "----UNKNOWN (Consider Failure)----\n";
    }
}

# Given the name of a directory, runs all tests in that directory
sub runTests($) {
    my $dirName = shift();
    my $fd;
    opendir($fd, $dirName) or die "Could not open tests directory: '$dirName'";
    while (my $file = readdir($fd)) {
        if ($file =~/^(good|bad).*\.fun$/) {
            runTest($dirName, $file);
        }
    }
    closedir($fd);
}

runTests("basic_tests");
runTests("big_tests");

