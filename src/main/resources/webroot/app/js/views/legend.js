app.Legend = function() {

	var params, colors, featureCollection, categories;
	var $select = $('#select-location').find('select');
	var $showAll = $('#show-all');
	var $fullName = $('#full-name');

	var $legend = $('#legend')
		.on('click', 'input[type="checkbox"]', function() {
			var $checkbox = $(this);
			var action = ($checkbox.prop('checked') ? 'add' : 'remove');
			toggleCluster($checkbox.data().id, action);
		})
		.on('click', '.cat', function(e) {
			var catName = params.category && params.category[0];
			if (catName && $(this).data().name === catName) {
				return removeCategory(catName, e);
			}
		});

	// this.colors = colors;
	initSelectLoc(app.config.locations); // TODO global

	function initSelectLoc(locations) {
		$select.on('change', function() {
			// window.location.hash = '#!' + this.value;
			window.location.href = '/#!' + this.value;
			window.location.reload();
		});

		_(locations).keys().forEach(function(locName) {
			$('<option />')
				.text(locName)
				.val(locName)
				.appendTo($select)
		})
	}

	function clusterIds(from) {
		return from.map(function(cluster) { return cluster.properties.clusterId; });
	}


	function removeCategory(catName, e) {
		var removeClusters = clusterIds(categories[catName]);
		params.clusters = _.difference(params.clusters, removeClusters);
		location.hash = '#!' + params.location +
			/clusters/ + params.clusters.join(';');
		return !!e.preventDefault;
	}

	function toggleCluster(clusterId, action) {

		var clusters = params.clusters || [];

		if (action === 'add') {
			clusters.push(clusterId);
		}
		else if (action === 'remove') {
			clusters.splice(clusters.indexOf(clusterId), 1);
		}

		clusters = clusters.sort(function(n1, n2) {
			return n1 - n2;
		});

		location.hash = '#!' + params.location +
			// (params.category ? ('/category/' + params.category) : '') +
			/clusters/ + clusters.join(';');
	}


	function update(clusters, _params_, _colors_) {

		params = _params_;
		colors = _colors_;

		$fullName.html('<a target="_blank" href="' + params.locationParams.jsonUrl + '">' + params.locationParams.jsonUrl.replace(/^.*\/(.*)$/, '$1') + '</a>');

		$showAll.on('click', function() {
			window.location.href = '#!' + params.location;
		});
		$select.find('option[value="' + params.location + '"]').prop('selected', true);

		clusters.fetch(function(_featureCollection_) {

			featureCollection = _featureCollection_;

			var selectAll = !params.category && !params.clusters;
			if (selectAll) {
				params.clusters = clusterIds(featureCollection.features);
			}

			categories = _.groupBy(featureCollection.features, function(feature) {
				return feature.properties.name;
			});

			var list = _(categories).keys().sort().map(function(catName) {

				var niceName = /c\((.*?)\)/.exec(catName)[1].replace(/,/g, ', ');
				var label = niceName + ' (' + categories[catName].length + ')';
				var sublist = [], subListStr = '';
				var colorCategory = colors[catName];
				var hrefCategory = '#!' + params.location + '/category/' + catName;
				var isChecked = params.category && params.category[0] === catName;
				if (isChecked) {
					params.clusters = clusterIds(categories[catName]);
				}

				if (categories[catName].length > 0) {
					sublist = categories[catName]
						.sort(function(cluster1, cluster2) {
							return cluster1.properties.clusterId - cluster2.properties.clusterId;
						})
						.map(function(cluster) {
							var clusterId = cluster.properties.clusterId;
							var heterogeneity = cluster.properties.heterogeneity ? cluster.properties.heterogeneity : 0;
							var surface = cluster.properties.heterogeneity ? cluster.properties.surface : 0;
							var density = cluster.properties.density ? cluster.properties.density : 0;
							//var sse = cluster.properties.sse ? cluster.properties.sse.toFixed(6) : 0;
							var distance = cluster.properties.distance ? cluster.properties.distance.toFixed(3) : 0;
							var label = 'cluster ' + clusterId + 
								' <em> venue: number=' + cluster.properties.venues.length + 
								' , avg_distance=' + distance  + "Km" +
 								' , density=' + (1/5*density).toFixed(3) + "/Km^2; " +  //the 1/5=0.2 is just an ACK
								' cluster: surface=' + (surface * 100).toFixed(2) + "%" + 
								', heterogeneity=' + (heterogeneity * 100).toFixed(2) + "%" +
								'</em>';
							var hrefCluster = '#!' + params.location + '/clusters/' + clusterId;
							var isChecked = params.clusters && params.clusters.indexOf(clusterId) >= 0;
							isChecked = isChecked || selectAll;

							return '<li class="' + (isChecked ? 'selected' : '') + '">' +
										'<a href="' + hrefCluster + '">' +
											'<input data-id="' + clusterId + '" type="checkbox" ' + (isChecked ? 'checked' : '' )+ '/>' +
											label +
										'</a>' +
									'</li>';
						});
				}

				if (sublist.length > 0) {
					subListStr = '<ul>' + sublist.join('\n') + '</ul>';
				}

				return 	'<li style="border-left: 3px solid ' + colorCategory + '"">' +
							'<a href="' + hrefCategory + '" style="color: ' + colorCategory + '" class="cat" data-name="' + catName + '"> ' + label + '</a>' +
							subListStr +
						'</li>';
			});

			$legend.html(list.join('\n'));
		});
	}

	return {
		update: update
	};

};