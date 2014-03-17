app.Categories = function(clusterFeature) {

	var categories = {},
	colors = d3.scale.category20c();

	clusterFeature.features.forEach(addCluster);


	function addCluster(cluster) {

		var name = cluster.properties.name,
		index = _(categories).size();

		if (!categories[name]) {
			categories[name] = {
				type: 'FeatureCollection',
				properties: {
					name: name,
					color: colors(index)
				},
				features: []
			};
		}
		categories[name].features.push(cluster);
	}

	return {
		get: function(name) {
			if (name) { return categories[name]; }
			return categories;
		},
		getNames: function() {
			return _(categories).keys();
		},
		getList: function() {
			return _(categories).toArray();
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
		query: function(params, callback) {
			var selectedCategories = [];

			if (params.category) {
				params.category.forEach(function(catName) {
					var cat = this.get(catName);
					if (!cat) {
						throw new Error('router: the category is not found');
					}
					selectedCategories.push(cat);
				}.bind(this));

				if (params.cluster && selectedCategories.length > 0) {
					var selectedClusters = selectedCategories[0].features.filter(function(clusterObj) {
						return params.cluster.indexOf(clusterObj.id) >= 0;
					});

					selectedCategories = [this.templateFeature(selectedClusters, selectedCategories[0].properties)];
					if (selectedCategories[0].features.length === 0) {
						throw new Error('router: the cluster is not in the category');
					}
				}
			}
			else {
				selectedCategories = this.getList();
			}
			
			callback(this.templateFeature(selectedCategories));
		}
	};
};