#!/bin/sh

export KAPPA_FILE=${DATA_DIR}/fgf2-fgf.ka
export TIME=15
export RUN_JAVA_ITERATIONS=false

export OCAML_XML_FILE_PREFIX="ocaml/simulation"
export JAVA_XML_FILE_PREFIX="java/simulation"

../run-simulations-base.sh


