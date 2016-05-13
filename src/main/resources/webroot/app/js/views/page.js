app.Page = function() {

	var map, legend, colors,
		loaderTimeout;

	function initMap(params, colors, callback) {

		if (!map) {
			map = app.Map({
				elmId: 'map',
				colors: colors,
				key: params.key,
				location: params.locationParams,
				configID: params.location
			}, callback);
		}
		return map;
	}

	function initLegend(colors) {
		if (!legend) {
			legend = app.Legend(colors);
		}
		return legend;
	}

	function initColors(clusters) {

		var colorsFun = d3.scale.category10();

		return clusters.getCategoryNames()
			.reduce(function(memo, cat, i) {
				memo[cat] = colorsFun(i);
				return memo;
			}, {});
	}

	function showLoader() {
		loaderTimeout = setTimeout(function() {
			$('#map-loader').fadeIn('fast');
		}, 200);
	}

	function hideLoader() {
		if (loaderTimeout) {
			clearTimeout(loaderTimeout);
		}
		$('#map-loader:visible').fadeOut('fast');

	}

	return {
		init: function(clusters, params) {
            console.log(params);
			var width = 300,
				$main = $('#main').css({
					width: window.innerWidth - width,
					height: window.innerHeight
				}),
				$sidebar = $('#sidebar').css({
					width: width,
					height: window.innerHeight
				}),
				$sidebarUl = $sidebar.find(">ul"),
				$map = $('#map-canvas');

			this.clusters = clusters;
			showLoader();
			this.clusters.fetch(function(clusterFeature) {

				clusters.query(params, function(filteredClusterFeature) {
					colors = initColors(clusters);
					// TODO.. bad api..
					map = initMap(params, colors);
					map.setOverlay(function() {
						map.init(filteredClusterFeature, params);
					});

					initLegend()
						.update(clusters, params, colors);
					hideLoader();
				});
			});
		}
	};
};