#!/bin/bash

# check for verbose flag
verbose=0
if [ -z $1 ] 
then
    echo USAGE: $0 [-v] package [files...]
    exit
else
  if [ "$1" == "-v" ];then
      verbose=1
      shift
  fi
fi

#echo $verbose

# after optional verbose flag, package must be first argument
if [ -z $1 ] 
then
    echo USAGE: $0 [-v] package [files...]
    exit
fi

package=$1
shift


#define directories
rootdir=$KROOT/kss/mosfire/gui/mscgui/mascgen_test_data
testdir=$rootdir/working/$package
refdir=$rootdir/newbase/$package

#echo $package
echo $testdir
echo $refdir

# if more arguments after package, they are the products to compare
if [ ! -z "$1" ]
then
    products=("$@")
else
    # if no arguments, compare all products
    products=(${package}.coords \
    ${package}.param \
    ${package}.xml \
    ${package}_BarPositions.csh \
    ${package}_AlignmentBarPositions.csh \
    ${package}_SlitList.txt \
    ${package}_SlitRegions.reg \
    ${package}_StarList.txt)
fi

#echo ${#products[@]}
#echo ${products[@]}
echo "--------------------------------------------------------------"

#display results for all specified products to compare
for file in "${products[@]}"
do
    f1=${testdir}/$file
    f2=${refdir}/$file
    if [ -e $f1 ] 
    then
	if [ $verbose -eq 1 ]; then
	    echo "replacing $f2 with $f1: "
	fi
	/bin/cp $f1 $f2
    fi
done