import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
public class PaintProgram extends JPanel implements MouseMotionListener, ActionListener, MouseListener, AdjustmentListener, ChangeListener, KeyListener
{
	JFrame frame;
	ArrayList<Point> points;
	Color currentColor, oldColor, backgroundColor;
	JMenuBar bar;
	JMenu colorMenu,fileMenu;
	JMenuItem save,load,clear,exit;
	JButton[] colorOptions;
	Color[] colors;
	Stack<ArrayList<Point>> freeLines;
	Stack<Object> shapes, undoRedoStack;
	boolean drawingFreeLine, drawingRectangle=false, drawingOval=false, eraserOn=false;
	boolean firstClick=true;
	JScrollBar penWidthBar;
	int penWidth,currX,currY,currWidth,currHeight;
	JColorChooser colorChooser;
	BufferedImage loadedImage;
	JFileChooser fileChooser;
	JButton freeLineButton, rectangleButton, ovalButton, undoButton, redoButton, eraser;
	ImageIcon freeLineImg, rectangleImg, ovalImg, loadImg, saveImg, undoImg, redoImg, eraserImg;
	Shape currShape;
	//boolean shiftPressed;
	public PaintProgram()
	{
		frame=new JFrame("Paint Pro");
		frame.add(this);

		bar=new JMenuBar();
		colorMenu=new JMenu("Colors");


		colors=new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA};
		colorOptions=new JButton[colors.length];
		colorMenu.setLayout(new GridLayout(7,1));
		for(int x=0; x<colors.length; x++)
		{
			colorOptions[x]=new JButton();
			colorOptions[x].putClientProperty("colorIndex",x);
			colorOptions[x].setBackground(colors[x]);
			colorOptions[x].addActionListener(this);
			colorOptions[x].setPreferredSize(new Dimension(100,50));
			colorMenu.add(colorOptions[x]);
		}
		colorChooser=new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(this);
		colorMenu.add(colorChooser);
		currentColor=colors[0];
		oldColor=currentColor;
		points=new ArrayList<Point>();
		freeLines=new Stack<ArrayList<Point>>();
		shapes=new Stack<Object>();
		undoRedoStack=new Stack<Object>();
		drawingFreeLine=true;
		penWidthBar=new JScrollBar(JScrollBar.HORIZONTAL, 1,0,1,40);
		penWidthBar.addAdjustmentListener(this);
		penWidth=penWidthBar.getValue();
		fileMenu=new JMenu("File");
		save=new JMenuItem("Save",KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveImg=new ImageIcon("saveImg.png");
		saveImg=new ImageIcon(saveImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		save.setIcon(saveImg);
		load=new JMenuItem("Load", KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		loadImg=new ImageIcon("loadImg.png");
		loadImg=new ImageIcon(loadImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		load.setIcon(loadImg);
		clear=new JMenuItem("New");
		exit=new JMenuItem("Exit");
		save.addActionListener(this);
		load.addActionListener(this);
		exit.addActionListener(this);
		clear.addActionListener(this);
		fileMenu.add(clear);
		fileMenu.add(load);
		fileMenu.add(save);
		fileMenu.add(exit);
		String currDir=System.getProperty("user.dir");
		fileChooser=new JFileChooser(currDir);
		freeLineImg=new ImageIcon("freeLineImg.png");
		freeLineImg=new ImageIcon(freeLineImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		freeLineButton=new JButton();
		freeLineButton.setIcon(freeLineImg);
		freeLineButton.setBackground(Color.LIGHT_GRAY);
		freeLineButton.addActionListener(this);
		freeLineButton.setFocusPainted(false);

		rectangleImg=new ImageIcon("rectImg.png");
		rectangleImg=new ImageIcon(rectangleImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		rectangleButton=new JButton();
		rectangleButton.setIcon(rectangleImg);
		//rectangleButton.setBackground(Color.LIGHT_GRAY);
		rectangleButton.setFocusPainted(false);
		rectangleButton.addActionListener(this);

		ovalImg=new ImageIcon("ovalImg.png");
		ovalImg=new ImageIcon(ovalImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		ovalButton=new JButton();
		ovalButton.setIcon(ovalImg);
		//ovalButton.setBackground(Color.LIGHT_GRAY);
		ovalButton.addActionListener(this);
		ovalButton.setFocusPainted(false);

		eraserImg=new ImageIcon("eraserImg.png");
		eraserImg=new ImageIcon(eraserImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		eraser=new JButton();
		eraser.setIcon(eraserImg);
		eraser.setFocusable(false);
		//ovalButton.setBackground(Color.LIGHT_GRAY);
		eraser.addActionListener(this);

		undoImg=new ImageIcon("undoImg.png");
		undoImg=new ImageIcon(undoImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		undoButton=new JButton();
		undoButton.setIcon(undoImg);
		undoButton.setFocusable(false);
		undoButton.setFocusPainted(false);
		undoButton.addActionListener(this);

		redoImg=new ImageIcon("redoImg.png");
		redoImg=new ImageIcon(redoImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		redoButton=new JButton();
		redoButton.setIcon(redoImg);
		redoButton.setFocusable(false);
		redoButton.setFocusPainted(false);
		redoButton.addActionListener(this);

		bar.add(fileMenu);
		currentColor=colors[0];
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		bar.add(colorMenu);
		bar.add(freeLineButton);
		bar.add(ovalButton);
		bar.add(rectangleButton);
		bar.add(eraser);
		bar.add(undoButton);
		bar.add(redoButton);
		bar.add(penWidthBar);
		backgroundColor=Color.WHITE;
		frame.add(bar, BorderLayout.NORTH);
		frame.setSize(1000,800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2= (Graphics2D)g;
		if(loadedImage!=null)
			g2.drawImage(loadedImage,0,0,null);
		g2.setColor(backgroundColor);
		g2.fillRect(0,0,frame.getWidth(),frame.getHeight());
		Iterator<Object> it=shapes.iterator();
		while(it.hasNext())
		{
			Object next=it.next();
			if(next instanceof Rectangle)
			{
				Rectangle temp=(Rectangle)next;
				g2.setColor(temp.getColor());
				g2.setStroke(new BasicStroke(temp.getPenWidth()));
				g2.draw(temp.getShape());
			}
			else if(next instanceof Oval)
			{
				Oval temp=(Oval)next;
				g2.setColor(temp.getColor());
				g2.setStroke(new BasicStroke(temp.getPenWidth()));
				g2.draw(temp.getShape());
			}
			else
			{
				ArrayList<?> temp=(ArrayList<?>)next;
				if(temp.size()>0)
				{
					g2.setStroke(new BasicStroke(((Point)temp.get(0)).getPenWidth()));
					for(int x=0; x<temp.size()-1; x++)
					{
						Point p1=(Point)temp.get(x);
						Point p2=(Point)temp.get(x+1);
						g2.setColor(p1.getColor());
						g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
					}
				}
			}
		}
		if(drawingFreeLine&&points.size()>0)
		{
			g2.setStroke(new BasicStroke(points.get(0).getPenWidth()));
			g2.setColor(points.get(0).getColor());
			for(int x=0; x<points.size()-1; x++)
			{
				Point p1=points.get(x);
				Point p2=points.get(x+1);
				g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
			}
		}
	}
	public void stateChanged(ChangeEvent e)
	{
		currentColor=colorChooser.getColor();
	}
	public void mouseMoved(MouseEvent e){}
	public void mouseReleased(MouseEvent e)
	{
		if(drawingRectangle||drawingOval)
		{
			shapes.push(currShape);
			firstClick=true;
		}
		else
		{
			shapes.push(points);
			points=new ArrayList<Point>();
			drawingFreeLine=true;
		}
		undoRedoStack=new Stack<Object>();
		repaint();
	}
	public void redo()
	{
		if(!undoRedoStack.isEmpty())
		{
			shapes.push(undoRedoStack.pop());
			repaint();
		}
	}
	public void undo()
	{
		if(!shapes.isEmpty())
		{
			undoRedoStack.push(shapes.pop());
			repaint();
		}
	}
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		penWidth=penWidthBar.getValue();
	}
	public void mouseDragged(MouseEvent e)
	{
		if(drawingRectangle||drawingOval)
		{
			if(firstClick)
			{
				currX=e.getX();
				currY=e.getY();
				if(drawingRectangle)
				{
					currShape=new Rectangle(currX,currY,currentColor,penWidthBar.getValue(),0,0);
				}
				else
					currShape=new Oval(currX,currY,currentColor,penWidthBar.getValue(),0,0);
				firstClick=false;
				shapes.push(currShape);
			}
			else
			{
				currWidth=Math.abs(e.getX()-currX);
				currHeight=Math.abs(e.getY()-currY);
				currShape.setWidth(currWidth);
				currShape.setHeight(currHeight);
				if(currX<=e.getX() && currY>=e.getY())
				{
					currShape.setY(e.getY());
				}
				else if(currX>=e.getX() && currY<=e.getY())
				{
					currShape.setX(e.getX());
				}
				else if(currX>=e.getX() && currY>=e.getY())
				{
					currShape.setY(e.getY());
					currShape.setX(e.getX());
				}
			}
		}
		else
			points.add(new Point(e.getX(), e.getY(), currentColor, penWidth));
		repaint();
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==save)
		{
			FileFilter filter=new FileNameExtensionFilter("*.png","png");
		    fileChooser.setFileFilter(filter);
			if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			{
				File file=fileChooser.getSelectedFile();
				try
				{
					String st=file.getAbsolutePath();
					if(st.indexOf(".png")>=0)
						st=st.substring(0,st.length()-4);
					ImageIO.write(createImage(),"png",new File(st+".png"));
				}catch(Exception ie)
				{
				}
			}
		}
		else if(e.getSource()==load)
		{
			fileChooser.showOpenDialog(null);
			File imgFile=fileChooser.getSelectedFile();
			if(imgFile!=null&&imgFile.toString().indexOf(".png")>=0)
			{
				try{
				loadedImage=ImageIO.read(imgFile);
				}
				catch(IOException oiewoi){}
				shapes=new Stack<Object>();
				repaint();
			}
			else
			{
				if(imgFile!=null)
					JOptionPane.showMessageDialog(null,"Not a PNG");
			}
		}
		else if(e.getSource()==clear)
		{
			shapes=new Stack<Object>();
			loadedImage=null;
			repaint();
		}
		else if(e.getSource()==exit)
		{
			System.exit(0);
		}
		else if(e.getSource()==freeLineButton)
		{
			drawingFreeLine=true;
			drawingRectangle=false;
			drawingOval=false;
			eraserOn=false;
			freeLineButton.setBackground(Color.LIGHT_GRAY);
			rectangleButton.setBackground(null);
			ovalButton.setBackground(null);
			eraser.setBackground(null);
			currentColor=oldColor;
		}
		else if(e.getSource()==rectangleButton)
		{
			drawingFreeLine=false;
			drawingRectangle=true;
			drawingOval=false;
			freeLineButton.setBackground(null);
			rectangleButton.setBackground(Color.LIGHT_GRAY);
			ovalButton.setBackground(null);
			eraserOn=false;
			eraser.setBackground(null);
			currentColor=oldColor;
		}
		else if(e.getSource()==ovalButton)
		{
			drawingFreeLine=false;
			drawingRectangle=false;
			drawingOval=true;
			freeLineButton.setBackground(null);
			rectangleButton.setBackground(null);
			ovalButton.setBackground(Color.LIGHT_GRAY);
			eraserOn=false;
			eraser.setBackground(null);
			currentColor=oldColor;
		}
		else if(e.getSource()==eraser)
		{
			drawingFreeLine=true;
			drawingRectangle=false;
			drawingOval=false;
			eraserOn=true;
			freeLineButton.setBackground(null);
			rectangleButton.setBackground(null);
			ovalButton.setBackground(null);
			eraserOn=true;
			eraser.setBackground(Color.LIGHT_GRAY);
			oldColor=currentColor;
			currentColor=backgroundColor;
		}
		else if(e.getSource()==undoButton)
		{
			undo();
		}
		else if(e.getSource()==redoButton)
		{
			redo();
		}
		else
		{
			if(eraserOn)
			{
				drawingFreeLine=true;
				drawingRectangle=false;
				drawingOval=false;
				eraserOn=false;
				freeLineButton.setBackground(Color.LIGHT_GRAY);
				rectangleButton.setBackground(null);
				ovalButton.setBackground(null);
				eraser.setBackground(null);
			}
			int index=(int)((JButton)e.getSource()).getClientProperty("colorIndex");
			currentColor=colors[index];
		}
	}
	public BufferedImage createImage()
	{
		int width=this.getWidth();
		int height=this.getHeight();
		BufferedImage img=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2=img.createGraphics();
		this.paint(g2);
		g2.dispose();
		return img;
	}
	public void keyPressed(KeyEvent e)
	{
		if(e.isControlDown())
		{
			if(e.getKeyCode()==KeyEvent.VK_Z)
			{
				undo();
			}
			if(e.getKeyCode()==KeyEvent.VK_Y)
			{
				redo();
			}
		}
	}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	public static void main(String[] args)
	{
		PaintProgram app = new PaintProgram();
	}
	public void mousePressed(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public class Point
	{
		int x,y, penWidth;
		Color color;
		public Point(int x, int y, Color color, int penWidth)
		{
			this.x=x;
			this.y=y;
			this.penWidth=penWidth;
			this.color=color;
		}
		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public Color getColor()
		{
			return color;
		}
		public int getPenWidth()
		{
			return penWidth;
		}
		public void setX(int x)
		{
			this.x=x;
		}
		public void setY(int y)
		{
			this.y=y;
		}
	}
	public class Shape extends Point
	{
		private int width,height;
		public Shape(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth);
			this.width=width;
			this.height=height;
		}
		public int getWidth()
		{
			return width;
		}
		public int getHeight()
		{
			return height;
		}
		public void setWidth(int width)
		{
			this.width=width;
		}
		public void setHeight(int height)
		{
			this.height=height;
		}
	}
	public class Rectangle extends Shape
	{
		public Rectangle(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth,width,height);
		}
		public Rectangle2D.Double getShape()
		{
			return new Rectangle2D.Double(getX(),getY(),getWidth(),getHeight());
		}
	}
	public class Oval extends Shape
	{
		public Oval(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth,width,height);
		}
		public Ellipse2D.Double getShape()
		{
			return new Ellipse2D.Double(getX(),getY(),getWidth(),getHeight());
		}
	}
}