#!/bin/sh

export KAPPA_FILE=large_systems-sysepi.ka
export TIME=1000
export RUN_JAVA_ITERATIONS=false

export OCAML_XML_FILE_PREFIX="ocaml/simulation"
export JAVA_XML_FILE_PREFIX="java/simulation"

../run-simulations-base.sh

