window.app = {
	init: function(location) {
		var hash = location.hash.slice(2);

		console.log(location);
		console.log(hash);

		this.config = this.Config();
		this.page = this.Page();

//        window.params
//        console.log(window.params);
		// TODO
		// jquery pattern style!
		this.router = this.Router();
		this.router.defineRoute(function(params) {
			var jsonUrl, locationParams;
            console.log(params);
			if (_.isEmpty(params)) {
				params = {
					location: app.config.defaultLoc
				};
			}
			console.log(params);
			locationParams = app.config.locations[params.location];
			params.locationParams = locationParams;
			params.key = app.config.key.leaflet;
			jsonUrl = locationParams.jsonUrl;
            //jsonUrl = 'data/milan-3cixty/yelp-100-1.geojson';

			this.page.init(this.Clusters(jsonUrl), params);

		}.bind(this));
		this.router.defineTrigger('data-href');

	},
	cache: {}
};