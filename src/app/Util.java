/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 */

package app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Various utility functions
 *
 * $Id: Util.java 602 2006-04-09 17:37:18Z bjorn $
 */
public class Util
{
    /**
     * This function converts an <code>int</code> integer array to a
     * <code>byte</code> array. Each integer element is broken into 4 bytes and
     * stored in the byte array in litte endian byte order.
     *
     * @param integers an integer array
     * @return a byte array containing the values of the int array. The byte
     *         array is 4x the length of the integer array.
     */
    public static byte[] convertIntegersToBytes (int[] integers) {
        if (integers != null) {
            byte[] outputBytes = new byte[integers.length * 4];

            for(int i = 0, k = 0; i < integers.length; i++) {
                int integerTemp = integers[i];
                for(int j = 0; j < 4; j++, k++) {
                    outputBytes[k] = (byte)((integerTemp >> (8 * j)) & 0xFF);
                }
            }
            return outputBytes;
        } else {
            return null;
        }
    }

    /**
     * This utility function will check the specified string to see if it
     * starts with one of the OS root designations. (Ex.: '/' on Unix, 'C:' on
     * Windows)
     *
     * @param filename a filename to check for absolute or relative path
     * @return <code>true</code> if the specified filename starts with a
     *         filesystem root, <code>false</code> otherwise.
     */
    public static boolean checkRoot(String filename) {
        File[] roots = File.listRoots();

        for (int i = 0; i < roots.length; i++) {
            try {
                String root = roots[i].getCanonicalPath().toLowerCase();
                if (filename.toLowerCase().startsWith(root)) {
                    return true;
                }
            } catch (IOException e) {
                // Do we care?
            }
        }

        return false;
    }
    
    // Set integer
    public static void setInt(int val, byte [] arr, int idx){
        arr[idx]   = (byte)((val >>> 24) & 0xff);
        arr[idx+1] = (byte)((val >>> 16) & 0xff);
        arr[idx+2] = (byte)((val >>> 8)  & 0xff);
        arr[idx+3] = (byte)((val >>> 0)  & 0xff);
    }

    // Get integer
    public static int getInt(byte [] arr, int idx){
        int v = arr[idx];
        v = (v << 8) | ((int)arr[idx+1]) & 0x000000FF;
        v = (v << 8) | ((int)arr[idx+2]) & 0x000000FF;
        v = (v << 8) | ((int)arr[idx+3]) & 0x000000FF;
        return v;
    }
    

    public static String getElementAttributeValue(Node node, String attribname)
    {
    	String att = getAttributeValue(getNode(node,attribname),"value");
    	return att;
    }
    public static int getElementAttribute(Node node, String attribname, int def)
	{
		return getAttribute(getNode(node,attribname), "value",def);
	}
    public static boolean getElementAttribute(Node node, String attribname, boolean def)
    {
    	return getAttribute(getNode(node,attribname), "value",def);
    }
	public static String getAttributeValue(Node node, String attribname)
	{
		if(node==null)
			return null;
		NamedNodeMap attributes = node.getAttributes();
		String att = null;
		if(attributes != null)
		{
			Node attribute = attributes.getNamedItem(attribname);
			if(attribute != null)
			{
				att = attribute.getNodeValue();
			}
		}
		return att;
	}
	public static String getAttributeValue(Node node, String attribname,String def)
	{
		String att = getAttributeValue(node,attribname);
		if(att==null)
			return def;
		else
			return att;
	}
	public static Node getNode(Node node, String attribname)
	{
		if(node==null)
			return null;
		NodeList nodeList = node.getChildNodes();
		if(nodeList != null)
		{
			Node child = null;
			for(int i=0;i<nodeList.getLength();i++){
				child = nodeList.item(i);
				if(child.getNodeName().equals(attribname)){
					return child;
				}
			}
		}
		return null;
	}
	public static String getNodeValue(Node node,String childName){
		Node child = getNode(node,childName);
		if(child!=null){
			return child.getTextContent();
		}
		return null;
	}

	public static int getAttribute(Node node, String attribname, int def)
	{
		String attr = getAttributeValue(node, attribname);
		if(attr != null)
		{
			int value=def;
			try{
				value =Integer.parseInt(attr); 
			}catch(Exception ex){
			}
			return value;
		}else
		{
			return def;
		}
	}
	public static boolean getAttribute(Node node, String attribname, boolean def)
	{
		String attr = getAttributeValue(node, attribname);
		
		if(attr != null)
		{
			return "true".equals(attr.trim());
		}else
		{
			return def;
		}
	}


