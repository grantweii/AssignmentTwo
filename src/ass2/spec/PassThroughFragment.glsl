#version 120

//gl_Color is the input from the vertex shader interpolated
//gl_FragColor is the color we make the fragment


void main (void) {	  
	gl_FragColor = gl_Color; 		
}