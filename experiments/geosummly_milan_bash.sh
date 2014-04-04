#!/bin/bash

java -jar geosummly.jar sampling -input ./milano-grid.geojson -output ./geosummly_output_milan -sleep 730;
java -jar geosummly.jar discovery -input ./geosummly_output_milan/density-transformation-matrix.csv -output ./geosummly_output_milan -combination 3;
java -jar geosummly.jar clustering -density ./geosummly_output_milan/density-transformation-matrix.csv -normalized ./geosummly_output_milan/normalized-transformation-matrix.csv -deltad ./geosummly_output_milan/deltad-values.csv -venues ./geosummly_output_milan/singles-matrix.csv -output ./geosummly_output_milan/clustering -eps 0.09;
java -jar geosummly.jar evaluation -etype correctness -input ./geosummly_output_milan/frequency-transformation-matrix.csv -density ./geosummly_output_milan/density-transformation-matrix.csv -deltad ./geosummly_output_milan/deltad-values.csv -output ./geosummly_output_milan/clustering_correctness -mnum 500;
java -jar geosummly.jar evaluation -etype validation -input ./geosummly_output_milan/singles-matrix.csv -density ./geosummly_output_milan/density-transformation-matrix.csv -deltad ./geosummly_output_milan/deltad-values.csv -output ./geosummly_output_milan/clustering_output_validation -fnum 10
