package graphical.model.gfx.overlays;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import graphical.model.gfx.MatchThreePanel;
import graphical.model.threads.Transition;

public class TransitionRenderer extends Renderer<MatchThreePanel>{
	public TransitionRenderer(Object ob){
		super(ob);
	}
	@Override
	public void doPaint(MatchThreePanel gvgPanel,JPanel pan, Graphics2D g) {
		for(Transition transition:gvgPanel.getTransitions()){
			AffineTransform t = new AffineTransform();
	        t.translate(transition.xlocation, transition.ylocation);
	        t.scale(1, 1);
	        if(transition.completionShape1 != null){
		        BufferedImage bi = ShapeRenderer.saved[transition.completionShape1.ordinal()];
		        if(bi != null)
		        	g.drawImage(bi, t, null);
	        }
		}
	}
}