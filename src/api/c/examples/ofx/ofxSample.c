#include "ofxCpp.h"

typedef struct ofApp ofApp;

ofApp* init();

void runApp(ofApp*);

int main()
{

	ofApp* ofxApp;

	ofxApp = init();

	runApp(ofxApp);	
}
