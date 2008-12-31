#!/bin/sh

export KAPPA_FILE=SIA_2007_03_23-egfr.ka
export TIME=15
export RUN_JAVA_ITERATIONS=false

export OCAML_XML_FILE_PREFIX="ocaml/simulation"
export JAVA_XML_FILE_PREFIX="java/simulation"

../run-simulations-base.sh

