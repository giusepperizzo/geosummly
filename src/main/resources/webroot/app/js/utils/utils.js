app.utils = {
	j: function(obj, fn) {
	  return JSON.stringify(obj, fn || null, 2);
	},

	reduceDecimals: function(number, places) {
	  places = places || 100;
	  return Math.round(number * 100) / 100;
	},

	inv: function(coord) {
	  return [coord[1], coord[0]];
	}
};