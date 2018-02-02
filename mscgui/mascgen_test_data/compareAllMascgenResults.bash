#!/bin/bash

packages=( q0207_pa_0 \
    q0207_pa_35 \
    q0207_pa_75 \
    q1442_pa_10 \
    q1442_pa_90 \
    q1700_pa_0 \
    q1700_pa_45 \
    q1700_pa_90 \
    q1700_shift_legal_range )

for package in "${packages[@]}"
do
    echo "################## $package #######################"
    ./compareMascgenResults.bash $package
done

