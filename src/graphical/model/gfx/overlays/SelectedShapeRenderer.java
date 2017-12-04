package graphical.model.gfx.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import graphical.model.gfx.MatchThreePanel;


public class SelectedShapeRenderer extends Renderer<MatchThreePanel>{
	@Override
	public void doPaint(MatchThreePanel gvgPanel,JPanel p, Graphics2D g) {
		if(gvgPanel.getSelected()==null){
			return;
		}
		double[] vals=gvgPanel.steps();
		int selectedX=gvgPanel.selectedx;
		int selectedY=gvgPanel.selectedy;
		g.setColor(Color.cyan);
		g.fill(new RoundRectangle2D.Double(selectedX*vals[0], selectedY*vals[1], vals[0], vals[1], vals[0]/4, vals[1]/4));// Ellipse2D.Double(i*x_step, j*y_step, x_step, y_step));
	}
}