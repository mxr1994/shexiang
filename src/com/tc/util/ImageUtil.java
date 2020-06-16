package com.tc.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtil {

// ===源图片路径名称如:c:\1.jpg   
	private String srcpath;

	// ===剪切图片存放路径名称.如:c:\2.jpg
	private String subpath;

	// ===剪切点x坐标
	private int x;

	private int y;

	// ===剪切点宽度
	private int width;

	private int height;

	public ImageUtil() {}

	public ImageUtil( int x, int y, int width, int height) {  
     this .x = x ;  
     this .y = y ;  
     this .width = width ;    
     this .height = height ;  
  }

	/**
	 * 对图片裁剪，并把裁剪完蛋新图片保存 。
	 */
	public void cut() throws IOException {

		FileInputStream is = null;
		ImageInputStream iis = null;

		try {
			// 读取图片文件
			is = new FileInputStream(srcpath);

			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。 参数：formatName -
			 * 包含非正式格式名称 . （例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索'。 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的 getDefaultReadParam
			 * 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			ImageIO.write(bi, "jpg", new File(subpath));
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}

	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getSrcpath() {
		return srcpath;
	}

	public void setSrcpath(String srcpath) {
		this.srcpath = srcpath;
	}

	public String getSubpath() {
		return subpath;
	}

	public void setSubpath(String subpath) {
		this.subpath = subpath;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public static void TrimImage( int x, int y, int width, int height, String sourceFile, String finalFile ) {
		ImageUtil imageUtil = new ImageUtil(x, y, width, height);
		imageUtil.srcpath = sourceFile;
		imageUtil.subpath = finalFile;
		try {
			imageUtil.cut();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 缩放图片(压缩图片质量，改变图片尺寸)
     * 若原图宽度小于新宽度，则宽度不变！
     * @param newWidth 新的宽度
     * @param quality 图片质量参数 0.7f 相当于70%质量
         * 2015年12月11日
     */
	public static File resize(File originalFile, File resizedFile, int newWidth, float quality) throws IOException {  
        if (quality > 1) {  
            throw new IllegalArgumentException("质量必须在0到1之间");  
        }  
   
        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());  
        Image i = ii.getImage();  
        Image resizedImage = null;  
   
        int iWidth = i.getWidth(null);  
        int iHeight = i.getHeight(null);  
   
        if(iWidth < newWidth){
            newWidth = iWidth;
        }
        if (iWidth > iHeight) {  
            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);  
        } else {  
            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);  
        }  
        
        // 此代码确保加载图像中的所有像素.  
        Image temp = new ImageIcon(resizedImage).getImage();  
   
        // 创建缓冲图像.  
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
   
        // 复制图像到缓冲图像.  
        Graphics g = bufferedImage.createGraphics();
   
        // 清除背景并绘制图像.  
        g.setColor(Color.white);  
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
        g.drawImage(temp, 0, 0, null);
        g.dispose();
   
        // 软化.  
        float softenFactor = 0.05f;  
        float[] softenArray = { 0, softenFactor, 0, softenFactor,  
                1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };  
        Kernel kernel = new Kernel(3, 3, softenArray);  
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);  
        bufferedImage = cOp.filter(bufferedImage, null);  
   
        // 将jpeg写入文件.  
        FileOutputStream out = new FileOutputStream(resizedFile);  
   
        // 将图像编码为JPEG数据流  
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
   
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);  
   
        param.setQuality(quality, true);  
   
        encoder.setJPEGEncodeParam(param);  
        encoder.encode(bufferedImage);
        out.close();
		return resizedFile;  
    } // Example usage  

}