package jcp.util;


public interface Bounded {
	public int height();
	public int width();
	public int x();
	public int y();
	public boolean contains(float x, float y);
	public void width(int i);
	public void height(int i);
}
