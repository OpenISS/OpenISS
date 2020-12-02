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
  //const dst = new cv.Mat();
  //let dst = new cv.Mat(src.rows, src.cols, src.type());
  let dst = cv.Mat.zeros(src.cols, src.rows, cv.CV_8U);
//  cv.cvtColor(src, src, cv.COLOR_RGB2GRAY, 0);
  cv.cvtColor(src, dst, cv.COLOR_RGBA2GRAY, 0);
//  cv.cvtColor(src, dst, cv.COLOR_RGBA2GRAY, 0);
  cv.Canny(dst, dst, 50, 150, 3, false);
  const canvas = createCanvas(image.width, image.height);
  cv.imshow(canvas, dst);
  writeFileSync('canny.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));
  src.delete();
  dst.delete();
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
