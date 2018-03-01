package com.core;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageCacheHandler {
	public static final int MAX_IMAGES = 20;
	
	private static HashMap<Integer, BufferedImage> imageCache = new HashMap<Integer, BufferedImage>();
	
	static int[] deleteQueue = new int[MAX_IMAGES];
	
	static int deletePointer = 0;
	
	public static void saveCache() {
		File imageCacheFolder = new File("cache/");
		
		// clear out existing cached files
		if (imageCacheFolder.exists()){
			File[] caches = imageCacheFolder.listFiles();
			for (File imageFile:caches){
				imageFile.deleteOnExit();
			}
		}
		for (Integer i : imageCache.keySet()){
			File cacheFile = new File ("cache/"+i);
			if (!cacheFile.exists()){
				try {
					ImageIO.write(imageCache.get(i), "png", cacheFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void loadCache(){
		File imageCacheFolder = new File("cache/");
		if (imageCacheFolder.exists()){
			File[] caches = imageCacheFolder.listFiles();
			for (File imageFile:caches){
				try {
					cacheImage(Integer.parseInt(imageFile.getName()),ImageIO.read(imageFile));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static BufferedImage getCardImage(int multiverseID) throws IOException {
		if (!imageCache.containsKey(multiverseID)) {
			String path = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + multiverseID + "&type=card";
			URL cardUrl = null;
			try {
				cardUrl = new URL(path);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedImage image = ImageIO.read(cardUrl);
			cacheImage(multiverseID, image);
			return image;
		}else{
			return imageCache.get(multiverseID);
		}

	}
	
	private static void cacheImage(int multiverseID, BufferedImage image) {
		imageCache.put(multiverseID, image);
		imageCache.remove(deleteQueue[deletePointer]);
		deleteQueue[deletePointer] = multiverseID;
		deletePointer++;
		if (deletePointer>=MAX_IMAGES) {
			deletePointer = 0;
		}
	}
}
