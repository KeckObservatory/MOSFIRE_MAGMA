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
refdir=$rootdir/baseline/$package

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
    ${package}.msc \
    ${package}_align.msc \
    ${package}_BarPositions.txt \
    ${package}_BarPositions.txt_arcsec \
    ${package}_AlignmentBarPositions.txt \
    ${package}_AlignmentBarPositions.txt_arcsec \
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
    if [ -e $f1 ] && [ -e $f2 ]
    then
	# if both files exist, compare
	echo -n "comparing $file: "
	d=`diff $f1 $f2`
	if [ "$d" == "" ]; then
	    echo "same"
	else 
	    echo "differ"
	fi
	# if verbose, display diff results
	if [ $verbose -eq 1 ]; then
	    echo "diff $f1 $f2"
	    echo "$d"
	fi
    else 
	# if either or both files don't exist, 
	# say same only if both files don't exist
	if [ -e $f1 ]
	then
	    echo "$f1 exists, but $f2 does not: differ"
	elif [ -e $f2 ] 
	then
	    echo "$f2 exists, but $f1 does not: differ"
	else
	    echo "Both $f1 and $f2 do not exist: same"
	fi
    fi
done