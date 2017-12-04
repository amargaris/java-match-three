package graphical.model.gfx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import graphical.model.gfx.overlays.Renderer;

public class MultiLayerPanel<T> extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private List<Renderer<T>> overlays;
	private Timer time;
	private static final int DEFAULT_INTERVAL = 100;
	private int interval;
	private Supplier<T> sup;

	public MultiLayerPanel(Supplier<T> getter){
		this(DEFAULT_INTERVAL,getter);
	}
	public MultiLayerPanel(int refreshInterval,Supplier<T> getter){
		super();
		setSupplier(getter);
		this.interval = refreshInterval;
		interval = DEFAULT_INTERVAL;
		overlays = new ArrayList<>();
		setOpaque(true);
		setBackground(Color.gray);
		setLayout(new BorderLayout());
		time = new Timer(interval,this);
		time.setCoalesce(true);
		time.setRepeats(true);
		time.start();
		setFocusable(true);
	}

	public void add(Renderer<T> overlay){
		overlays.add(overlay);
	}
	public MultiLayerPanel<T> display(String title){
		final JFrame frame = new JFrame(title);
		frame.setSize(500,500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.getContentPane().setBackground(Color.white);
		SwingUtilities.invokeLater(()->frame.setVisible(true));
		SwingUtilities.invokeLater(()->{
			install(frame);
			SwingUtilities.invokeLater(()->frame.repaint());
		});
		return this;
	}
	public void install(JFrame fram){
		fram.getContentPane().invalidate();
		fram.getContentPane().removeAll();
		fram.getContentPane().add(MultiLayerPanel.this,BorderLayout.CENTER);
		fram.getContentPane().validate();
		fram.getContentPane().repaint();
		
		fram.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				//time.stop();
				System.exit(0);
			}
		});
		SwingUtilities.invokeLater(()->this.requestFocus());
	}
	@SuppressWarnings("unchecked")
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		try{
			g.setColor(Color.black);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
			Graphics2D g2=(Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for(@SuppressWarnings("rawtypes") Renderer over:overlays){
				over.paint(getCore(),this,g2);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	public void setSupplier(Supplier<T> sup){
		this.sup = sup;
	}
	public T getCore(){
		return sup.get();
	}
}