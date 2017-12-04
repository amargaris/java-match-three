package graphical.model.gfx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;

import graphical.model.XY;
import graphical.model.XYZ;
import graphical.model.enume.GameShape;
import graphical.model.gfx.overlays.SelectedShapeRenderer;
import graphical.model.gfx.overlays.ShapeRenderer;
import graphical.model.gfx.overlays.TransitionRenderer;
import graphical.model.threads.Transition;

public class MatchThreePanel extends MultiLayerPanel<MatchThreePanel> implements MouseListener,MouseMotionListener{

	public static void main(String... args){
		SwingUtilities.invokeLater(()-> {
			new MatchThreePanel(10,10,1000).display("Testing Game");
		});
	}

	private static final long serialVersionUID = 1L;
	public static Color[] colors = new Color[]{Color.yellow,Color.red,Color.blue,Color.green};
	private static Random rand = new Random();
	
	private GameShape[][] game;
	private XYZ selected;
	public int selectedx;
	public int selectedy;
	private boolean flag;
	private static final long TRANSITION_INTERVAL=50;
	private volatile boolean block;
	private double LIMIT;
	private List<Transition>transitions;
	private Thread transitionThread,actionsThread;
	private String[][] scoreBoard;
	public static GameShape[] values = GameShape.values();
	private int counter,limitation;
	private JTable table;
	private int gameWidth;
	private int gameHeight;
	private int deleted;
	private List<Runnable>actions;
	public List<Runnable>queue;
	
	public static boolean LOG_ACTIONS = false;
	
	public MatchThreePanel(int width,int height,int limitation){
		super(50,null);
		setSupplier(()->this);
		transitions=new LinkedList<>();
		this.gameWidth =width;
		this.gameHeight =height;
		LIMIT=(double)1/(width*2);
		game =generate(this,width,height);
		add(new SelectedShapeRenderer());
		add(new ShapeRenderer());
		addMouseListener(this);
		addMouseMotionListener(this);
		flag=true;
		add(new TransitionRenderer(getTransitions()));
		scoreBoard= new String[5][5];
		setCounter(0);
		setDeleted(0);
		setLimitation(limitation);
		actions= new ArrayList<>();
		queue = new LinkedList<>();
	}

	public GameShape getAt(int width,int height){
		return game[width][height];
	}
	public void setAt(GameShape shape,int width,int height){
		game[width][height]=shape;
	}
	
	public int getGameWidth() {
		return gameWidth;
	}

