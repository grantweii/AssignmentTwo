#version 120

uniform vec3 lightPosition;
uniform sampler2D texUnit;
uniform bool isNight;

varying float dist;
varying vec2 texCoordV;
varying vec3 v;
varying vec3 n;

void main (void) {	
    
    vec4 totalLight = vec4(1.0); 
   	
   	//if night, check if within spotlight
  	if (isNight) {
	  	vec3 lightDir = vec3(gl_LightSource[0].position-ecPos);
	     
	    /* compute the distance to the light source to a varying variable*/
	    dist = length(lightDir);
  		float NdotL = max(dot(n,normalize(lightDir)),0.0);
		if (NdotL > 0.0) {
	 
	 	//if within spotlight
	    float spotEffect = dot(normalize(gl_LightSource[0].spotDirection), normalize(-lightDir));
	    if (spotEffect > gl_LightSource[0].spotCosCutoff) {
	 		//SPOTLIGHT ILLUMINATION
	 		
	 		
	 		
	 		
	 		
	 		
	    } else { //else not within spotlight, set night light
	    	
	    }
  	} else {   	//else day light
	    totalLight = setDayLighting(totalLight);
	}
  	
  	gl_FragColor = texture2D(texUnit, texCoordV) * totalLight;
    //gl_FragColor = texture2D(texUnit,texCoordV) * gl_Color;
}

vec4 setDayLighting(varying vec4 totalLight) {
	vec3 l = normalize(lightPosition - v); 
    vec3 e = normalize(-v); 
    vec3 r = normalize(-reflect(l, n));  
    
	//ambient lighting
    vec4 l_ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
    l_ambient = clamp(l_ambient, 0.0, 1.0);
     
    //diffuse lighting
    vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0);
    l_diffuse = clamp(l_diffuse, 0.0, 1.0);
    
    //specular lighting
    vec4 l_specular = gl_FrontMaterial.specular 
                  * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess);
    l_specular = clamp(l_specular, 0.0, 1.0);

    //Final resulting light (ambient + diffuse + specular)
    totalLight = (l_ambient + l_diffuse + l_specular);
    totalLight = clamp(totalLight, 0.0, 1.0); 
    
    return totalLight;
}

vec4 setNightLighting(varying vec4 totalLight) {
	vec3 l = normalize(lightPosition - v); 
    vec3 e = normalize(-v); 
    vec3 r = normalize(-reflect(l, n));  
    
	//ambient lighting
    vec4 l_ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
    l_ambient = clamp(l_ambient, 0.0, 1.0);
     
    //diffuse lighting
    vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0);
    l_diffuse = clamp(l_diffuse, 0.0, 1.0);
    
    //specular lighting
    vec4 l_specular = gl_FrontMaterial.specular 
                  * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess);
    l_specular = clamp(l_specular, 0.0, 1.0);

    //Final resulting light (ambient + diffuse + specular)
    totalLight = (l_ambient + l_diffuse + l_specular);
    totalLight = clamp(totalLight, 0.0, 1.0); 
    
    return totalLight;
}