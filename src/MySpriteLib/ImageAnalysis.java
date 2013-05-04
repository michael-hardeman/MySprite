package MySpriteLib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ImageAnalysis {

  ////////////////////////
  // Instance Variables //
  ////////////////////////
    private static Logger logger = Logger.getLogger(ImageAnalysis.class.getName());
  
  //////////////////
  // Enumerations //
  //////////////////
    static enum Corner{
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT
    }
    
  ///////////////////////////////
  // findClosestEdgeFromCorner //
  ///////////////////////////////
    public static Point findClosestEdgeFromCorner(BufferedImage item, Corner corner){
      Point position;
      CannyEdgeDetector detector = new CannyEdgeDetector();
      detector.setLowThreshold(0.5f);
      detector.setHighThreshold(1f);
      detector.setSourceImage(item);
      detector.process();
      BufferedImage edges = detector.getEdgesImage();
      switch(corner){
        case TOP_LEFT:
          position  = new Point(0,0);
          break;
        case TOP_RIGHT:
          position  = new Point(edges.getWidth()-1, 0);
          break;
        case BOTTOM_LEFT:
          position  = new Point(0, edges.getHeight()-1);
          break;
        case BOTTOM_RIGHT:
          position  = new Point(edges.getWidth()-1, edges.getHeight()-1);
          break;
        default:
          logger.log(Level.WARNING, "Unknown Corner type: "+corner.name()+" using Corner.TOP_LEFT");
          position  = new Point(0,0);
          break;
      }
      HashMap<Float, Point> edgePoints = new HashMap<Float, Point>();
      for(int x=0; x<edges.getWidth(); x++){
        for(int y=0; y<edges.getHeight(); y++){
          if(edges.getRGB(x, y) == Color.white.hashCode()){
            edgePoints.put(((float)position.distance(x, y)), new Point(x, y));
          }
        }
      }
      Float minIndex = null;
      Set<Float> keys = edgePoints.keySet();
      Iterator<Float> iterator = keys.iterator();
      while(iterator.hasNext()){
        Float current = iterator.next();
        if(minIndex == null){
          minIndex = current;
        } else if(current.floatValue() < minIndex.floatValue()){
          minIndex = current;
        }
      }
      return edgePoints.get(minIndex);
    }
    
  ///////////////////
  // makeComposite //
  ///////////////////
    public static BufferedImage makeComposite(BufferedImage S, BufferedImage D){
      if(S.getWidth() != D.getWidth() || S.getHeight() != D.getHeight()){
        logger.log(Level.WARNING, "Images to be compositied do not match in size.");
        return null;
      }
      BufferedImage theta = new BufferedImage( 
        D.getWidth(),
        D.getHeight(),
        BufferedImage.TYPE_4BYTE_ABGR
      );
      for(int x = 0; x < theta.getWidth(); x++){
        for(int y = 0; y < theta.getHeight(); y++){
          float alpha   = ((byte)(S.getRGB(x, y) >>> 0 ))/255.0F;
          float S_red   = ((byte)(S.getRGB(x, y) >>> 24))/255.0F;
          float S_green = ((byte)(S.getRGB(x, y) >>> 16))/255.0F;
          float S_blue  = ((byte)(S.getRGB(x, y) >>> 8 ))/255.0F;
          float D_red   = ((byte)(D.getRGB(x, y) >>> 24))/255.0F;
          float D_green = ((byte)(D.getRGB(x, y) >>> 16))/255.0F;
          float D_blue  = ((byte)(D.getRGB(x, y) >>> 8 ))/255.0F;
          // T = S*a + D*(1 - a)
          float theta_red   = (S_red   * alpha) + (D_red   * (1-alpha));
          float theta_green = (S_green * alpha) + (D_green * (1-alpha));
          float theta_blue  = (S_blue  * alpha) + (D_blue  * (1-alpha));
          byte r = (byte) (theta_red   * 255.0F);
          byte g = (byte) (theta_green * 255.0F);
          byte b = (byte) (theta_blue  * 255.0F);
          byte a = (byte) 0xff;
          theta.setRGB( x, y, ByteBuffer.wrap(new byte[]{r,g,b,a}).getInt());
        }
      }
      return theta;
    }
    
  ///////////////
  // tileImage //
  ///////////////
    public static BufferedImage tileImage(int width, int height, BufferedImage tile){
      BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g2d = (Graphics2D)output.getGraphics();
      for(int i=0; i<width/tile.getWidth()+1; i++){
        for(int j=0; j<height/tile.getHeight()+1; j++){
          g2d.drawImage(
            tile,
            i*tile.getWidth(),
            j*tile.getHeight(),
            tile.getWidth(),
            tile.getHeight(),
            null
          );
        }
      }
      g2d.dispose();
      return output;
    }

  ///////////////////////
  // CannyEdgeDetector //
  ///////////////////////
    static class CannyEdgeDetector {
      
      ///////////////
      // Constants //
      ///////////////
        private final static float GAUSSIAN_CUT_OFF = 0.005f;
        private final static float MAGNITUDE_SCALE  = 100F;
        private final static float MAGNITUDE_LIMIT  = 1000F;
        private final static int   MAGNITUDE_MAX    = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);
      
      ////////////////////////
      // Instance Variables //
      ////////////////////////
        private int           height;
        private int           width;
        private int           picsize;
        private int[]         data;
        private int[]         magnitude;
        private BufferedImage sourceImage;
        private BufferedImage edgesImage;
        private float         gaussianKernelRadius;
        private float         lowThreshold;
        private float         highThreshold;
        private int           gaussianKernelWidth;
        private boolean       contrastNormalized;
        private float[]       xConv;
        private float[]       yConv;
        private float[]       xGradient;
        private float[]       yGradient;
      
      /////////////////
      // Constructor //
      /////////////////
        public CannyEdgeDetector(){
          this.lowThreshold         = 2.5f;
          this.highThreshold        = 7.5f;
          this.gaussianKernelRadius = 2f;
          this.gaussianKernelWidth  = 16;
          this.contrastNormalized   = false;
        }
      ////////////////////
      // getSourceImage //
      ////////////////////
        public BufferedImage getSourceImage() {
          return this.sourceImage;
        }
      
      ////////////////////
      // setSourceImage //
      ////////////////////
        public void setSourceImage(BufferedImage image) {
          this.sourceImage = image;
        }
        
      ///////////////////
      // getEdgesImage //
      ///////////////////
        public BufferedImage getEdgesImage() {
          return this.edgesImage;
        }
      
      ///////////////////
      // getEdgesImage //
      ///////////////////
        public void setEdgesImage(BufferedImage edgesImage) {
          this.edgesImage = edgesImage;
        }
      
      /////////////////////
      // getLowThreshold //
      /////////////////////
        public float getLowThreshold() {
          return this.lowThreshold;
        }
        
      /////////////////////
      // setLowThreshold //
      /////////////////////
        public void setLowThreshold(float threshold) {
          if(threshold < 0) throw new IllegalArgumentException();
          this.lowThreshold = threshold;
        }
        
      //////////////////////
      // getHighThreshold //
      //////////////////////
        public float getHighThreshold() {
          return this.highThreshold;
        }
        
      //////////////////////
      // setHighThreshold //
      //////////////////////
        public void setHighThreshold(float threshold) {
          if(threshold < 0) throw new IllegalArgumentException();
          this.highThreshold = threshold;
        }
        
      ////////////////////////////
      // getGaussianKernelWidth //
      ////////////////////////////
        public int getGaussianKernelWidth() {
          return this.gaussianKernelWidth;
        }
        
      ////////////////////////////
      // setGaussianKernelWidth //
      ////////////////////////////
        public void setGaussianKernelWidth(int gaussianKernelWidth) {
          if(gaussianKernelWidth < 2) throw new IllegalArgumentException();
          this.gaussianKernelWidth = gaussianKernelWidth;
        }
        
      /////////////////////////////
      // getGaussianKernelRadius //
      /////////////////////////////
        public float getGaussianKernelRadius() {
          return this.gaussianKernelRadius;
        }
        
      /////////////////////////////
      // setGaussianKernelRadius //
      /////////////////////////////
        public void setGaussianKernelRadius(float gaussianKernelRadius) {
          if (gaussianKernelRadius < 0.1f) throw new IllegalArgumentException();
          this.gaussianKernelRadius = gaussianKernelRadius;
        }
      
      /////////////////////////////
      // isContrastNormalized //
      /////////////////////////////
        public boolean isContrastNormalized() {
          return this.contrastNormalized;
        }
        
      /////////////////////////////
      // setContrastNormalized //
      /////////////////////////////
        public void setContrastNormalized(boolean contrastNormalized) {
          this.contrastNormalized = contrastNormalized;
        }
        
      /////////////
      // Process //
      /////////////
        public void process() {
          width   = sourceImage.getWidth();
          height  = sourceImage.getHeight();
          picsize = width * height;
          initializeArrays();
          readLuminance();
          if(contrastNormalized) normalizeContrast();
          computeGradients(gaussianKernelRadius, gaussianKernelWidth);
          int low = Math.round(lowThreshold * MAGNITUDE_SCALE);
          int high = Math.round( highThreshold * MAGNITUDE_SCALE);
          performHysteresis(low, high);
          thresholdEdges();
          writeEdges(data);
        }
      
      //////////////////////
      // initializeArrays //
      //////////////////////
        private void initializeArrays() {
          if(data == null || picsize != data.length) {
            data      = new int[picsize];
            magnitude = new int[picsize];
            xConv     = new float[picsize];
            yConv     = new float[picsize];
            xGradient = new float[picsize];
            yGradient = new float[picsize];
          }
        }
        
      //////////////////////
      // computeGradients //
      //////////////////////
        private void computeGradients(float kernelRadius, int kernelWidth) {
          //generate the gaussian convolution masks
          float kernel[] = new float[kernelWidth];
          float diffKernel[] = new float[kernelWidth];
          int kwidth;
          for(kwidth = 0; kwidth < kernelWidth; kwidth++) {
            float g1 = gaussian(kwidth, kernelRadius);
            if(g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2)
              break;
            float g2 = gaussian(kwidth - 0.5f, kernelRadius);
            float g3 = gaussian(kwidth + 0.5f, kernelRadius);
            kernel[kwidth] = (g1 + g2 + g3) / 3f / (2f * (float) Math.PI * kernelRadius * kernelRadius);
            diffKernel[kwidth] = g3 - g2;
          }
          int initX = kwidth - 1;
          int maxX = width - (kwidth - 1);
          int initY = width * (kwidth - 1);
          int maxY = width * (height - (kwidth - 1));
          //perform convolution in x and y directions
          for(int x = initX; x < maxX; x++) {
            for(int y = initY; y < maxY; y += width) {
              int index = x + y;
              float sumX = data[index] * kernel[0];
              float sumY = sumX;
              int xOffset = 1;
              int yOffset = width;
              for(; xOffset < kwidth ;) {
                sumY += kernel[xOffset] * (data[index - yOffset] + data[index + yOffset]);
                sumX += kernel[xOffset] * (data[index - xOffset] + data[index + xOffset]);
                yOffset += width;
                xOffset++;
              }
              yConv[index] = sumY;
              xConv[index] = sumX;
            }
          }
          for(int x = initX; x < maxX; x++) {
            for(int y = initY; y < maxY; y += width) {
              float sum = 0f;
              int index = x + y;
              for(int i = 1; i < kwidth; i++)
                sum += diffKernel[i] * (yConv[index - i] - yConv[index + i]);
              xGradient[index] = sum;
            }
          }
          for(int x = kwidth; x < width - kwidth; x++) {
            for(int y = initY; y < maxY; y += width) {
              float sum = 0.0f;
              int index = x + y;
              int yOffset = width;
              for(int i = 1; i < kwidth; i++) {
                sum += diffKernel[i] * (xConv[index - yOffset] - xConv[index + yOffset]);
                yOffset += width;
              }
              yGradient[index] = sum;
            }
          }
          initX = kwidth;
          maxX = width - kwidth;
          initY = width * kwidth;
          maxY = width * (height - kwidth);
          for(int x = initX; x < maxX; x++) {
            for(int y = initY; y < maxY; y += width) {
              int index = x + y;
              int indexN = index - width;
              int indexS = index + width;
              int indexW = index - 1;
              int indexE = index + 1;
              int indexNW = indexN - 1;
              int indexNE = indexN + 1;
              int indexSW = indexS - 1;
              int indexSE = indexS + 1;
              float xGrad = xGradient[index];
              float yGrad = yGradient[index];
              float gradMag = hypot(xGrad, yGrad);
              //perform non-maximal supression
              float nMag = hypot(xGradient[indexN], yGradient[indexN]);
              float sMag = hypot(xGradient[indexS], yGradient[indexS]);
              float wMag = hypot(xGradient[indexW], yGradient[indexW]);
              float eMag = hypot(xGradient[indexE], yGradient[indexE]);
              float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
              float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
              float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
              float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
              float tmp;
              if(xGrad * yGrad <= (float) 0                                                             /*(1)*/
                ? Math.abs(xGrad) >= Math.abs(yGrad)                                                    /*(2)*/
                ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag) /*(3)*/
                && tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag)                               /*(4)*/
                : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag) /*(3)*/
                && tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag)                               /*(4)*/
                : Math.abs(xGrad) >= Math.abs(yGrad)                                                    /*(2)*/
                ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag) /*(3)*/
                && tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag)                               /*(4)*/
                : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag) /*(3)*/
                && tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag)                               /*(4)*/
              ) {
                magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
                //NOTE: The orientation of the edge is not employed by this
                //implementation. It is a simple matter to compute it at
                //this point as: Math.atan2(yGrad, xGrad);
              } else {
                magnitude[index] = 0;
              }
            }
          }
      }
      
      ///////////
      // hypot //
      ///////////
        private float hypot(float x, float y) {
          return (float) Math.hypot(x, y);
        }
      
      //////////////
      // gaussian //
      //////////////
        private float gaussian(float x, float sigma) {
          return (float) Math.exp(-(x * x) / (2f * sigma * sigma));
        }
      
      ///////////////////////
      // preformHysteresis //
      ///////////////////////
        private void performHysteresis(int low, int high) {
          Arrays.fill(data, 0);
          int offset = 0;
          for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
              if(data[offset] == 0 && magnitude[offset] >= high) {
                follow(x, y, offset, low);
              }
              offset++;
            }
          }
        }
      
      ////////////
      // follow //
      ////////////
        private void follow(int x1, int y1, int i1, int threshold) {
          int x0 = x1 == 0 ? x1 : x1 - 1;
          int x2 = x1 == width - 1 ? x1 : x1 + 1;
          int y0 = y1 == 0 ? y1 : y1 - 1;
          int y2 = y1 == height -1 ? y1 : y1 + 1;
          data[i1] = magnitude[i1];
          for(int x = x0; x <= x2; x++) {
            for (int y = y0; y <= y2; y++) {
              int i2 = x + y * width;
              if ((y != y1 || x != x1)
                && data[i2] == 0 
                && magnitude[i2] >= threshold) {
                follow(x, y, i2, threshold);
                return;
              }
            }
          }
        }
      
      ///////////////////
      // tresholdEdges //
      ///////////////////
        private void thresholdEdges() {
          for (int i = 0; i < picsize; i++) {
            data[i] = data[i] > 0 ? -1 : 0xff000000;
          }
        }
      
      ///////////////
      // luminance //
      ///////////////
        private int luminance(float r, float g, float b) {
          return Math.round(0.299f * r + 0.587f * g + 0.114f * b);
        }
      
      ///////////////////
      // readLuminance //
      ///////////////////
        private void readLuminance() {
          int type = sourceImage.getType();
          if(type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB) {
            int[] pixels = (int[]) sourceImage.getData().getDataElements(0, 0, width, height, null);
            for (int i = 0; i < picsize; i++) {
              int p = pixels[i];
              int r = (p & 0xff0000) >> 16;
              int g = (p & 0xff00) >> 8;
              int b = p & 0xff;
              data[i] = luminance(r, g, b);
            }
          } else if (type == BufferedImage.TYPE_BYTE_GRAY) {
            byte[] pixels = (byte[]) sourceImage.getData().getDataElements(0, 0, width, height, null);
            for (int i = 0; i < picsize; i++) {
              data[i] = (pixels[i] & 0xff);
            }
          } else if (type == BufferedImage.TYPE_USHORT_GRAY) {
            short[] pixels = (short[]) sourceImage.getData().getDataElements(0, 0, width, height, null);
            for (int i = 0; i < picsize; i++) {
              data[i] = (pixels[i] & 0xffff) / 256;
            }
          } else if (type == BufferedImage.TYPE_3BYTE_BGR) {
            byte[] pixels = (byte[]) sourceImage.getData().getDataElements(0, 0, width, height, null);
            int offset = 0;
            for(int i = 0; i < picsize; i++) {
              int b = pixels[offset++] & 0xff;
              int g = pixels[offset++] & 0xff;
              int r = pixels[offset++] & 0xff;
              data[i] = luminance(r, g, b);
            }
          } else {
            throw new IllegalArgumentException("Unsupported image type: " + type);
          }
        }
      
      ///////////////////////
      // normalizeContrast //
      ///////////////////////
        private void normalizeContrast() {
          int[] histogram = new int[256];
          for(int i = 0; i < data.length; i++) {
            histogram[data[i]]++;
          }
          int[] remap = new int[256];
          int sum = 0;
          int j = 0;
          for(int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            int target = sum*255/picsize;
            for (int k = j+1; k <=target; k++) {
              remap[k] = i;
            }
            j = target;
          }
          for(int i = 0; i < data.length; i++) {
            data[i] = remap[data[i]];
          }
        }
      
      ////////////////
      // writeEdges //
      ////////////////
        private void writeEdges(int pixels[]) {
          if(edgesImage == null) {
            edgesImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          }
          edgesImage.getWritableTile(0, 0).setDataElements(0, 0, width, height, pixels);
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
// MySprite - Java Sprite Editor                                              //
// Copyright (C) 2013 Michael Allen Hardeman                                  //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU General Public License as published by       //
// the Free Software Foundation, either version 3 of the License, or          //
// (at your option) any later version.                                        //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              //
// GNU General Public License for more details.                               //
//                                                                            //
// You should have received a copy of the GNU General Public License          //
// along with this program.  If not, see <http://www.gnu.org/licenses/>.      //
////////////////////////////////////////////////////////////////////////////////
