package edu.ucla.astro.irlab.util.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;

public class ConvertibleFrame {

	private JFrame frame = new JFrame();
	private JInternalFrame iframe = new JInternalFrame();
	private JDesktopPane desktop;
	private boolean isInternal = false;
	private JPanel contentPane = new JPanel();
	private JPanel iContentPane = new JPanel();
	private ImageIcon icon = new ImageIcon();
	private String title;
	public ConvertibleFrame() {
		this("");
	}
	public ConvertibleFrame(String title) {
		this(null, title);
	}
	public ConvertibleFrame(JDesktopPane desktopPane, String title) {
		desktop = desktopPane;
		this.title = title;
		
		initFrame();
		initIFrame();
	}
		
	private void initFrame() {
		frame.setTitle(title);
		frame.setContentPane(contentPane);
	}
	private void initIFrame() {
		iframe.setTitle(title);
		iframe.setContentPane(iContentPane);
		/*
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
				System.out.println(this.toString()+"hidden");
			}
			public void componentMoved(ComponentEvent e) {
				System.out.println(this.toString()+"moved");
				move();
			}
			public void componentResized(ComponentEvent e) {
				System.out.println(this.toString()+"resized");
			}
			public void componentShown(ComponentEvent e) {
				System.out.println(this.toString()+"shown");
			}
		});
		*/
		if (desktop != null) {
			isInternal = true;
			
			//. add frame to desktop, but make sure it isn't already added
			JInternalFrame[] frames = desktop.getAllFrames();
			
			for (JInternalFrame ii : frames) {
				if (ii == iframe) {
					return;
				}
			}
			
			desktop.add(iframe);
		}		
	}
	public void setFrame(JFrame newFrame) {
		frame = newFrame;
		initFrame();
	}
	public JFrame getFrame() {
		return frame;
	}
	public void setInternalFrame(JInternalFrame newFrame) {
		//. remove old frame from desktop
		if (desktop != null) {
			desktop.remove(iframe);
		}
		iframe = newFrame;
		initIFrame();
	}
	public JInternalFrame getInternalFrame() {
		return iframe;
	}
	public Container getContentPane() {
		if (isInternal) {
			return iContentPane;
		} else {
			return contentPane;
		}
	}
	public void setContentPane(Container pane) {
		if (isInternal) {
			iContentPane = (JPanel)pane;
			iframe.setContentPane(iContentPane);
		} else {
			contentPane = (JPanel)pane;
			frame.setContentPane(contentPane);
		}
	}
	public void addInternalFrameListener(InternalFrameListener l) {
		iframe.addInternalFrameListener(l);
	}
	public void addComponentListener(ComponentListener l) {
		iframe.addComponentListener(l);
		frame.addComponentListener(l);
	}
	public int getHeight() {
		if (isInternal) {
			return iframe.getHeight();
		} else {
			return frame.getHeight();
		}
	}
	public int getWidth() {
		if (isInternal) {
			return iframe.getWidth();
		} else {
			return frame.getWidth();
		}
	}
	public void setClosable(boolean b) {
		iframe.setClosable(b);
	}
	public void setDefaultCloseOperation(int operation) {
		iframe.setDefaultCloseOperation(operation);
	}
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
		frame.setIconImage(icon.getImage());
		iframe.setFrameIcon(icon);
	}
	public Point getLocation() {
		if (isInternal) {
			return iframe.getLocation();
		} else {
			return frame.getLocationOnScreen();
		}
	}
	public void setLocation(Point loc) {
		setLocation(loc.x, loc.y);
	}
  public void setLocation(int x, int y) {
  	frame.setLocation(x, y);
  	iframe.setLocation(x, y);
  }
  public void setMaximizable(boolean b) {
  	iframe.setMaximizable(b);
  }
  public void setName(String n) {
  	iframe.setName(n);
  	frame.setName(n);
  }
  public void setResizable(boolean b) {
  	iframe.setResizable(b);
  	frame.setResizable(b);
  }
  public void setSize(Dimension dim) {
  	setSize(dim.width, dim.height);
  }
  public void setSize(int w, int h) {
  	frame.setSize(w, h);
  	iframe.setSize(w, h);
  }
  public void setVisible(boolean b) {
		if (isInternal) {
			if (iframe.isVisible()) {
  			if (b) {
  				iframe.toFront();
  			} else {
  				iframe.setVisible(b);
  			}
  		} else {
  			iframe.setVisible(b);
  		}
  	} else {
  		frame.setVisible(b);
  	}
  }	
  public void setTitle(String t) {
  	title = t;
  	frame.setTitle(t);
  	iframe.setTitle(t);
  }
  public void repaint() {
  	frame.repaint();
  	iframe.repaint();
  }
  public void validate() {
  	frame.validate();
  	iframe.validate();
  }
  public void pack() {
  	frame.pack();
  	iframe.pack();
  }
  public void move() {
  	Point p = getLocation();
  	System.out.println("location = "+p.getX()+", "+p.getY());
  }
}
