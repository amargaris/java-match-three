package graphical.model.gfx.overlays;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JPanel;

public abstract class Renderer<T> {
	
	private Object synchronizationReference;
	private float alpha;
	private Composite composite;
	private String alias;
	private Supplier<Object> syncGetter;
	private Icon icon;
	
	/**
	 * Simple Constructor, no synchronization dependencies
	 */
	public Renderer(){
		setAlpha(1.0f);
	}
	/**
	 * Constructor with synchronization reference parameter an already active Object
	 * @param ref	The object to be synchronized with.
	 */
	public Renderer(Object ref){
		this(()->ref);
	}
	/**
	 * Constructor with a callback method to acquire the synchronization Object (for cases where the synchronization object changes
	 * or is not yet initialized)
	 * @param syncGetter	The method that returns the referenced object. JAVA 8 will replace this.
	 */
	public Renderer(Supplier<Object> get){
		this();
		this.syncGetter = get;
	}
	
	public Renderer<T> setIcon(Icon icon){
		this.icon  =icon;
		return this;
	}
	
	public Renderer<T> alias(String alias){
		this.alias = alias;
		return this;
	}
	
	public String getAlias(){
		return alias;
	}
	
	public Renderer<T> setAlpha(float newAlpha){
		this.alpha = newAlpha;
		this.composite = makeComposite();
		return this;
	}
	
	private AlphaComposite makeComposite() {
		int type = AlphaComposite.SRC_OVER;
		return AlphaComposite.getInstance(type, alpha);
	}
	
	public Icon icon(){
		return icon;
	}
	
	public void checkSync(){
		if(synchronizationReference == null){
			if(syncGetter != null){
				synchronizationReference = syncGetter.get();
			}
		}
	}
	
	public void paint(T panel,JPanel pan,Graphics2D g){
		Composite originalComposite = null;
		if(alpha != 1.0f){
			originalComposite = g.getComposite();
			g.setComposite(composite);
		}
		checkSync();
		if(synchronizationReference != null){
			synchronized(synchronizationReference){
				doPaint(panel,pan,g);
			}
		} else {
			doPaint(panel,pan,g);
		}
		if(originalComposite != null){
			g.setComposite(originalComposite);
		}
	}
	protected abstract void doPaint(T model,JPanel cont,Graphics2D g);
}
