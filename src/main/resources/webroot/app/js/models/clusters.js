app.Clusters = function(jsonUrl) {

  var clusterFeature = {},
    transform = d3.geo.transform();
      path = d3.geo.path().projection(transform);

  function prepareData(data) {

    var feature, i;
    clusterFeature = data;

    for (i = 0; i < clusterFeature.features.length; i++) {

      feature = clusterFeature.features[i];
      feature.geometry.coordinates = inverseCoords(feature.geometry.coordinates);

      try {
        feature.geometry = generateHulls(feature.geometry);
        feature.properties.area = calculateArea(feature.geometry.geometries[1]) || 0;
      }
      catch(err) {

        // When one coordinate always has the same value (all the points are in one line)
        // d3.geom.hull() will raise a TypeError
        // In that case the cluster is removed from the array
        // Example:
        // [[ 11.16370494, 45.91714919 ], [ 11.16874312, 45.91714919 ], [ 11.1888958, 45.91714919 ]]

        clusterFeature.features.splice(i, 1);
        i--;
      }
    }

    // temporarly commented
    // kill if area is zero
//    clusterFeature.features = clusterFeature.features.filter(function(feature) {
//      if(feature.properties.area == 0 ) console.log("empty area, then removed clusterId:" + (feature.id + 1) );
//      return feature.properties.area !== 0;
//    });

    clusterFeature.features = clusterFeature.features.sort(sortByArea);
    return clusterFeature;
  }

  function inverseCoords(coords) {
    return coords.map(app.utils.inv);
  }

  function generateHulls(geometry) {
    var geoCollection = {
      type: 'GeometryCollection',
      geometries: []
    };

    geoCollection.geometries = [
      geometry,
      {
        type: 'Polygon',
        coordinates: [d3.geom.hull(geometry.coordinates)]
      }
    ];
    return geoCollection;
  }

  function calculateArea(geometry) {
    //console.log(path.area(geometry));
    return path.area(geometry);
  }

  function sortByArea(feature1, feature2) {
    return feature2.properties.area - feature1.properties.area;
  }

  return {
    query: function(params, callback) {
      var filteredClusters = clusterFeature.features;

      if (params.category) {
        filteredClusters = clusterFeature.features.filter(function(feature) {
          return params.category.indexOf(feature.properties.name) >= 0;
        });
      }
      if (params.clusters) {
        filteredClusters = clusterFeature.features.filter(function(feature) {
          return params.clusters.indexOf(feature.properties.clusterId) >= 0;
        });
      }

      callback(this.templateFeature(filteredClusters));
    },
    templateFeature: function(features, properties) {
      if (!features || !_(features).isArray()) {
        throw new Error('route: features must be array');
      }
      var templateObj = {
        collection: 'FeatureCollection',
        features: features
      };
      if (properties) {
        templateObj.properties = properties;
      }

      return templateObj;
    },
    getCategoryNames: function() {
      var names = clusterFeature.features.map(function(feature) {
          return feature.properties.name;
        });
        return _(names).uniq();
    },
    fetch: function(callback) {

      if (app.cache[jsonUrl]) {
        clusterFeature = app.cache[jsonUrl]
        callback(clusterFeature);
      }
      else {
        d3.json(jsonUrl, function(data) {
          clusterFeature = prepareData(data);
          app.cache[jsonUrl] = clusterFeature;
          callback(clusterFeature);
        });
      }
    },
    feature: function() {
      return clusterFeature;
    }
  }
};