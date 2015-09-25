#!/usr/bin/env python -u

import os

show_errors = True

print "Warming Up"
print "=========="
print ""
os.system("scala -J-Xss200m cs162.linlapel.Interpreter tests/test1.llpl &> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'")

print "Linear Tests"
print "============"
print ""

for file in os.listdir("tests"):
    if file.endswith(".llpl"):
        cmd = ""
        if show_errors:
            cmd = "timeout 2 scala -J-Xss200m cs162.linlapel.Interpreter tests/" + file + " | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'"
        else:
            cmd = "timeout 2 scala -J-Xss200m cs162.linlapel.Interpreter tests/" + file + " 2> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        print "running " + file + ":"
        os.system(cmd)
        print ""

print ""
print "Nonlinear Tests (From Last Project)"
print "==================================="
print ""

for file in os.listdir("fromlastproject"):
    if file.endswith(".llpl"):
        cmd = ""
        if show_errors:
            cmd = "timeout 2 scala -J-Xss200m cs162.linlapel.Interpreter fromlastproject/" + file + " | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        else:
            cmd = "timeout 2 scala -J-Xss200m cs162.linlapel.Interpreter fromlastproject/" + file + " 2> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        print "running " + file + ":"
        os.system(cmd)
        print ""
