function Tooltip(params) {

	var _this = this;

	params = _.defaults(params, {
		elmId: 'tooltip',
		width: 240
	});

	var $tooltip = $('<div />')
		.addClass('tooltip')
		.attr('id', params.elmId)
		.css('width', params.width || '')
		.appendTo($('body'));

	hideTooltip();

	function showTooltip(content, event) {
		$tooltip.html(content).show();
		updatePosition(event);
	}
	
	function hideTooltip() {
		$tooltip.hide();
	}
	
	function updatePosition(event) {
		var xOffset = 20;
		var yOffset = 10;
		var $window = $(window);
		
		var ttw = $tooltip.width();
		var tth = $tooltip.height();
		var wscrY = $window.scrollTop();
		var wscrX = $window.scrollLeft();
		var curX = (document.all) ? event.clientX + wscrX : event.pageX;
		var curY = (document.all) ? event.clientY + wscrY : event.pageY;
		var ttleft = ((curX - wscrX + xOffset*2 + ttw) > $window.width()) ? curX - ttw - xOffset*2 : curX + xOffset;

		 if (ttleft < wscrX + xOffset){
		 	ttleft = wscrX + xOffset;
		 } 
		 var tttop = ((curY - wscrY + yOffset*2 + tth) > $window.height()) ? curY - tth - yOffset*2 : curY + yOffset;
		 if (tttop < wscrY + yOffset){
		 	tttop = curY + yOffset;
		 } 
		 $tooltip.css({
		 	'top': tttop + 'px',
		 	'left': ttleft + 'px'
		 });
	}
	
	return {
		showTooltip: showTooltip,
		hideTooltip: hideTooltip
	};
}
