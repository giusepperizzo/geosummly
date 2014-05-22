# geosummly - Geographic Summaries from Crowdsourced Data

[geosummly][geosummly] is as a framework that creates geographic summaries using the whereabouts of Foursquare users. Exploiting the density of the venue types in a particular region, the system adds a layer over any typical cartography geographic maps service, creating a first glance summary over the venues sampled from the Foursquare
knowledge base. Each summary is represented by a convex hull. The shape is automatically computed according to the venue densities enclosed in the area. The summary is then labeled with the most prominent category or categories. The prominence is given by the observed venue category density. A set of summaries are provided in the public [demonstration][demonstration].

## Architectural Overview 
The prototype is composed of 6 stages:
* sampling: it performs the sampling of foursquare venues that are surrounded by a bounding box, and it records this informations on a matrix;
* import: it generates a multidimensional tensor matrix, given the sampled data, where each dimension reports the magnitude of the Fourquare category venue, and each object shapes a portion (cell) of the original bounding box;
* discovery: it estimates the parameter minpts;
* clustering: it performs the clustering algorithm;
* evaluation: it computes the SSE and the Jaccard as evaluation means of the obtained clustering output.
* optimization: it performs the Pareto distribution on the clustering output by exploiting 3 optimization functions: cluster spatial coverage, cluster density, cluster heterogeneity.

Please refer to our [paper][paper] for a detailed description. 

## Requirements
* Java 8+
* Maven 3+
* [elki v0.6][elki] (require mvn install)
* [foursquare-api-java v1.0.3][fi] (require mvn install)
* [simjoin][simjoin] (require mvn install)
* [common.csv][csv] (require mvn install)
* [Foursquare app][4sqrApp] (requre to instanciate an app and to get two auth strings such as Client ID, Client Secret)

## Setting Up 
    git clone git@github.com:giusepperizzo/geosummly.git
    cp props/config.properties.default props/config.properties 
    vim props/config.properties (add ClientID, and ClientSecret)
    mvn clean
    mvn package

## CLI API
For a full list of commands, please refer to:

    geosummly -H –help  (print the command list)

### sampling
```sh
-L –coord   <n,e,s,w>       set the input grid coordinates
-I –input   <path/to/file>  set the geojson input file
-O –output  <path/to/dir>   set the output directory
-g –gnum    <arg>           set the number of cells of a side of the squared grid. Default 20.
-r –rnum    <arg>           set the number of cells, taken randomly, chosen for the sampling.
-s –social  <arg>           set the social network for meta-data collection. So far only foursquare is activable. Default fourquare.
-z -sleep   <arg>           set the milliseconds between two calls to social media server. Default 0.
-C –cache                   cache activation. Default deactivated.
```
The options *coord*, *input* (only if *coord* is not specified), *output* are mandatory. The options *input* and *coord* are mutually exclusive. The options *input* and *gnum* are mutually exclusive. The options *input* and *rnum* are mutually exclusive.
The output consists of a file of single venues for each of the two levels of the Foursquare categories taxonomy, a log file with the sampling informations. 

    geosummly sampling –input path/to/file.geojson –output path/to/dir –ctype missing 
    geosummly sampling –coord 45,8,44,7 –output path/to/dir –gnum 40 –rnum 100


### import
```sh
-I –input   <path/to/file>  set the csv input file
-L –coord   <n,e,s,w>       set the bounding box coordinates
-g –gnum    <arg>           set the number of cells of a side of the squared grid. Default 20.
-O –output  <path/to/dir>   set the output directory
-l –ltype   <arg>           set the type of coordinates (latitude and longitude) normalization. Allowed values: norm, notnorm, missing. Default norm.
```
The options *input*, *coord*, *gnum*, *output* are mandatory." Input file has to be a .csv of single venues, output of the sampling state. The output consist of a file of grid-shaped aggregated venues, a file of density values of the previous aggregates, a file with intra-feature normalized density values shifted in [0,1].

    geosummly import -input path/to/file.csv -coord 48,8,44,7 \
     -gnum 100 -output path/to/dir -ltype notnorm

### discovery
```sh
-I –input        <path/to/file>  set the csv input file
-O –output       <path/to/dir>   set the output directory
-c -combination  <arg>           set the number of categories combinations for minpts estimation. Default 5.
-r –rnum         <arg>           set the number of cells, taken randomly, chosen for the discovery operation.
```
The options *input*, *output* are mandatory. Input file has to be a .csv of grid-shaped density values. The output consists of a file of standard deviation values for the categories combinations.

    geosummly discovery –input path/to/file.csv –output path/to/dir –combination 3

