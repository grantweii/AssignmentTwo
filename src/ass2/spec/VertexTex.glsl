#version 120

attribute vec3 coordinates;
attribute vec3 normals;
attribute vec2 textures;

varying vec4 ecPos;
varying vec3 v;
varying vec3 n;
varying vec2 texCoordV;

void main (void) {
    
    gl_Position =  gl_ModelViewProjectionMatrix * vec4(coordinates, 1.0);
    
    ecPos = gl_ModelViewProjectionMatrix * gl_Position;
    
 	// transform vertex coordinates to eye space
  	v = vec3(gl_ModelViewMatrix * vec4(coordinates, 1.0));
  	n = normalize(gl_NormalMatrix * normals);
	  
  	//passthrough colors
  	gl_FrontColor = gl_Color;
  	gl_BackColor = gl_Color;

  	texCoordV = textures;
    
}