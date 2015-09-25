#!/usr/bin/env python -u

import os

show_errors = True

print "Warming Up"
print "=========="
print ""
#os.system("scala -J-Xss200m cs162.lapel.Interpreter good/append0.lpl &> /dev/null | head -n 100")
os.system("scala -J-Xss200m cs162.lapel.Interpreter good/append0.lpl &> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'")

print "Good Tests"
print "=========="
print ""

for file in os.listdir("good"):
    if file.endswith(".lpl"):
        cmd = ""
        if show_errors:
            cmd = "timeout 2 scala -J-Xss200m cs162.lapel.Interpreter good/" + file + " | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'"
        else:
            cmd = "timeout 2 scala -J-Xss200m cs162.lapel.Interpreter good/" + file + " 2> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        print "running " + file + ":"
        os.system(cmd)
        print ""

print ""
print "Bad Tests"
print "========="
print ""

for file in os.listdir("bad"):
    if file.endswith(".lpl"):
        cmd = ""
        if show_errors:
            cmd = "timeout 2 scala -J-Xss200m cs162.lapel.Interpreter bad/" + file + " | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        else:
            cmd = "timeout 2 scala -J-Xss200m cs162.lapel.Interpreter bad/" + file + " 2> /dev/null | awk '{if ($0 == \"========\" && count < 10) {print $0; count++;} else if ($0 != \"========\" && count < 10) {print $0;} else {exit 0;}}'" 
        print "running " + file + ":"
        os.system(cmd)
        print ""
