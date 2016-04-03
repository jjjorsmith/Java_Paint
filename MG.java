import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
import java.awt.*;
import java.awt.event.*;

public class MG extends JFrame implements ChangeListener
{
	JColorChooser cc;
	Color myColor;
	Canvas surface;
	int bSize = 30;
	String bMsg = "Brush Size: ";
	
	MG()
	{
		//set up main window
		setSize(800,600);
		setLocation(0,0);
		setTitle("Paint 13.5.2");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		//set up the color chooser
		cc = new JColorChooser();
		AbstractColorChooserPanel[] panels = cc.getChooserPanels();
		for (AbstractColorChooserPanel accp : panels) {
			if (accp.getDisplayName().equals("Swatches")) {
			}
			else
			{
				cc.removeChooserPanel(accp);
			}
		}
		cc.getSelectionModel().addChangeListener(this);
		cc.setPreviewPanel(new JPanel());
		add(cc, BorderLayout.SOUTH);
		
		//set up the menu bar
		JMenuBar mBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem close = new JMenuItem("Close");
		JMenuItem newImage = new JMenuItem("New");
		mBar.add(file);
		file.add(newImage);
		file.add(close);
		setJMenuBar(mBar);
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				System.exit(0);
			}
		});
		newImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.clear();
			}
		});
		
		//setup a toolBar for the left side
		JPanel toolbar = new JPanel();
		toolbar.setSize(50,600);
		toolbar.setLayout(new GridLayout(10,0));
		toolbar.setVisible(true);
		JLabel toolOptions = new JLabel("Tools");
		toolbar.add(toolOptions);
		JButton inc = new JButton("Brush +");
		JButton dec = new JButton("Brush -");
		JLabel brushSize = new JLabel(bMsg+bSize);
		inc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				if(bSize <= 98)
					bSize += 2;
				surface.newSize(bSize);
				brushSize.setText(bMsg+bSize);
			}
		});
		dec.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				if(bSize >= 2)
					bSize -= 2;
				surface.newSize(bSize);
				brushSize.setText(bMsg+bSize);
			}
		});
		toolbar.add(inc);
		toolbar.add(dec);
		toolbar.add(brushSize);
		JButton line = new JButton("Line");
		line.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.shape(1);
			}
		});
		toolbar.add(line);
		JButton pen = new JButton("Pen");
		pen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.shape(0);
			}
		});
		toolbar.add(pen);
		JButton rect = new JButton("Rectangle");
		rect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.shape(2);
			}
		});
		toolbar.add(rect);
		JButton square = new JButton("Square");
		square.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.shape(3);
			}
		});
		toolbar.add(square);
		JButton oval = new JButton("Oval");
		oval.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				surface.shape(4);
			}
		});
		toolbar.add(oval);
		add(toolbar, BorderLayout.WEST);
		
		//setup canvas for the drawing surface
		surface = new Canvas(this);
		add(surface, BorderLayout.CENTER);
		repaint();
		
	}
	
	//overrides stateChanged from changeListener
	public void stateChanged(ChangeEvent ce)
	{
		myColor = cc.getColor();
		surface.newColor(myColor);
	}
	
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				new MG();
			}
		});
	}
}

class Canvas extends JComponent implements MouseListener, MouseMotionListener
{

	//variables
	Color myColor = Color.black;
	int mySize = 30;
	int myShape = 0; //0 - pen, 1 - line, 2 - rectangle, 3 - square, 4 - oval
	int mouseX, mouseY, newX, newY;
	int firstTime = 0;
	MG window;
	Image image;
	Graphics2D g2;
	
	//setup the canvas	
	Canvas(MG window)
	{
		this.window = window;
		setDoubleBuffered(false);
		
		//listen for mouse action on this component
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(image == null)
		{
			image = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D)image.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
		g.drawImage(image, 0, 0, null);

	} // end of over-ridden paintComponent method
	
	public void mousePressed(MouseEvent me)
	{
		mouseX = me.getX();
		mouseY = me.getY();
		
		if( myShape == 0 )
		{
			g2.setColor(myColor);
			g2.fillOval(mouseX - mySize/2, mouseY - mySize/2, mySize, mySize);
			repaint();
		}
		if( myShape == 2 )
		{
			g2.setColor(myColor);
			g2.fillRect(mouseX - (2*mySize/2), mouseY - (mySize/2), 2*mySize, mySize);
			repaint();
		}
		if( myShape == 3 )
		{
			g2.setColor(myColor);
			g2.fillRect(mouseX - mySize/2, mouseY - mySize/2, mySize, mySize);
			repaint();
		}
		if( myShape == 4 )
		{
			g2.setColor(myColor);
			g2.fillOval(mouseX - (2*mySize/2), mouseY - (mySize/2), 2*mySize, mySize);
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent me)
	{
		if( myShape == 1)
		{
			g2.setColor(myColor);
			g2.setStroke(new BasicStroke(mySize));
			g2.drawLine(mouseX, mouseY, me.getX(), me.getY());
			repaint();
		}
	}
	
	public void mouseDragged(MouseEvent me)
	{
		if( myShape == 0 )
		{
			mouseX = me.getX();
			mouseY = me.getY();
			g2.setColor(myColor);
			g2.fillOval(mouseX - mySize/2, mouseY - mySize/2, mySize, mySize);
			repaint();
		}
	}
	
	public void mouseClicked(MouseEvent me)
	{
	
	}
	
	public void mouseEntered(MouseEvent me)
	{
		
	}
	
	public void mouseExited(MouseEvent me)
	{
		
	}
	
	public void mouseMoved(MouseEvent me)
	{
	}
	
	public void newColor(Color cc)
	{
		myColor = cc;
	}
	
	public void newSize(int newSize)
	{
		mySize = newSize;
	}
	
	public void shape(int shape)
	{
		myShape = shape;
	}
	
	public void clear(){
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(Color.black);
		repaint();
	}
}