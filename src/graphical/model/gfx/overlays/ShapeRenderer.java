package graphical.model.gfx.overlays;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;

import graphical.model.enume.GameShape;
import graphical.model.gfx.MatchThreePanel;

public class ShapeRenderer extends Renderer<MatchThreePanel>{
	private int previousx,previousy;
	public static volatile BufferedImage[] saved;
	static{
		saved = new BufferedImage[GameShape.values().length-1];
	}
	public ShapeRenderer(){
		previousx=-1;
		previousy=-1;
	}
	@Override
	public void doPaint(MatchThreePanel gvgPanel,JPanel pan,Graphics2D g) {
		checkBounds(gvgPanel);
		double[] vals=gvgPanel.steps();
		for(int i=0;i<gvgPanel.getGameWidth();i++){
			for(int j=0;j<gvgPanel.getGameHeight();j++){
				if(gvgPanel.getAt(i,j)==null||gvgPanel.getAt(i,j)==GameShape.empty){
					continue;
				}
				AffineTransform t = new AffineTransform();
		        t.translate(i*vals[0], j*vals[1]); // x/y set here, ball.x/y = double, ie: 10.33
		        t.scale(1, 1); // scale = 1 
				g.drawImage(saved[gvgPanel.getAt(i,j).ordinal()],t,null);
			}
		}
	}

	public void checkBounds(MatchThreePanel panel){
		if(previousx==-1||previousx!=panel.getWidth()||previousy!=panel.getHeight()){
			previousx=panel.getWidth();
			previousy=panel.getHeight();
			for(int i=0;i<GameShape.values().length-1;i++){
				saved[i]= Scalr.resize(
						MatchThreePanel.bufferizeImage(MatchThreePanel.class, "resources/"+GameShape.values()[i].name()+".png"), 
						Mode.FIT_EXACT, 
						(int)(panel.getWidth()/panel.getGameWidth()),
						(int)(panel.getHeight()/panel.getGameHeight()));
				}
		}
	}
}