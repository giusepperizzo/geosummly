app.Map = function(params, callback) {

  var
    locationParams = params.location,

    map = new google.maps.Map(document.getElementById(params.elmId), {
      zoom: locationParams.zoom,
      center: new google.maps.LatLng(locationParams.center.lat, locationParams.center.lng),
      mapTypeId: google.maps.MapTypeId.ROADMAP
    }),

    svg,
    g,

    selectedFeature,
    transform,
    path,
    clustersSelection,
    venuesSelection,
    layerPoints,
    overlay,

    colors = params.colors,
    minCircle = 3,
    maxCircle = 30,

    tooltip = Tooltip({
      elmId: "tooltip",
      width: 240
    });

  function setOverlay(callback) {
    overlay = new google.maps.OverlayView();
    overlay.onAdd = function() {
      var overlayEl = this.getPanes().overlayLayer;
      g = d3.select(overlayEl).select('svg g');
      if (g.size() === 0) {
        overlayEl.parentNode.style.zIndex = "10000";
        //Vuk's solution: svg = d3.select(this.getPanes().overlayLayer).append("svg");
        svg = d3.select(this.getPanes().overlayLayer).append("svg");
        g = svg.append("g");// .attr("class", "leaflet-zoom-hide");
      }
      callback();
    };
    overlay.draw = function() {
      update();
    };
    overlay.setMap(map);
  }

  function drawBorder(bounds, map) {
    var pointSW = [bounds.sud, bounds.west],
        pointNE = [bounds.north, bounds.est],
        border = [pointSW, pointNE],
        styles = { stroke: true, color: '#000', weight: 5, fillOpacity: 0 };

    var rectangle = new google.maps.Rectangle({
      strokeColor: '#000',
      strokeWeight: 5,
      fillOpacity: 0,
      clickable: false,
      map: map,
      bounds: new google.maps.LatLngBounds(
        new google.maps.LatLng(bounds.sud, bounds.west),
        new google.maps.LatLng(bounds.north, bounds.est))
    });
  }

  function initPath() {
    transform = d3.geo.transform({ point: function projectPoint(x, y) {
      var projection = overlay.getProjection();
      var point = projection.fromLatLngToDivPixel(new google.maps.LatLng(y, x));
      this.stream.point(point.x, point.y);
    }});

    path = d3.geo.path().projection(transform);
  }

  function getBounds(feature) {
    var allBounds = feature.features.map(function(subfeature) {
      return path.bounds(subfeature);
    });

    var topLeftX = allBounds.map(function(bounds) { return bounds[0][0]; });
    var topLeftY = allBounds.map(function(bounds) { return bounds[0][1]; });
    var bottomRightX = allBounds.map(function(bounds) { return bounds[1][0]; });
    var bottomRightY = allBounds.map(function(bounds) { return bounds[1][1]; });

    var topLeftTotal = [Math.min.apply(null, topLeftX), Math.min.apply(null, topLeftY)];
    var bottomRightTotal = [Math.max.apply(null, bottomRightX), Math.max.apply(null, bottomRightY)];
    var boundsTotal = [topLeftTotal, bottomRightTotal];

    return boundsTotal;
  }

  function getAllCoords(feature) {
    var groupedCoords = feature.features.map(function(subfeature) {
      return subfeature.geometry.geometries[1].coordinates[0];
    });
    return _.flatten(groupedCoords, true);
  }

  function initClustersSelection(selectedFeature, params) {
    var infoTemplate = _.template(d3.select('#cluster-tooltip').html());

    // clustersSelection = svg polygon elements
    // representing clusters (hulls)
    clustersSelection = g.selectAll("path.cluster")
      .data(selectedFeature.features, function(feature) {
        return feature.properties.clusterId;
      });

    clustersSelection.enter()
      .append("path")
      .attr('data-href', function(feature) {
        var props = feature.properties,
          hash = [
            '#!' + params.location,
            // 'category', props.name,
            'clusters', props.clusterId
          ].join('/');
        return hash;
      })
      .classed('cluster', true)
      .style('fill', function(feature) {
        return colors[feature.properties.name];
      })
      .style('stroke', function(feature) {
        return colors[feature.properties.name];
      })
      .on("mouseover", function(d) {
        var data = {
          name: d.properties.name,
          idCluster: d.properties.idCluster,
          venuesLen: d.properties.venues.length
        }
        tooltip.showTooltip(infoTemplate({ data: data }), d3.event);
      })
      .on("mouseout", tooltip.hideTooltip)
      .on('click', function() {
      });

    clustersSelection.exit().remove();
  }

  function initVenuesSelection(selectedFeature) {

    var
      infoTemplate = _.template(d3.select('#venue-tooltip').html()),
      venues = [],
      maxBeenHere,
      radiusScale;

    if (selectedFeature.features.length === 1) {
      venues = getVenues(selectedFeature);
      maxBeenHere = _(venues).pluck('beenHere')[0];
    }

    var radiusScale = d3.scale.pow()
      .exponent(0.5)
      .domain([0, maxBeenHere])
      .range([minCircle, maxCircle]);

    // venuesSelection svg circle elements
    // representing venues
    venuesSelection = g.selectAll('circle')
      .data(venues, function(venue) {
        return venue.id;
      });

    venuesSelection.enter().append('circle')
      .attr('r', function(d) {
        console.log(d.beenHere, radiusScale(d.beenHere));
        return radiusScale(d.beenHere) || 1;
      })
      .on("mouseover", function(d) {
        d.date = moment(d.timestamp).fromNow();
        tooltip.showTooltip(infoTemplate({ data: d }), d3.event);
      })
      .on("mouseout", tooltip.hideTooltip)
      .on('click', function(d) {
        window.open(
          'https://foursquare.com/v/' + d.id,
          '_blank'
        );
      });

    venuesSelection.exit().remove();
  }

  function init(feature, params) {
    selectedFeature = feature;

    drawBorder(locationParams.bounds, map);

    if (selectedFeature.features.length > 0) {
      initClustersSelection(selectedFeature, params);
      initVenuesSelection(selectedFeature);
      // dirty hack..
      // if it's 'home' page of a location, don't do fitBounds
      // just show everything with the bbox
      if (location.hash.search(new RegExp(['clusters', 'category'].join('|'))) > -1) {
        fitToCurrent(selectedFeature);
      }
    }
    else {
      alert('Wrong cluster or category!')
    }
  }

  function fitToCurrent(selectedFeature) {
    var latLngList = getAllCoords(selectedFeature)
      .map(app.utils.inv)
      .map(function(pair) {
        return new google.maps.LatLng(pair[0], pair[1]);
      });
    var bounds = new google.maps.LatLngBounds();
    //  Go through each...
    for (var i = 0, ltLgLen = latLngList.length; i < ltLgLen; i++) {
      //  And increase the bounds to take this point
      bounds.extend(latLngList[i]);
    }
    //  Fit these bounds to the maps
    map.fitBounds(bounds);
  }

  function update() {
    initPath();

    var
      bounds = getBounds(selectedFeature),
      topLeft = bounds[0],
      bottomRight = bounds[1],
      padding = Math.pow(maxCircle, map.getZoom() / 10);

    svg
      .attr("width", bottomRight[0] - topLeft[0] + (2 * padding))
      .attr("height", bottomRight[1] - topLeft[1] + (2 * padding))
      .style("left", (topLeft[0] - padding) + "px")
      .style("top", (topLeft[1] - padding) + "px");

    g.attr("transform", "translate(" + (-topLeft[0] + padding) + "," + (-topLeft[1] + padding) + ")");

    clustersSelection.attr("d", function(feature) {
      var geo = feature.geometry.geometries[1];
      var coords = geo.coordinates[0];
      coords.push(coords[0]);
      geo.coordinates = [coords];

      return path(geo);
    }.bind(this));

    venuesSelection.each(function(venue) {
      var projection = overlay.getProjection();
      var point = projection.fromLatLngToDivPixel(new google.maps.LatLng(venue.venueLatitude, venue.venueLongitude));

      d3.select(this)
        .attr('cx', point.x)
        .attr('cy', point.y);
    });
  }

  function getVenues(selectedFeature, limit) {

    limit = limit || 300;

    var coords, marker,
        markers = [];

    var feature = selectedFeature.features[0];
    var categoriesToShow = categoriesFromFeature(feature.properties.name);
    var venues = selectedFeature.features[0].properties.venues;

    var venuesInCategory = venues.filter(function(venue) {
      return (categoriesToShow.indexOf(venue.category) >= 0);
    });

    // if there are no venues that share the same categories as the cluster
    // show all the venues
    if (venuesInCategory.length > 0) {
      venues = venuesInCategory;
    }

    return venues.sort(sortVenue)
      .slice(0, limit);

    function sortVenue(v1, v2) {
      // beenHere is sometimes undefined!
      // this caused sort to fail, that led to huge circle bug
      return (v2.beenHere || 0) - (v1.beenHere || 0);
    }

    function categoriesFromFeature(categoryName) {
      var regex = /c\((.*?)\)/;
      var matched = regex.exec(categoryName);

      if (matched) {
        return matched[1].split(',');
      }
      return null;
    }
  }

  return {
    locationParams: locationParams,
    init: init,
    setOverlay: setOverlay,
    get: function() {
      return map;
    }
  };
};