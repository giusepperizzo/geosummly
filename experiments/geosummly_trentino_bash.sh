#!/bin/bash

java -jar geosummly.jar sampling -coord 46.53633684901995, 11.831262564937385,45.672795442796335,10.914315493899755 -output ./geosummly_output_trentino -sleep 730;
java -jar geosummly.jar discovery -input ./geosummly_output_trentino/density-transformation-matrix.csv -output ./geosummly_output_trentino -combination 3;
java -jar geosummly.jar clustering -density ./geosummly_output_trentino/density-transformation-matrix.csv -normalized ./geosummly_output_trentino/normalized-transformation-matrix.csv -deltad ./geosummly_output_trentino/deltad-values.csv -venues ./geosummly_output_trentino/singles-matrix.csv -output ./geosummly_output_trentino/clustering;
java -jar geosummly.jar evaluation -etype correctness -input ./geosummly_output_trentino/clustering-output.log -frequency ./geosummly_output_trentino/frequency-transformation-matrix.csv -output ./geosummly_output_trentino/clustering_correctness -mnum 500;
java -jar geosummly.jar evaluation -etype validation -input ./geosummly_output_trentino/clustering-output.log -venues ./geosummly_output_trentino/singles-matrix.csv -output ./geosummly_output_trentino/clustering_output_validation -fnum 10;