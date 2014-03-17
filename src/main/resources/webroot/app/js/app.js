window.app = {
	init: function(locaation) {
		var hash = location.hash.slice(2);

		this.config = this.Config();
		this.page = this.Page();

		// TODO
		// jquery pattern style!
		this.router = this.Router();
		this.router.defineRoute(function(params) {
			var jsonUrl, locationParams;

			if (_.isEmpty(params)) {
				params = {
					location: app.config.defaultLoc
				};
			}

			locationParams = app.config.locations[params.location];
			params.locationParams = locationParams;
			params.key = app.config.key.leaflet;
			jsonUrl = locationParams.jsonUrl;

			this.page.init(this.Clusters(jsonUrl), params);

		}.bind(this));
		this.router.defineTrigger('data-href');

	},
	cache: {}
};