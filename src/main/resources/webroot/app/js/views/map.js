app.Map = function(params) {

    var locationParams = params.location;
	var map = new L.Map(params.elmId, {
			center: [ locationParams.center.lat, locationParams.center.lng ],
			zoom: locationParams.zoom
		})
		.addLayer(new L.TileLayer('http://{s}.tile.cloudmade.com/' + params.key + '/998/256/{z}/{x}/{y}.png'))
		.on("viewreset", onViewReset);

	var
		svg,
		g,

		selectedFeature,
		transform,
		path,
		clustersSelection,
		venuesSelection,
		layerPoints,

		colors = params.colors,
 		minCircle = 3,
    maxCircle = 30,

    tooltip;

	drawBorder(locationParams.bounds, map);
	svg = d3.select(map.getPanes().overlayPane).append("svg");
	g = svg.append("g").attr("class", "leaflet-zoom-hide");

	tooltip = Tooltip({
		elmId: "tooltip",
		width: 240
	});


	function drawBorder(bounds, map) {
		var pointSW = [bounds.sud, bounds.west];
		var pointNE = [bounds.north, bounds.est];
		var border = [pointSW, pointNE];
		var styles = { stroke: true, color: '#000', weight: 5, fillOpacity: 0 };
		L.rectangle(border, {className: 'border'}).addTo(map);
	}

	function initPath() {

		transform = d3.geo.transform({ point: function projectPoint(x, y) {
			var point = map.latLngToLayerPoint(new L.LatLng(y, x));
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


	function updateClustersSelection(selectedFeature, params) {
		// clustersSelection = svg polygon elements
		// representing clusters (hulls)

        var infoTemplate = _.template(d3.select('#cluster-tooltip').html());

		clustersSelection = g.selectAll("path.cluster")
			.data(selectedFeature.features, function(feature) {
				return feature.properties.clusterId;
			});

		clustersSelection.enter().append("path")
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
            .on("mouseout", tooltip.hideTooltip);

		clustersSelection.exit().remove();
	}


	function updateVenuesSelection(selectedFeature) {

        var infoTemplate = _.template(d3.select('#venue-tooltip').html());

		var venues = [];
		if (selectedFeature.features.length === 1) {
			var venues = getVenues(selectedFeature);
		}


		// function groupVenues(venues) {
		// 	 return _(venues).groupBy(function(v) {
		// 		 return v.category;
		// 	 });
		// }

  //       var catNames = _(groupVenues(venues)).keys();

		// var fillColor = d3.scale.ordinal()
		// 	 .domain(catNames)
		// 		.range(catNames.map(function(catName) { return colors[catName]; }));



		var maxBeenHere = _(venues).pluck('beenHere')[0];

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
				return radiusScale(d.beenHere) || 1;
			})
            // .style('fill', function(d) {
            //     return fillColor(d.category);
            // })
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


	function update(feature, params) {

		selectedFeature = feature;

		updateClustersSelection(selectedFeature, params);
		updateVenuesSelection(selectedFeature);

		onViewReset();

		// debugger;
		map.fitBounds(getAllCoords(selectedFeature).map(app.utils.inv));
	}


	function onViewReset() {

		initPath();

		var bounds = getBounds(selectedFeature),
			// var bounds = path.bounds(selectedFeature),
			topLeft = bounds[0],
			bottomRight = bounds[1],
            // padding = 2 * maxCircle;
            padding = Math.pow(maxCircle, map.getZoom() / 10);

		svg .attr("width", bottomRight[0] - topLeft[0] + (2 * padding))
				.attr("height", bottomRight[1] - topLeft[1] + (2 * padding))
				.style("left", (topLeft[0] - padding) + "px")
				.style("top", (topLeft[1] - padding) + "px");

		g	 .attr("transform", "translate(" + (-topLeft[0] + padding) + "," + (-topLeft[1] + padding) + ")");


		clustersSelection.attr("d", function(feature) {
				var geo = feature.geometry.geometries[1];
				var coords = geo.coordinates[0];
				coords.push(coords[0]);
				geo.coordinates = [coords];

				return path(geo);
		}.bind(this));


		venuesSelection.each(function(venue) {
			var point = map.latLngToLayerPoint(new L.LatLng(venue.venueLatitude, venue.venueLongitude));
			d3.select(this)
				.attr('cx', point.x)
				.attr('cy', point.y);
		});


		// removePoints();
		// removeVenues();

		// show only if one cluster
		if (selectedFeature.features.length === 1) {

			// addPoints();
			// addVenues();
		}
	}

	function addPoints(limit, everyNth) {

		var limit = limit || 1000;
		var everyNth = everyNth || 2;

		var markers = _(selectedFeature.features.map(function(feature) {
			var coordinates = feature.geometry.geometries[0].coordinates;
			return coordinates.map(app.utils.inv)
				.filter(function(coords, i) { return i % everyNth === 0; })
				.slice(0, limit)
				.map(function(coords) {
					return L.marker(coords, { icon: L.divIcon({ className: 'grid-dot' }) });
				});
		})).flatten();

		layerPoints = L.layerGroup(markers).addTo(map);
	}

	function removePoints(argument) {
		if (layerPoints) { map.removeLayer(layerPoints); }
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


	// function createMarkers(coords, icon) {
	//	 var options = {};
	//	 if (icon) { options.icon = icon; }
	//	 return coords.map(function(marker) {
	//		 return L.marker(marker, options);
	//			// .bindPopup(renderPopup()); // .addTo(map);
	//	 })
	//	 .filter(function(d, i) {
	//		 return i % 10 === 0;
	//	 });
	// }

	return {
        locationParams: locationParams,
		update: update,
		get: function() {
			return map;
		}
	}
};