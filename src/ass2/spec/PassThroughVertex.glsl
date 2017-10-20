#version 120

//Just does standard transformations and passes the color from
// the jogl program onto the fragment shader

void main(void) {
	gl_Position=gl_ModelViewProjectionMatrix*gl_Vertex;
    gl_FrontColor = gl_Color;
    gl_BackColor =  gl_Color;
}