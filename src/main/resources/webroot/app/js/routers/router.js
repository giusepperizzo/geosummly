// POSSIBLE CATEGORY EXAMPLES:
// ["c(Food)", "c(Arts & Entertainment,Food)", "c(Professional & Other Places)", "c(Arts & Entertainment)", "c(Nightlife Spot)", "c(Arts & Entertainment,Nightlife Spot)", "c(Residence)"]

app.Router = function() {

	var regexp = /^#!([^\/]*?)(?:\/(category)\/(.*?))?(?:\/(clusters)\/(.*?))?\/?$/;

	function urlToParams(hash) {

		var params = {},
			paramList = regexp.exec(hash);

		if (!paramList) { return {}; }

		if (paramList[1]) 				  { params.location = paramList[1]; }
		if (paramList[2] && paramList[3]) { params[paramList[2]] = paramList[3]; }
		if (paramList[4] && paramList[5]) { params[paramList[4]] = paramList[5]; }

		return params;
	}

	return {
		defineRoute: function(routeCallback) {
			$.route(function(hash) {
			    console.log(hash);  //one problem is here, the hash is incorrect
				var params = urlToParams(hash);
                console.log(params);
				params = this.prepareParams(params);

                console.log($);
                console.log(params);
				console.log('route');
				routeCallback(params);

				}.bind(this));
			return this;
		},

		defineTrigger: function(selector) {

			document.body.addEventListener('click', function(e) {
				var hash = d3.select(e.target).attr(selector);
				if (hash) {
					window.location.hash = hash;
				}
			});
			return this;
		},

		prepareParams: function(params) {

			if (!params) { params = {}; }
			if (params.category) {
				params.category = params.category.split(';');
			}

			if (params.clusters) {
				// if (!params.category || params.category.length !== 1) {
				// 	throw new Error('router: please provide one category');
				// }

				params.clusters = params.clusters.split(';');

				params.clusters = params.clusters.map(function(cluster) {
					cluster = parseInt(cluster, 10);
					if (!_(cluster).isNumber()) {
						throw new Error('router: the cluster must be number');
					}
					return cluster;
				});
			}
			return params;
		}
	};
};