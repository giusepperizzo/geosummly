geosummly
=========

####Geo Summarization Based on Crowd Sensors

geosummly is as a 4-states application, respectively:
* sampling: it performs the sampling of foursquare venues that are surrounded by a bounding box, and it generates a multidimensional tensor matrix where each dimension reports the magnitude of the Fourquare category vanue, and each object shapes a portion (cell) of the original 
bounding box;
* discovery: it estimates the parameter minpts;
* clustering: it performs the clustering algorithm;
* evaluation: it computes the SSE and the Jaccard as evaluation means of the obtained clustering output.


###CLI commands

#####sampling
```sh
-L –coord   <n,e,s,w>       set the input grid coordinates
-I –input   <path/to/file>  set the geojson input file
-O –output  <path/to/dir>   set the output directory
-v -vtype   <arg>           set the type of venue grouping. Allowed values: single, cell. Default single.
-l –ltype   <arg>           set the type of coordinates (latitude and longitude) normalization. Allowed values: norm, notnorm, missing. Default norm.
-g –gnum    <arg>           set the number of cells of a side of the squared grid. Default 20.
-r –rnum    <arg>           set the number of cells, taken randomly, chosen for the sampling.
-s –social  <arg>           set the social network for meta-data collection. So far only foursquare is activable. Default fourquare.
-z -sleep   <arg>           set the milliseconds between two calls to social media server. Default 0.
-C –cache                   cache activation. Default deactivated.
```
The options *coord*, *input* (only if *coord* is not specified), *output* are mandatory. The options *input* and *coord* are mutually exclusive. The options *input* and *gnum* are mutually exclusive. The options *input* and *rnum* are mutually exclusive.
The output consists of a file of single venues, a file of grid-shaped aggregated venues, a file of density values of the previous aggregates, a file with intra-feature normalized density values shifted in [0,1]. 

#####discovery
```sh
-I –input        <path/to/file>  set the csv input file
-O –output       <path/to/dir>   set the output directory
-c -combination  <arg>           set the number of categories combinations for minpts estimation. Default 5.
-r –rnum         <arg>           set the number of cells, taken randomly, chosen for the discovery operation.
```
The options *input*, *output* are mandatory. Input file has to be a .csv of grid-shaped density values. The output consists of a file of standard deviation values for the categories combinations.

#####clustering
```sh
-D –density     <path/to/file>  set the input file of density values
-N –normalized  <path/to/file>  set the input file of normalized density values
-S –deltad      <path/to/file>  set the input file of deltad values
-V –venues      <path/to/file>  set the input file of single venues
-O –output      <path/to/dir>   set the output directory
-M -method      <arg>           set the clustering algorithm. So far only geosubclu is activable. Default geosubclu.
-e -eps         <arg>           set the eps value of clustering algorithm. Default 0.1.
```
The options *density*, *normalized*, *deltad*, *venues*, *output* are mandatory. Density file has to be a .csv of grid-shaped density values, output the Sampling state. Normalized file has to be a .csv of grid-shaped normalized density values, output of the sampling state. Deltad file has to be a .csv of deltad values, output the Discovery state. Venues file has to be a .csv of single venues, output the Sampling state. The output consists of a .geojson file expressed as a feature collection whose features are the clusters, a set of RDF Turtle file (one for each cluster), a log file with the clustering informations.

#####evaluation
```sh
-E –etype      <arg>           set the operation to do. Allowed values: correctness, validation.
-I –input      <path/to/file>  set the log input file
-F -frequency  <path/to/file>  set the input file of frequency values
-V -venues     <path/to/file>  set the input file of single venues
-O –output     <path/to/dir>   set the output directory
-m –mnum       <arg>           set the random matrix number to create. Default 500.
-f –fnum       <arg>           set the fold number to create for the cross-validation. Default 10.
```

The options *etype*, *input*, *frequency* (only if etype is equal to correctness), *venues* (only if etype is equal to validation), *output* are mandatory. 
The input file has to be the log file returned by the clustering state.
If *etype* argument is equal to correctness, the *frequency* option (csv file of grid-shaped aggregates) has to be expressed mandatory and, for each of the *mnum* matrices, the output is: a random grid-shaped aggregates, a grid of density values of the previous aggregates, a grid with intra-feature normalized density values shifted in [0,1]. In addition to the output a SSE log and a R script (visualization of SSE values) are provided. Moreover *venues* and *fnum* options cannot be used.
If *etype* argument is equal to validation, the *venues* option (csv file of single venues) has to be expressed mandatory and, for each fold, the output is a file of density values and a file with intra-feature normalized density values shifted in [0,1]. In addition to the output a Jaccard log is provided. Moreover *frequency* and *mnum* options cannot be used.

#####more options
```sh
-H –help  print the command list 
```

###Examples

```sh
geosummly sampling –input path/to/file.geojson –output path/to/dir –vtype cell –ctype missing 

geosummly sampling –coord 45,8,44,7 –output path/to/dir –cnum 40 –snum 100

geosummly discovery –input path/to/file.csv –output path/to/dir –combination 3

geosummly clustering -density path/to/file1.csv -normalized path/to/file2.csv -deltad path/to/file3.csv -venues path/to/file4.csv -output path/to/dir

geosummly evaluation –etype correctness –input path/to/file.log -frequency path/to/file.csv –output path/to/dir –mnum 300

geosummly evaluation –etype validation –input path/to/file.log -venues path/to/file.csv –output path/to/dir
```    
