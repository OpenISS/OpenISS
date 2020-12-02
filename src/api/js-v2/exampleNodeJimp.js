const Jimp = require('jimp');
async function onRuntimeInitialized(){
  // load local image file with jimp. It supports jpg, png, bmp, tiff and gif:
  var jimpSrc = await Jimp.read('./color_example.jpg');
  // `jimpImage.bitmap` property has the decoded ImageData that we can use to create a cv:Mat
  var src = cv.matFromImageData(jimpSrc.bitmap);
  // following lines is copy&paste of opencv.js dilate tutorial:
  let dst = new cv.Mat();

//  let dst = new cv.Mat(src.rows, src.cols, cv.CV_8U);
  cv.cvtColor(src, dst, cv.COLOR_RGB2GRAY, 0);
  //  cv.cvtColor(src, dst, cv.COLOR_RGBA2GRAY, 0);
//  cv.Canny(src, dst, 50, 150, 3, false);

  // let M = cv.Mat.ones(5, 5, cv.CV_8U);
  // let anchor = new cv.Point(-1, -1);
  // cv.dilate(src, dst, M, anchor, 1, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());
  // Now that we are finish, we want to write `dst` to file `output.png`. For this we create a `Jimp`
  // image which accepts the image data as a [`Buffer`](https://nodejs.org/docs/latest-v10.x/api/buffer.html).
  // `write('output.png')` will write it to disk and Jimp infers the output format from given file name:
  new Jimp({
    width: dst.cols,
    height: dst.rows,
    data: Buffer.from(dst.data)
  })
  .write('output.jpg');
  src.delete();
  dst.delete();
}
// Finally, load the open.js as before. The function `onRuntimeInitialized` contains our program.
Module = {
  onRuntimeInitialized() {
    setTimeout(() => {
      // this is our application:
      //console.log(cv.getBuildInformation())
    }, 0);
  }
}
cv = require('./opencv.js');
