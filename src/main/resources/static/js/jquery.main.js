$(document).ready(function(){

	jQuery('#example-rand').jstars({
		  image_path: 'images', // this is requried option
		  image: 'jstar-map.png', // this is requried option
		  style: 'rand',
		  frequency: 30,
		  style_map: { 
			    white: 0,
			    blue: -27,
			    green: -54,
			    red: -81,
			    yellow: -108
			  },
			  width: 27, // single star width
			  height: 27, // single star height
			  delay: 300 // rotate speed


		});                  

})



