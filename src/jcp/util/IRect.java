package jcp.util;

import java.awt.*;

import processing.core.PImage;

public class IRect extends Rect {

	public PImage image;
	public String name;
	public boolean animated;
	public float aspect;

	public IRect(Image i, String name) {
		this(new PImage(i), name);
	}
	
	public IRect(PImage p, String name) {
		this.image = p;
		this.name = name;
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
		this.width = p.width;
		this.height = p.height;
		this.aspect = p.width / p.height; 
	}

}
