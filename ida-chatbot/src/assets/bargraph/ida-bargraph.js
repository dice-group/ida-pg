/**
 *
 */
function generateBarGraph(svg, data) {
	// requisite vars start
  if (typeof d3v4 == 'undefined')
    d3v4 = d3;

  let tempWidth = svg.node().parentNode.clientWidth;
  let tempHeight = svg.node().parentNode.clientHeight;

  let margin = {top: 20, right: 20, bottom: 30, left: 40};
  let width = +tempWidth - margin.left - margin.right;
  let height = +tempHeight - margin.top - margin.bottom;
  let g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	var x0 = d3.scaleBand().rangeRound([ 0, width ]).paddingInner(0.1);

	var x1 = d3.scaleBand().padding(0.05);

	var y = d3.scaleLinear().rangeRound([ height, 0 ]);

	var z = d3.scaleOrdinal().range(
			[ "#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c",
					"#ff8c00" ]);

	// requisite vars end

	var keys = data.keys;

	x0.domain(data.baritems.map(function(d) {
		return d[data.xaxisname];
	}));
	x1.domain(keys).rangeRound([ 0, x0.bandwidth() ]);
	y.domain([ 0, d3.max(data.baritems, function(d) {
		return d3.max(keys, function(key) {
			return d[data.yaxisname];
		});
	}) ]).nice();

	g.append("g").selectAll("g").data(data.baritems).enter().append("g").attr(
			"transform", function(d) {
				return "translate(" + x0(d[data.xaxisname]) + ",0)";
			}).selectAll("rect").data(function(d) {
		return keys.map(function(key) {
			return {
				key : key,
				value : d[data.yaxisname]
			};
		});
	}).enter().append("rect").attr("x", function(d) {
		return x1(d.key);
	}).attr("y", function(d) {
		return y(d.value);
	}).attr("width", x1.bandwidth()).attr("height", function(d) {
		return height - y(d.value);
	}).attr("fill", function(d) {
		return z(d.key);
	});

	g.append("g").attr("class", "axis").attr("transform",
			"translate(0," + height + ")").call(d3.axisBottom(x0));

	g.append("g").attr("class", "axis").call(d3.axisLeft(y).ticks(null, "s"))
			.append("text").attr("x", 2).attr("y", y(y.ticks().pop()) + 0.5)
			.attr("dy", "0.32em").attr("fill", "#000").attr("font-weight",
					"bold").attr("text-anchor", "start").text(data.yaxisname);

	var legend = g.append("g").attr("font-family", "sans-serif").attr(
			"font-size", 10).attr("text-anchor", "end").selectAll("g").data(
			keys.slice().reverse()).enter().append("g").attr("transform",
			function(d, i) {
				return "translate(0," + i * 20 + ")";
			});

	legend.append("rect").attr("x", width - 19).attr("width", 19).attr(
			"height", 19).attr("fill", z);

	legend.append("text").attr("x", width - 24).attr("y", 9.5).attr("dy",
			"0.32em").text(function(d) {
		return d;
	});
};

/*
var bgdata = {
	xaxisname : 'cityName',
	yaxisname : 'avgrent',
	keys : [ 'cityName' ],
	baritems : [ {
		cityName : 'Berlin',
		avgrent : 2600,
	}, {
		cityName : 'Paris',
		avgrent : 4000
	}, {
		cityName : 'Delhi',
		avgrent : 2500
	}, {
		cityName : 'Bengaluru',
		avgrent : 2000
	}, {
		cityName : 'Paderborn',
		avgrent : 2450
	}, {
		cityName : 'Palika Bazaar',
		avgrent : 150
	} ]

};

// function call
generateBarGraph(bgdata);*/
