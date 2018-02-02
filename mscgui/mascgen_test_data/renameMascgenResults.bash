#!/bin/bash

packages=( q0207_pa_0 \
    q0207_pa_75 \
    q1442_pa_10 \
    q1442_pa_90 \
    q1700_pa_0 \
    q1700_pa_45 \
    q1700_pa_90 \
    q1700_shift_legal_range )

dir=$KROOT/kss/mosfire/gui/mscgui/mascgen_test_data/baseline

for package in "${packages[@]}"
do
    workingDir=$dir/${package}/
    mv $workingDir/${package}.xml  $workingDir/${package}.msc
    mv $workingDir/${package}_align.xml  $workingDir/${package}_align.msc
done

