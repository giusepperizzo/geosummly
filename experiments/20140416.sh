OUT_MILAN_BD=datasets/milanbigdata/20140416;
OUT_MILAN_CITY=datasets/milancitycenter/20140416;
OUT_TRENTINO_BD=datasets/trentinobigdata/20140416;

###
## Milan - BigDataChallenge
###
java -jar geosummly.jar sampling -input geojson_input_dataset/milano-grid.geojson \ 
 -output $OUT_MILAN_BD -sleep 730;

java -jar geosummly.jar discovery -input $OUT_MILAN_BD/density-transformation-matrix.csv \
 -output $OUT_MILAN_BD -combination 4;

java -jar geosummly.jar clustering -density $OUT_MILAN_BD/density-transformation-matrix.csv \
 -normalized $OUT_MILAN_BD/normalized-transformation-matrix.csv \
 -deltad $OUT_MILAN_BD/deltad-values.csv \
 -venues $OUT_MILAN_BD/singles-matrix.csv \
 -output $OUT_MILAN_BD/clustering ;

java -jar geosummly.jar evaluation -etype correctness \
 -input $OUT_MILAN_BD/clustering.log \
 -frequency $OUT_MILAN_BD/frequency-transformation-matrix.csv \
 -output $OUT_MILAN_BD/clustering_correctness -mnum 500;

java -jar geosummly.jar evaluation -etype validation \
 -input $OUT_MILAN_BD/clustering.log \
 -venues $OUT_MILAN_BD/singles-matrix.csv \
 -output $OUT_MILAN_BD/clustering_output_validation -fnum 10;

###
## Milan - Only city center 
###
java -jar geosummly.jar sampling -coord 45.51597051718207,9.24774169921875,45.42158812329078,9.11590576171875 \ 
 -output $OUT_MILAN_CITY -gnum 100 -sleep 730;

java -jar geosummly.jar discovery -input $OUT_MILAN_CITY/density-transformation-matrix.csv \
 -output $OUT_MILAN_CITY -combination 4;

java -jar geosummly.jar clustering -density $OUT_MILAN_CITY/density-transformation-matrix.csv \
 -normalized $OUT_MILAN_CITY/normalized-transformation-matrix.csv \
 -deltad $OUT_MILAN_CITY/deltad-values.csv \
 -venues $OUT_MILAN_CITY/singles-matrix.csv \
 -output $OUT_MILAN_CITY/clustering ;

java -jar geosummly.jar evaluation -etype correctness \
 -input $OUT_MILAN_CITY/clustering.log \
 -frequency $OUT_MILAN_CITY/frequency-transformation-matrix.csv \
 -output $OUT_MILAN_CITY/clustering_correctness -mnum 500;

java -jar geosummly.jar evaluation -etype validation \
 -input $OUT_MILAN_CITY/clustering.log \
 -venues $OUT_MILAN_CITY/singles-matrix.csv \
 -output $OUT_MILAN_CITY/clustering_output_validation -fnum 10;

###
## Trentino - BigDataChallenge
###
java -jar geosummly.jar sampling -input geojson_input_dataset/trentino-grid.geojson \ 
 -output $OUT_TRENTINO_BD -sleep 730;

java -jar geosummly.jar discovery -input $OUT_TRENTINO_BD/density-transformation-matrix.csv \
 -output $OUT_TRENTINO_BD -combination 4;

java -jar geosummly.jar clustering -density $OUT_TRENTINO_BD/density-transformation-matrix.csv \
 -normalized $OUT_TRENTINO_BD/normalized-transformation-matrix.csv \
 -deltad $OUT_TRENTINO_BD/deltad-values.csv \
 -venues $OUT_TRENTINO_BD/singles-matrix.csv \
 -output $OUT_TRENTINO_BD/clustering ;

java -jar geosummly.jar evaluation -etype correctness \
 -input $OUT_TRENTINO_BD/clustering.log \
 -frequency $OUT_TRENTINO_BD/frequency-transformation-matrix.csv \
 -output $OUT_TRENTINO_BD/clustering_correctness -mnum 500;

java -jar geosummly.jar evaluation -etype validation \
 -input $OUT_TRENTINO_BD/clustering.log \
 -venues $OUT_TRENTINO_BD/singles-matrix.csv \
 -output $OUT_TRENTINO_BD/clustering_output_validation -fnum 10;