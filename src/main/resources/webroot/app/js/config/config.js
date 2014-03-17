app.Config = function() {
	
	function Location(params) {
		this.bounds = params.bounds;
		this.jsonUrl = params.jsonUrl;
		this.zoom = params.zoom;
		this.getCenter();
	}
	Location.prototype.getCenter = function() {
		if (!this.center) {
			this.center = {
				lat: this.bounds.sud + ((this.bounds.north - this.bounds.sud) / 2),
				lng: this.bounds.west + ((this.bounds.est - this.bounds.west) / 2 )
			};
		}
		return this.center;
	};

	return {
		defaultLoc: 'trentino',
		key: {
			leaflet: '5f9ebf625acb4df3a40163ddca8c064b'
		},
		locations: {
			milan: new Location({
				jsonUrl: 'data/milan/clustering-output-eps0.09.geojson',
				bounds: {
					north: 45.56673320779651,
					sud: 45.35774348391226,
					west: 9.012991053852822,
					est: 9.311176701852737
				},
				zoom: 11
			}),
			trentino: new Location({
				jsonUrl: 'data/trentino/clustering-output-eps0.09.geojson',
				bounds: {
					north: 46.53633684901995,
					sud: 45.672795442796335,
					west: 10.914315493899755,
					est: 11.831262564937385
				},
				zoom: 11
			})
		}
	};
};