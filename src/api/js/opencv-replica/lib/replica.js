const { Canvas, createCanvas, Image, ImageData, loadImage } = require('canvas');
const { JSDOM } = require('jsdom');
const { writeFileSync, existsSync, mkdirSync } = require("fs");
const Jimp = require('jimp');


const request = require('request');
//const baseUrl = 'http://192.168.50.50:8080';
const baseUrl = 'http://localhost:8080';

let loaded = false;

exports.getFrame = function(args, cb) {

  var requestSettings = {
      url: baseUrl + '/rest/openiss/getStaticFrame/' + args.seq_id,
      method: 'GET',
      encoding: null
  };
  request(requestSettings, function (error, response, body) {
      cb(error, response, body);
  });

}

exports.getCanny = async function(args, cb) {

  var requestSettings = {
      url: baseUrl + '/rest/openiss/getStaticFrame/' + args.seq_id,
      method: 'GET',
      encoding: null
  };

  if (!loaded) {
    installDOM();
    await loadOpenCV();
    loaded = true;  
  }

  request(requestSettings, async function (error, response, body) {

    id = args.seq_id
    const image = await Jimp.read(Buffer.from(body))
    var src = cv.matFromImageData(image.bitmap);
    let dst = new cv.Mat();
    cv.cvtColor(src, dst, cv.COLOR_RGB2GRAY, 0);
    cv.Canny(dst, dst, 50, 150, 3, false);
    console.log('creating canvas ' + image.bitmap.width + "x" + image.bitmap.height)
    const canvas = createCanvas(image.bitmap.width, image.bitmap.height)
    console.log("doCanny: " + id);
    cv.imshow(canvas, dst);

    writeFileSync('./jobs/f' + id + '.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));

    canvas.toBuffer((err, buf) => {
      if (err) throw err // encoding failed
      cb(error, response, buf);
      // buf is JPEG-encoded image at 95% quality
    }, 'image/jpeg', { quality: 0.95 })

    src.delete();
    dst.delete()
    
  });

}

exports.getContour = async function(args, cb) {

  var requestSettings = {
    url: baseUrl + '/rest/openiss/getStaticFrame/' + args.seq_id,
      method: 'GET',
      encoding: null
  };

  if (!loaded) {
    installDOM();
    await loadOpenCV();
    loaded = true;  
  }

  request(requestSettings, async function (error, response, body) {

    id = args.seq_id
    const image = await Jimp.read(Buffer.from(body))
    var src = cv.matFromImageData(image.bitmap);
    
    let dst = new cv.Mat();
    cv.cvtColor(src, src, cv.COLOR_RGB2GRAY, 0);
    cv.threshold(src, src, 100, 255, cv.THRESH_BINARY);
    const canvas = createCanvas(image.width, image.height);
    console.log("doContour: " + id);
    cv.imshow(canvas, src);

    writeFileSync('./jobs/f' + id + '.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));

    canvas.toBuffer((err, buf) => {
      if (err) throw err // encoding failed
      cb(error, response, buf);
      // buf is JPEG-encoded image at 95% quality
    }, 'image/jpeg', { quality: 0.95 })

    src.delete();
    dst.delete()
    
  });

}

exports.doCanny = async function(id) {

  // before loading opencv.js we emulate a minimal HTML DOM. See the function declaration below.
  if (!loaded) {
    installDOM();
    await loadOpenCV();
    loaded = true;  
  }
  // using node-canvas, we an image file to an object compatible with HTML DOM Image and therefore with cv.imread()
  const image = await loadImage('./color_example.jpg');
  const src = cv.imread(image);
  let dst = new cv.Mat();
  cv.cvtColor(src, dst, cv.COLOR_RGB2GRAY, 0);
  cv.Canny(dst, dst, 50, 150, 3, false);
  const canvas = createCanvas(image.width, image.height);
  cv.imshow(canvas, dst);
  console.log("doCanny: " + id);
  writeFileSync('./jobs/f' + id + '.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));
  src.delete();
  dst.delete();
}

exports.doContour = async function(id) {

    // before loading opencv.js we emulate a minimal HTML DOM. See the function declaration below.
    if (!loaded) {
      installDOM();
      await loadOpenCV();
      loaded = true;  
    }
    // using node-canvas, we an image file to an object compatible with HTML DOM Image and therefore with cv.imread()
    const image = await loadImage('./color_example.jpg');
    const src = cv.imread(image);
    let dst = new cv.Mat();
    cv.cvtColor(src, src, cv.COLOR_RGB2GRAY, 0);
    cv.threshold(src, src, 100, 255, cv.THRESH_BINARY);
    const canvas = createCanvas(image.width, image.height);
    cv.imshow(canvas, src);
    console.log("doContour: " + id);
    writeFileSync('./jobs/f' + id + '.jpg', canvas.toBuffer('image/jpeg', { quality: 0.95 } ));
    src.delete();
    dst.delete();
}

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