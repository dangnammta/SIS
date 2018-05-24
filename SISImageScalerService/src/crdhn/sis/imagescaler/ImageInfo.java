/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.imagescaler;

import java.awt.image.BufferedImage;

/**
 *
 * @author namdv
 */
public class ImageInfo {
    
    public String name;
    public String type;
    public String path;
    public int width;
    public int height;
//    public BufferedImage imageSrc;

    public ImageInfo(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

//    public BufferedImage getImageSrc() {
//        return imageSrc;
//    }
//
//    public void setImageSrc(BufferedImage imageSrc) {
//        this.imageSrc = imageSrc;
//    }
    
    
    
}
