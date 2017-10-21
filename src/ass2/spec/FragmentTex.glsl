#version 120

uniform vec3 lightPosition;
uniform sampler2D texUnit;

varying vec2 texCoordV;
varying vec3 v;
varying vec3 n;

void main (void) {	
    
    vec4 totalLight = vec4(1.0); //identity matrix
  
	    //Gather required variables
	    vec3 l = normalize(lightPosition - v); //normalise light source
	    vec3 e = normalize(-v); // we are in Eye Coordinates, so EyePos is (0,0,0)
	    vec3 r = normalize(-reflect(l, n));  
	    
	    //Calculate ambient lighting
	    vec4 l_ambient = gl_FrontMaterial.ambient;
	    l_ambient = clamp(l_ambient, 0.0, 1.0);
	     
	    //Calculate diffuse lighting
	    vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0);
	    l_diffuse = clamp(l_diffuse, 0.0, 1.0);
	    
	    //Calculate specular lighting
	    vec4 l_specular = gl_FrontMaterial.specular 
	                  * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess);
	    l_specular = clamp(l_specular, 0.0, 1.0);
	
	    //Final resulting light (ambient + diffuse + specular)
	    totalLight = (l_ambient + l_diffuse + l_specular);
	    totalLight = clamp(totalLight, 0.0, 1.0); 
  	
  	
  	gl_FragColor = texture2D(texUnit, texCoordV) * totalLight;
    //gl_FragColor = texture2D(texUnit,texCoordV) * gl_Color;
}