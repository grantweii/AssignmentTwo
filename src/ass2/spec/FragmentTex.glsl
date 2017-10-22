#version 120

uniform vec3 lightPosition;
uniform sampler2D texUnit;
uniform bool nightMode;
uniform int avatarRotation;
uniform vec3 enemyPos;

varying vec4 ecPos;
varying vec2 texCoordV;
varying vec3 v;
varying vec3 n;

void main (void) {	
    
    vec4 totalLight = vec4(1.0); 
    
    vec3 l = normalize(lightPosition - v); 
    vec3 e = normalize(-v); 
    vec3 r = normalize(-reflect(l, n));  
    
    if (nightMode) {

	    vec3 toLight;
        toLight.x = ecPos.x-lightPosition.x;
        toLight.y = 0;
        toLight.z = ecPos.z-lightPosition.z;

        vec3 avRot;
        avRot.x = cos(avatarRotation*3.141/180);
        avRot.y = 0;
        avRot.z = sin(avatarRotation*3.141/180);

        float arg = dot(normalize(toLight),normalize(avRot));

        float angle = acos(arg);

        if (angle < 0.5) {
            //ambient lighting
            vec4 l_ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
            l_ambient = clamp(l_ambient, 0.0, 1.0);

            //diffuse lighting
            vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0) * gl_LightSource[0].diffuse;
            l_diffuse = clamp(l_diffuse, 0.0, 1.0);

            //specular lighting
            vec4 l_specular = gl_FrontMaterial.specular
                          * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess) * gl_LightSource[0].specular;
            l_specular = clamp(l_specular, 0.0, 1.0);

            //Final resulting light (ambient + diffuse + specular)
            totalLight = (l_ambient + l_diffuse + l_specular);
            totalLight = clamp(totalLight, 0.0, 1.0);
        } else {
            //ambient lighting
            vec4 l_ambient = gl_FrontMaterial.ambient * gl_LightSource[1].ambient;
            l_ambient = clamp(l_ambient, 0.0, 1.0);

            //diffuse lighting
            vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0) * gl_LightSource[1].diffuse;
            l_diffuse = clamp(l_diffuse, 0.0, 1.0);

            //specular lighting
            vec4 l_specular = gl_FrontMaterial.specular
                          * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess) * gl_LightSource[1].specular;
            l_specular = clamp(l_specular, 0.0, 1.0);

            //Final resulting light (ambient + diffuse + specular)
            totalLight = (l_ambient + l_diffuse + l_specular);
            totalLight = clamp(totalLight, 0.0, 1.0);
		}
    } else {
	    //ambient lighting
	    vec4 l_ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
	    l_ambient = clamp(l_ambient, 0.0, 1.0);
	     
	    //diffuse lighting
	    vec4 l_diffuse = gl_FrontMaterial.diffuse * max(dot(n,l), 0.0) * gl_LightSource[0].diffuse;
	    l_diffuse = clamp(l_diffuse, 0.0, 1.0);
	    
	    //specular lighting
	    vec4 l_specular = gl_FrontMaterial.specular 
	                  * pow(max(dot(r,e), 0.0), 0.3 * gl_FrontMaterial.shininess) * gl_LightSource[0].specular;
	    l_specular = clamp(l_specular, 0.0, 1.0);
	
	    //Final resulting light (ambient + diffuse + specular)
	    totalLight = (l_ambient + l_diffuse + l_specular);
	    totalLight = clamp(totalLight, 0.0, 1.0); 
    }
    
    gl_FragColor = texture2D(texUnit, texCoordV) * totalLight;
    //gl_FragColor = texture2D(texUnit,texCoordV) * gl_Color;
    
}