#####clustering
```sh
-D –density     <path/to/file>  set the input file of density values
-N –normalized  <path/to/file>  set the input file of normalized density values
-S –deltad      <path/to/file>  set the input file of deltad values
-V –venues      <path/to/file>  set the input file of single venues
-L -coord       <n,e,s,w>       set the bounding box coordinates
-O –output      <path/to/dir>   set the output directory
-M -method      <arg>           set the clustering algorithm. So far only geosubclu is activable. Default geosubclu.
-e -eps         <arg>           set the eps value of clustering algorithm. Default sqrt(2) * (1/ sqrt( size(density_values) )).
```
The options *density*, *normalized*, *deltad*, *venues*, *coord*, *output* are mandatory. Density file has to be a .csv of grid-shaped density values, output the import state. Normalized file has to be a .csv of grid-shaped normalized density values, output of the import state. Deltad file has to be a .csv of deltad values, output the discovery state. Venues file has to be a .csv of single venues, output the sampling state. The output consists of a .geojson file expressed as a feature collection whose features are the clusters, a set of RDF Turtle file (one for each cluster), a log file with the clustering informations.

    geosummly clustering -coord 45,8,44,7 -density path/to/file1.csv \
     -normalized path/to/file2.csv -deltad path/to/file3.csv \
     -venues path/to/file4.csv -output path/to/dir


### evaluation
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
If *etype* argument is equal to correctness, the *frequency* option (csv file of grid-shaped aggregates) is mandatory and, for each of the *mnum* matrices, the output is: a random grid-shaped aggregates, a grid of density values of the previous aggregates, a grid with intra-feature normalized density values shifted in [0,1]. In addition to the output a SSE log and a R script (visualization of SSE values) are provided. Moreover *venues* and *fnum* options cannot be used.
If *etype* argument is equal to validation, the *venues* option (csv file of single venues) is mandatory and, for each fold, the output is a file of density values and a file with intra-feature normalized density values shifted in [0,1]. In addition to the output a Jaccard log is provided. Moreover *frequency* and *mnum* options cannot be used.

    geosummly evaluation –etype correctness –input path/to/file.log \
     -frequency path/to/file.csv –output path/to/dir –mnum 300
    geosummly evaluation –etype validation –input path/to/file.log \
     -venues path/to/file.csv –output path/to/dir


### optimization
```sh
-I –input    <path/to/file>  set the geojson input file
-i -infos    <path/to/file>  set the log input file
-O -output   <path/to/dir>   set the output directory
-t -top      <arg>           set the number of clusters to hold in the fingerprint. Default 10.
-w -weight   <w1,w2,w3>      set the weights to assign to each optimization function. Default 0.3.
```

The options *input*, *infos*, *output* are mandatory.
Input file has to be a geojson file, output of the clustering state.
Infos file has to be a log file, output of the sampling state.
The output consists of a log file, a geojson file with the clustering result after the optimization.

    geosummly optimization -input path/to/file.geojson \
     -infos path/to/file1.log -output path/to/dir \
     -weight 0.5,0.2,0.3 -top 5

## Web UI
A web interface can be instantiated to visualize on a map the output of the geosummly.

    java -cp /path/where/geosummly/jar/is/located it.unito.geosummly.Server 8080
    
A set of pre-computed fingerprints will be shown. To add new ones, just copy the geojson of the clustering stage into the webroot/app/data folder, and then add the new location to webroot/app/js/config/config.js.

## License
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License http://www.apache.org/licenses/LICENSE-2.0. Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. The license is applicable to the modules developed in this project.

## Citation
If you want to cite this work, please use the following citation.

    Rizzo G., Falcone G., Meo R., Pensa R., Troncy R., Milicic V. (2014), 
    Geographic Summaries from Crowdsourced Data. 
    In 11th Extended Semantic Web Conference (ESWC'14) Poster Demo Session, Hersonissou, Crete, Greece


[geosummly]: https://github.com/giusepperizzo/geosummly
[demonstration]: http://geosummly.eurecom.fr/
[paper]: http://www.di.unito.it/~rizzo/publications/Rizzo_Falcone-ESWC2014.pdf 
[elki]: http://elki.dbs.ifi.lmu.de/wiki/Releases
[fi]: https://github.com/wallabyfinancial/foursquare-api-java
[simjoin]: https://code.google.com/p/similarity-join-tools
[csv]: http://commons.apache.org/proper/commons-csv
[4sqrApp]: https://developer.foursquare.com