	/**
	 * 
	 * cut an image from a scrouce image
	 * 
	 * @param img
	 * @param offX
	 * @param offY
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image getSubImage(Image img,int offX,int offY,int width,int height){
		Image newImage = new Image(Display.getCurrent(),width,height);
		GC g=new GC(newImage);
		g.drawImage(img, -offX, -offY);
		ImageData newData = newImage.getImageData();
		g.dispose();
		newImage.dispose();
		ImageData srcData = img.getImageData();
		byte[] alpha = new byte[width*height];
		
		int srcX = offX < 0 ? 0 : offX;
		int srcY = offY < 0 ? 0 : offY;
		int destX = srcX - offX;
		int destY = srcY - offY;

		int srcEndX = offX + width - 1;
		int srcEndY = offY + height - 1;
		if (srcEndX >= srcData.width)
			srcEndX = srcData.width - 1;
		if (srcEndY >= srcData.height)
			srcEndY = srcData.height - 1;

		int destEndX = width - destX - 1 - (offX + width - srcEndX - 1);
		int destEndY = height - destY - 1 - (offY + height - srcEndY - 1);

		int copyW = destEndX - destX + 1;

		// out of srcouce, build an empty image
		if (srcEndX < 0 || srcEndY < 0 || srcX >= srcData.width
				|| srcY >= srcData.height) {
			newData.alphaData=alpha;
			newImage = new Image(Display.getCurrent(),newData);
			return newImage;
		}

		// start to copy data each line
		for (int y = srcY; y <= srcEndY; y++) {
			if (srcData.alphaData != null){
				System.arraycopy(srcData.alphaData, y * srcData.width + srcX,
						alpha, (destY + y - srcY) * width + destX, copyW);
			}
			else{
				int start = (destY + y - srcY) * width + destX;
				for(int x=start;x<start+copyW;x++){
					alpha[x]=(byte)255;
				}
			}
		}
		newData.alphaData=alpha;
		newData.alpha=-1;
		newData.transparentPixel=srcData.transparentPixel;
		newImage = new Image(Display.getCurrent(),newData);
		return newImage;
//		ImageData srcData = img.getImageData();
////		System.out.println("srcData.width = "+srcData.width+"   srcData.height = " + srcData.height+"   depth = "+srcData.depth + "   srcData.bytesPerLine = "+srcData.bytesPerLine);
//		srcData=fixImageData(srcData);
//		int bytesPerPixel = srcData.bytesPerLine/srcData.width;
//		int destBytesPerLine = bytesPerPixel*width;
//		byte[] newData = new byte[destBytesPerLine*height];
//		byte[] alphaData=null;
//		if(srcData.alphaData!=null)
//			alphaData = new byte[width*height];
//		int srcX = offX<0?0:offX;
//		int srcY = offY<0?0:offY;
//		int destX = srcX-offX;
//		int destY = srcY-offY;
//		
//		int srcEndX = offX+width-1;
//		int srcEndY = offY+height-1;
//		if(srcEndX>=srcData.width)
//			srcEndX = srcData.width-1;
//		if(srcEndY>=srcData.height)
//			srcEndY = srcData.height-1;
//		
//		int destEndX = width-destX-1-(offX+width-srcEndX-1);
//		int destEndY = height-destY-1-(offY+height-srcEndY-1);
//		
//		int copyW = destEndX-destX+1;
//		
//		// out of srcouce, build an empty image
//		if(srcEndX<0||srcEndY<0||srcX>=srcData.width||srcY>=srcData.height){
//			ImageData destData = new ImageData(width, height, srcData.depth, srcData.palette,
//					destBytesPerLine, newData);
//			destData.alphaData = alphaData;
//			return new Image(Display.getCurrent(),destData);
//		}
//		
//		// start to copy data each line
//		for(int y=srcY;y<=srcEndY;y++){
//			System.arraycopy(
//					srcData.data, y*srcData.bytesPerLine+srcX*bytesPerPixel,
//					newData, (destY+y-srcY)*destBytesPerLine+destX*bytesPerPixel, copyW*bytesPerPixel);
//			if(alphaData!=null)
//				System.arraycopy(
//						srcData.alphaData, y*srcData.width+srcX,
//						alphaData, (destY+y-srcY)*width+destX, copyW);
//		}
//		// return image
////		System.out.println("width = "+width+"   height = " + height+"   depth = "+srcData.depth + "   destBytesPerLine = "+destBytesPerLine);
//		ImageData destData = new ImageData(width, height, srcData.depth, srcData.palette,
//				destBytesPerLine, newData);
//		destData.alphaData = alphaData;
//		destData.transparentPixel=srcData.transparentPixel;
//		return new Image(Display.getCurrent(),destData);
	}
	
	
	public static ImageData fixImageData(ImageData imageData){
		if(imageData.bytesPerLine>=imageData.width)
			return imageData;
		byte[] data = new byte[imageData.width*imageData.height];
		int shift = 0;
		int mask = (1<<imageData.depth)-1;
		int index=0;
		for(int i=0;i<data.length;i++){
			data[i] = (byte)((imageData.data[index]>>>shift)&mask);
			shift+=imageData.depth;
			if(shift >=8){
				index++;
				shift=0;
			}
			
		}
		imageData.data=data;
		imageData.depth=8;
		imageData.bytesPerLine=imageData.width;
		ImageData newData = new ImageData(imageData.width, imageData.height, 8, imageData.palette,
				imageData.width, data);
		newData.alphaData = imageData.alphaData;
		newData.transparentPixel = imageData.transparentPixel;
		return newData;
	}
	public static int copyFile(String src,String dest){
		try{
		    File inputFile = new File(src);
		    File outputFile = new File(dest);
		    if(outputFile.exists())
		    	return -2;
		    else
		    	outputFile.createNewFile();
		    FileInputStream fis = new FileInputStream(inputFile);
		    FileOutputStream fos = new FileOutputStream(outputFile);
		    int c;
		    while ((c = fis.read()) != -1)
		      fos.write(c);
		    fos.close();
		    fis.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return -1;
		}
		return 0;
	}
	
    public static final byte[] applyPLETData(byte[] imageData, byte[] pletData)
    {
    	if(imageData == null || pletData == null)
    	{
    		return null;
    	}
    	
    	// PLET块中Data的起始index
    	int[] plet_info = getPLETData_pos_length(imageData);
    	if(plet_info == null){
    		return null;
    	}
    	
//    	System.out.println("get here");
    	// 将调色版数据复制进图片数据
    	System.arraycopy(pletData, 0, imageData, plet_info[0], pletData.length);
    	
    	// crc PLET块的数据
////    	int crc_data = crc(imageData, pletPos, pletLength, crc_table);
//    	int crc_data = CRCalculator.getCRC(imageData, plet_info[0] - 4, plet_info[1] + 4);
////    	System.out.println("crc_data == " + crc_data);
//    	int crc_pos = plet_info[0] + plet_info[1]; 
////    	System.out.println("crc_pos == " + crc_pos);
////    	imageData[crc_pos] = Common.int2Byte(crc_data >> 24);
////    	imageData[crc_pos + 1] = Common.int2Byte(crc_data >> 16);
////    	imageData[crc_pos + 2] = Common.int2Byte(crc_data >> 8);
////    	imageData[crc_pos + 3] = Common.int2Byte(crc_data);
//    	
//    	imageData[crc_pos] = (byte) (crc_data >> 24);
//    	imageData[crc_pos + 1] = (byte) (crc_data >> 16);
//    	imageData[crc_pos + 2] = (byte) (crc_data >> 8);
//    	imageData[crc_pos + 3] = (byte) crc_data;
    	return imageData;
    }
	 /** 
     * 得到PLET块中DATA的 index 与 长度
     * @param imageData
     * @return
     */
    public static int[] getPLETData_pos_length(byte[] imageData)
    {
    	if(imageData == null)
    	{
    		return null;
    	}
    	
    	for(int i = 0 ; i < imageData.length ; i ++)
    	{
    		if(imageData[i] == 'P' && imageData[i + 1] == 'L' && imageData[i + 2] == 'T' && imageData[i + 3] == 'E')
    		{
    			int length = (imageData[i - 4] << 24) | (imageData[i - 3] << 16) | (imageData[i - 2] << 8) | (imageData[i - 1]);
    			return new int[]{i + 4, length};
    		}
    	}
    	return null;
    }
    
    public static final byte[] loadImage(File imageFile, File paletteFile)
    {
    	byte[] imageData = getFileData(imageFile);
    	byte[] paletteData = getFileData(paletteFile);
    	if(imageData == null || paletteData == null)
    	{
    		return null;
    	}
    	
//    	System.out.println("image == " + imageFile.getName());
//    	System.out.println("pl == " + paletteFile.getName());
    	return applyPLETData(imageData, paletteData);
    }
    
	
	public static byte[] getFileData(File file)
	{
		if(file == null || !file.exists() || !file.isFile())
		{
			return null;
		}
		
		byte[] buffer = new byte[1024];
		FileInputStream fis = null; 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		try
		{
			fis = new FileInputStream(file);
			
			int read = -1;
			while((read = fis.read(buffer)) != -1)
			{
				baos.write(buffer, 0, read);
			}
			
			return baos.toByteArray();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}finally
		{
			try{
				baos.close();
			}catch(Exception ex){}
			try{
				fis.close();
			}catch(Exception ex){}
		}
	}
	
    public static int intValue(String str){
        try{
           return Integer.parseInt(str);
        }catch(NumberFormatException e){
            return 0;
        }
    }
}
