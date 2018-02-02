package edu.ucla.astro.irlab.util.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;

public class GradientButton extends JButton {

	public GradientButton() {
		initButton();
	}

	public GradientButton(Icon icon) {
		super(icon);
		initButton();
	}

	public GradientButton(String text) {
		super(text);
		initButton();
	}

	public GradientButton(Action a) {
		super(a);
		initButton();
	}

	public GradientButton(String text, Icon icon) {
		super(text, icon);
		initButton();
	}

	Color defaultColor;
	Color whiteColor;
	private void initButton() {
		defaultColor = new Color(0xbbccff);
		whiteColor = UIManager.getColor("Button.background");
		setBackground(defaultColor);
		setContentAreaFilled(false);
	}
	public Color getDefaultColor() {
		return defaultColor;
	}
	public void setDefaultColor(Color c) {
		defaultColor=c;
	}
	protected void paintComponent(Graphics g) {
		Color bgColor = getBackground();
		Graphics2D g2 = (Graphics2D)g.create();
		float[] dist = {0.0f, 0.25f, 0.35f, 1.0f};
		float[] compArray = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);			
		Color lightColor = Color.getHSBColor(compArray[0], (compArray[1]*(1f-0.5f*compArray[1])), compArray[2]);
		Color[] colors = {lightColor, whiteColor, whiteColor, bgColor};
		g2.setPaint(new LinearGradientPaint(new Point(0,0), new Point(0,getHeight()), dist, colors));
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
		super.paintComponent(g);
	}
}