	public void setGameWidth(int gameWidth) {
		this.gameWidth = gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public void setGameHeight(int gameHeight) {
		this.gameHeight = gameHeight;
	}

	public int getLimitation() {
		return limitation;
	}

	public void setLimitation(int limitation) {
		this.limitation = limitation;
	}
	
	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public void updateTable(){
		for(int i=0;i<scoreBoard.length;i++){
			for(int j=0;j<scoreBoard[i].length;j++){
				scoreBoard[i][j]="VAROUFAKIS"+new Random().nextInt(255);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((AbstractTableModel)table.getModel()).fireTableDataChanged();
			}
		});
	}
	public GameShape[][] game(){
		return game;
	}
	public static boolean[] active;
	static{
		active = new boolean[values.length];
		for(int i=0;i<active.length;i++)
			active[i]=true;
	}
	public static GameShape getRandom(){
		int index=-1;
		do{
			index=rand.nextInt(values.length-1);
		}while(!active[index]);
		return values[index];
	}
	public static GameShape[][] generate(MatchThreePanel panel,int width,int height){
		GameShape[][] shapes = new GameShape[width][height];
		for(int i=0;i<shapes.length;i++){
			for(int j=0;j<shapes[i].length;j++){
				shapes[i][j]=getRandom();
			}
		}
		return shapes;
	}
	public XYZ getSelected(){
		return selected;
	}
	public void install(JFrame frame){
		super.install(frame);
		//frame.getContentPane().add(getScoreBoard(),BorderLayout.EAST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		enableTransitionThread();
		enableActionsThread();
	}
	public JPanel getScoreBoard(){
		JPanel rightPanel = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private int patternWidth=50;
			private int patternHeight=50;
			private BufferedImage pattern=Scalr.resize(
					bufferizeImage(MatchThreePanel.class, "resources/pattern.png"),Mode.FIT_EXACT, patternWidth,patternHeight);
			
			public void paintComponent(Graphics g){
				int howManyx=getWidth()/patternWidth;
				int howManyy=getHeight()/patternHeight;
				int howManyLeftx=getWidth()%patternWidth;
				int howManyLefty=getHeight()%patternHeight;
				for(int i=0;i<howManyx;i++){
					for(int j=0;j<howManyy;j++){
						g.drawImage(pattern,i*patternWidth,j*patternWidth,null);
					}
				}
				g.drawImage(pattern, howManyx*patternWidth,howManyy*patternHeight,howManyLeftx,howManyLefty,null);
				super.paintComponent(g);
			}
		};
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(new JLabel("Ethnikos Proypolismos etos "+Calendar.getInstance().get(Calendar.YEAR)),BorderLayout.NORTH);
		table = new JTable(scoreBoard,new String[]{"Epithymito","Epaxthes"});
		table.setOpaque(false);
		JProgressBar bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(1,50));
		rightPanel.add(bar,BorderLayout.SOUTH);
		rightPanel.add(table,BorderLayout.CENTER);
		rightPanel.setOpaque(false);
		rightPanel.setPreferredSize(new Dimension(200,1));
		return rightPanel;
	}
	public void enableTransitionThread(){
		transitionThread=new Thread(){
			public void run(){
				while(true){
					try{
						applyTransitions();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		};
		transitionThread.start();
	}
	
	public void enableActionsThread(){
		actionsThread = new Thread(){
			public void run(){
				while(true){
					if(actions.size()==0&&queue.size()==0){
						try{
							//unBlockActions();
							if(LOG_ACTIONS){System.out.println("actions sleeping");}
							Thread.sleep(500);
							continue;
						}
						catch(Exception e){
							if(LOG_ACTIONS){System.out.println("new Action");}
						}
					}
					blockActions();
					if(queue.size()!=0){
						synchronized(queue){
							for(Runnable run:queue){
								actions.add(run);
							}
							queue.clear();
						}
					}
					Iterator<Runnable> it=actions.iterator();
					while(it.hasNext()){
						blockActions();
						it.next().run();
					}
					actions.clear();
					unBlockActions();
				}
			}
		};
		actionsThread.start();
	}
	public void addActions(Runnable run){
		synchronized(actionsThread){
			if(actionsThread.getState()==State.TIMED_WAITING)
			actionsThread.interrupt();
		}
		synchronized(queue){
			queue.add(run);
		}
		
	}
	public static boolean LOG_TRANSITIONS=false;
	public void applyTransitions()throws Exception{
		if(getTransitions().size()==0){
			try{
				if(LOG_TRANSITIONS){System.out.println("transitions sleeping");}
				Thread.sleep(1000);
				return;
			}catch(Exception e){
				
			}
		}
		if(LOG_TRANSITIONS){System.out.println("Transition!");}
		synchronized (getTransitions()) {
			Iterator<Transition>it=getTransitions().iterator();
			while(it.hasNext()){
				Transition trans=it.next();
				trans=trans.advance();
				if(trans!=null){
					it.remove();
				}
			}
		}
		try{
			Thread.sleep(TRANSITION_INTERVAL);
		}catch(Exception e){
			
		}
	}
	public void addTransition(List<Transition>simultaneousTransitions){
		boolean flag=getTransitions().size()==0;
		synchronized (getTransitions()) {
			for(Transition transition:simultaneousTransitions){
				transition.init();
				getTransitions().add(transition);
			}
		}
		if(flag){
			synchronized(transitionThread){
				transitionThread.interrupt();
			}
		}
	}
	public static final int LEFT=0;
	public static final int TOP=1;
	public static final int RIGHT=2;
	public static final int BOTTOM=3;
	public static final int[] DIRECTIONS={LEFT,TOP,RIGHT,BOTTOM};
	public static final int X_=0;
	public static final int Y_=1;
	public static final int[][] DIR_COR={{-1,0},//left
										 {0,-1},//top
										 {+1,0},//right
										 {0,1}};//bottom
	public static final int[] CHECK={X_,Y_,X_,Y_};
	public String print(int width,int height){
		return String.format("Shape: %s [%d,%d]", game[width][height],width,height);
	}
	public void searchForDeletion(final int x,final int y){
		//long start=System.nanoTime();
		GameShape shape = game[x][y];
		if(shape==null){
			return;
		}
		GameShape[] close=getClose(x,y);
		List<List<int[]>>listed = new ArrayList<>();
		listed.add(new ArrayList<int[]>());
		listed.get(X_).add(new int[]{x,y});
		listed.add(new ArrayList<int[]>());
		listed.get(Y_).add(new int[]{x,y});
		outer :for(int direction:DIRECTIONS){
			GameShape sh=close[direction];
			if(sh==GameShape.empty){
				continue;
			}
			if(sh==shape){
				int xa=x+DIR_COR[direction][X_];
				int ya=y+DIR_COR[direction][Y_];
				
				//System.out.println(print(xa,ya));
				listed.get(CHECK[direction]).add(new int[]{xa,ya});
				if(xa<0||xa==getGameWidth()){
					continue outer;
				} 
				if(ya<0||ya==getGameHeight()){
					continue outer;
				}
				//System.out.println("checking "+xa+" , "+ya);
				while(getClose(xa,ya,direction)==shape){
					//System.out.println(print(xa,ya));
					xa+=DIR_COR[direction][X_];
					ya+=DIR_COR[direction][Y_];
					listed.get(CHECK[direction]).add(new int[]{xa,ya});
					if(xa<0||xa==getGameWidth()){
						continue outer;
					}
					if(ya<0||ya==getGameHeight()){
						continue outer;
					}
				}
			}
			
		}
		boolean horizontal=listed.get(X_).size()>=3;
		boolean vertical =listed.get(Y_).size()>=3;
		if(horizontal&vertical){
			System.out.println("Bomb Vertical");
		}
		else if(horizontal){
			if(listed.get(X_).size()==4){
				System.out.println("Bomb Directional");
			}
		}
		if(horizontal){
			for(int[] arr:listed.get(X_)){
				deleteOne(arr[X_],arr[Y_]);
				//System.out.println("deleting");
			}
		}
		if(vertical){
			int max=Integer.MIN_VALUE;
			for(int[] arr:listed.get(Y_)){
				if(arr[Y_]>max){
					max=arr[Y_];
					
				}
			}
			//for(int []arr:listed.get(Y_)){
				//System.out.println(max);TODO ...
				delete(listed.get(Y_).get(0)[X_],max,3);
			//}
			
		}
		//long stop=System.nanoTime();
		//System.out.println("search:  "+(stop-start)/1000000+" ms");
	}
	public GameShape getClose(int x,int y,int direction){
		if(direction==LEFT){
			return x>0?game[x+DIR_COR[direction][X_]][y+DIR_COR[direction][Y_]]:GameShape.empty;
		}
		else if(direction==TOP){
			return y>0?game[x+DIR_COR[direction][X_]][y+DIR_COR[direction][Y_]]:GameShape.empty;
		}
		else if(direction==RIGHT){
			return x<game.length-1?game[x+DIR_COR[direction][X_]][y+DIR_COR[direction][Y_]]:GameShape.empty;
		}
		else if(direction==BOTTOM){
			return y<game[0].length-1?game[x+DIR_COR[direction][X_]][y+DIR_COR[direction][Y_]]:GameShape.empty;
		}
		else{
			System.out.println("KWLOS");
			return null;
		}
	}
	public GameShape[] getClose(int x,int y){
		GameShape[] values = new GameShape[DIRECTIONS.length];
		for(int direction:DIRECTIONS){
			values[direction]=getClose(x, y, direction);
		}
		return values;
	}
	public JMenu addFunctionalities(){
		return new JMenu("Actions");
	}
	public void blockActions(){
		block=true;
	}
	public void unBlockActions(){
		block=false;
	}
	public void swap(final int x1,final int y1,final int x2,final int y2){
		if(block){
			return;
		}
		if(x2>getGameWidth()-1||x2<0){
			return;
		}
		if(y2>getGameWidth()-1||y2<0){
			return;
		}
		blockActions();
		addTransition(Arrays.asList(new Transition(x1,y1,x2,y2,MatchThreePanel.this),new Transition(x2,y2,x1,y1,MatchThreePanel.this)));
	}
	public double[] steps(){
		if(game==null){
			return null;
		}
		double[]arr=new double[2];//left,right
		double width=getWidth();
		double height=getHeight();
		double length=game.length;
		double length2=game[0].length;
		arr[0]=width/length;
		arr[1]=height/length2;
		return arr;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(block){
			return;
		}
		selectAt(arg0.getX(),arg0.getY());
		deleteSelected();
	}
	public void deleteSelected(){
		if(getSelected()==null){
			return;
		}
		selected=null;
		deleteOne(selectedx,selectedy);
		selectedx=-1;
		selectedy=-1;
		
	}
	public void delete(final int width,final int height,final int verticalSteps){
		addActions(new Runnable(){
			@Override
			public void run() {
				deleted++;
				List<Transition>list = new ArrayList<Transition>();
				
				for(int i=0;i<height-verticalSteps+1;i++){
					
					list.add(new Transition(width, i, //from
											width, i+verticalSteps, //to
											MatchThreePanel.this));
				}
				if(canCreateMore()){
					for(int i=0;i<verticalSteps;i++){
						//game[width][height-i]=null;
						list.add(new Transition(width, -1, 
												width, i, //sto 0,1,2
												MatchThreePanel.this));
					}
				}
				addTransition(list);
			}
		});
	}
	public void deleteOne(int width,int height){
		delete(width,height,1);
	}
	public boolean canCreateMore(){
		return getCounter()<limitation;
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(block){
			return;
		}
		selected=null;
		flag=true;
	}
	public void selectAt(double x1,double y1){
		double[] ratio=steps();
		double x=x1/ratio[0];
		double y=y1/ratio[1];
		selectedx=(int)x;
		selectedy=(int)y;
		selected= new XYZ(selectedx*ratio[0]+ratio[0]/2,selectedy*ratio[1]+ratio[1]/2);
		repaint();
	}
	public void decideDirection(int newX,int newY){
		if(Math.abs(newX-getSelected().getX())>Math.abs(newY-getSelected().getY())){
			int directionx=(int)Math.signum(newX-getSelected().getX());
			swap(selectedx,selectedy,selectedx+directionx,selectedy);
		}
		else{
			int directiony=(int)Math.signum(newY-getSelected().getY());
			swap(selectedx,selectedy,selectedx,selectedy+directiony);
		}
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(block){
			return;
		}
		if(flag){
			selectAt(arg0.getX(),arg0.getY());
			flag=false;
		}
		else{
			if(XY.distance(getSelected().getX(),getSelected().getY(),arg0.getX(),arg0.getY())>LIMIT*getWidth()){
				decideDirection(arg0.getX(),arg0.getY());//);
				selected=null;
				flag=true;
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
	public List<Transition> getTransitions() {
		return transitions;
	}
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public static BufferedImage bufferizeImage(Class<?> clas,String uri) {
		ImageIcon image = new ImageIcon(clas.getResource(uri));
		BufferedImage im = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = im.getGraphics();
		image.paintIcon(null, g, 0, 0);
		g.dispose();
		return im;
	}
	/**
	 * 
	 */
}
