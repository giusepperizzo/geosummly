geosummly
=========

####Geo Summarization Based on Crowd Sensors

geosummly can be viewed as a 4-states application, respectively in this order:

* sampling: foursquare venues meta-data collection and transformation into a grid in order to obtain density values of venues categories with respect to geospatial coordinates.
* discovery: minpts parameter estimation.
* clustering: GeoSubClu algorithm application.
* evaluation: algorithm correctness and output validation.


###CLI commands

#####sampling
```sh
–coord   <n,s,w,e>       set the input grid coordinates
–input   <path/to/file>  set the geojson input file
–output  <path/to/dir>   set the output directory
-vtype   <arg>           set the type of venue grouping. Allowed values: single, cell. Default single.
–ctype   <arg>           set the type of coordinates normalization. Allowed values: norm, notnorm, missing. Default norm.
–cnum    <arg>           set the number of cells of a side of the squared grid. Default 20.
–snum    <arg>           set the number of cells, taken randomly, chosen for the sampling.
–social  <arg>           set the social network for meta-data collection. Default fourquare.
–cache                   cache activation. Default deactivated.
```
The options *coord*, *input* (only if *coord* is not specified), *output* are mandatory. The options *input* and *coord* are mutually exclusive. The options *input* and *cnum* are mutually exclusive. The options *input* and *snum* are mutually exclusive.
The output consist of a file of single venues, a file of grid-shaped aggregated venues, a file of density values of the previous aggregates, a file with intra-feature normalized density values shifted in [0,1]. 

#####discovery
```sh
–input        <path/to/file>  set the csv input file
–output       <path/to/dir>   set the output directory
-combination  <arg>           set the number of categories combinations for minpts estimation. Default 5.                             
```
The options *input*, *output* are mandatory. Input file has to be a .csv either of singles or grid-shaped venues. The output consist of a file of standard deviation values for the categories combinations.

#####clustering
```sh
–input   <path/to/file>  set the csv input file
–output  <path/to/dir>   set the output directory                           
```
The options *input*, *output* are mandatory. Input file has to be a .csv of grid-shaped normalized density values. The output consist of a list of triples (latitude, longitude, membership category).

#####evaluation
```sh
–etype   <arg>           set the operation to do. Allowed values: correctness, validation.
–input   <path/to/file>  set the csv input file
–output  <path/to/dir>   set the output directory
–mnum    <arg>           set the random matrix number to create. Default 500.
–fnum    <arg>           set the fold number to create for the cross-validation. Default 10.
```
The options *etype*, *input*, *output* are mandatory. If *etype* argument is equal to correctness, the input file has to be a .csv of grid-shaped aggregates, and the output is a set of random grid-shaped aggregates. Moreover *fnum* option cannot be used.
If *etype* argument is equal to validation, the input file has to be a .csv of single venues, and, for each fold, the output is the same as the one returned by the sampling state. Moreover *mnum* option cannot be used.

#####more options
```sh
–help  print the command list 
```

###Examples

```sh
geosummly sampling –input path/to/file.geojson –output path/to/dir –vtype cell –ctype missing 

geosummly sampling –coord 45,44,7,8 –output path/to/dir –cnum 40 –snum 100

geosummly discovery –input path/to/file.csv –output path/to/dir –combination 3

geosummly clustering –input path/to/file.csv –output path/to/dir

geosummly evaluation –etype validation –input path/to/file.csv –output path/to/dir

geosummly evaluation –etype correctness –input path/to/file.csv –output path/to/dir –mnum 300
```    
