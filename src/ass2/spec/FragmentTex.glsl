#version 120

varying vec2 texCoordV;

uniform sampler2D texUnit;



void main (void) {	
   
	gl_FragColor = texture2D(texUnit,texCoordV);
    //gl_FragColor = texture2D(texUnit,texCoordV) * gl_Color;
}