import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class BaseFrame extends JFrame implements KeyListener, ActionListener, MouseMotionListener, MouseListener{
	protected int mx,my,mb;
	protected boolean []keys;

	protected Image back,dbImage;
	protected Graphics dbg;
	protected String col="";
	protected GamePane pane;
	protected Timer timer;
	
	final protected int LEFT = 37;
	final protected int UP = 38;
	final protected int RIGHT = 39;
	final protected int DOWN = 40;
	final protected int SPACE = 32;
	final protected int ESC = 27;
	
    public BaseFrame(String t, int w, int h) {
		super(t);
		pane = new GamePane(this);
		pane.setPreferredSize(new Dimension(w, h));
		
		addKeyListener(this);
		addMouseListener (this);
		addMouseMotionListener(this);
		
		keys = new boolean[2000];
		add(pane);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		timer = new Timer(20, this);
		timer.setInitialDelay(1000);
		timer.start();
    }
    
    public Image loadImage(String name){
    	return new ImageIcon(name).getImage();
    }
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

	private void updateMouse(MouseEvent e){
    	try{
			Point offsetF = getLocationOnScreen();    		
			Point offset = pane.getLocationOnScreen();    		
			mx = e.getX() - (offset.x - offsetF.x);
			my = e.getY() - (offset.y - offsetF.y);
    	}
    	catch(IllegalComponentStateException ex ){
			mx = e.getX();
			my = e.getY();
		}
	}   

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
     	updateMouse(e);     	
     	mb = 0;
    }    
    public void mouseClicked(MouseEvent e){
    	updateMouse(e);
    	mb = 0;
    }    
    public void mouseDragged(MouseEvent e){
    	updateMouse(e);
	}
    public void mouseMoved(MouseEvent e){
    	updateMouse(e);
	}
    public void mousePressed(MouseEvent e){
    	updateMouse(e);
    	mb = e.getButton();
	}		

	public void move(){}
	public void draw(Graphics g){}

	@Override
	public void actionPerformed(ActionEvent e){
		move();
		repaint();
	}
	
	class GamePane extends JPanel{
		BaseFrame main;
		public GamePane(BaseFrame m){
			main = m;
		}
		public void paintComponent(Graphics g){
			main.draw(g);
		}		
	}
}
