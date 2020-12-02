const { Canvas, createCanvas, Image, ImageData, loadImage } = require('canvas');
const { JSDOM } = require('jsdom');
const { writeFileSync, existsSync, mkdirSync } = require("fs");
// This is our program. This time we use JavaScript async / await and promises to handle asynchronicity.
(async () => {
  // before loading opencv.js we emulate a minimal HTML DOM. See the function declaration below.
  installDOM();
  await loadOpenCV();
  // using node-canvas, we an image file to an object compatible with HTML DOM Image and therefore with cv.imread()
  const image = await loadImage('./color_example.jpg');
  const src = cv.imread(image);
  //let dst = new cv.Mat();
  let dst = cv.Mat.zeros(src.cols, src.rows, cv.CV_8U);
  cv.cvtColor(src, src, cv.COLOR_RGB2GRAY, 0);
  cv.threshold(src, src, 100, 255, cv.THRESH_BINARY);
  // let contours = new cv.MatVector();
  // let hierarchy = new cv.Mat();
  // cv.findContours(src, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);

  // let contoursColor = new cv.Scalar(255, 255, 255);
  // for (let i = 0; i < contours.size(); ++i) {
  //   cv.drawContours(dst, contours, i, contoursColor, 1, cv.LINE_8, hierarchy, 100);
  // }
  const canvas = createCanvas(image.width, image.height);
  cv.imshow(canvas, src);
  writeFileSync('contour.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));
  src.delete();
  dst.delete();
  // contours.delete();
  // hierarchy.delete();
})();
// Load opencv.js just like before but using Promise instead of callbacks:
function loadOpenCV() {
  return new Promise(resolve => {
    global.Module = {
      onRuntimeInitialized: resolve
    };
    global.cv = require('./opencv.js');
  });
}
// Using jsdom and node-canvas we define some global variables to emulate HTML DOM.
// Although a complete emulation can be archived, here we only define those globals used
// by cv.imread() and cv.imshow().
function installDOM() {
  const dom = new JSDOM();
  global.document = dom.window.document;
  // The rest enables DOM image and canvas and is provided by node-canvas
  global.Image = Image;
  global.HTMLCanvasElement = Canvas;
  global.ImageData = ImageData;
  global.HTMLImageElement = Image;
}
