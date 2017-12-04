package graphical.model.threads;

import graphical.model.enume.GameShape;
import graphical.model.gfx.MatchThreePanel;

public class Transition{
	private double step=20;
	private int counter;
	public double xlocation;
	public double ylocation;
	private int x_,y_;
	double xTrans;
	double yTrans;
	public volatile  GameShape completionShape1;
	GameShape completionShape2;
	MatchThreePanel panel;
	public Transition(int fromX,int fromY,int x2,int y2,MatchThreePanel panel){
		System.out.println(String.format("[%d,%d]--->[%d,%d]",fromX,fromY,x2,y2));
		double[] steps=panel.steps();
		this.x_=x2;
		this.y_=y2;
		this.panel=panel;
		xTrans=-(fromX-x2)*steps[0]/step;
		if(fromY!=-1){
			this.completionShape1=panel.getAt(fromX,fromY);
			this.completionShape2=panel.getAt(x2, y2);
			//
			yTrans=-(fromY-y2)*steps[1]/step;
		}
		else{
			this.completionShape1=MatchThreePanel.getRandom();
			panel.setCounter(panel.getCounter() + 1);
			yTrans=-(fromY )*steps[1]/step;
		}
		xlocation=fromX*steps[0];
		ylocation=fromY*steps[1];
		counter=0;
	}
	public void init(){
		panel.setAt(null, x_, y_);
	}
	
	public Transition advance(){
		
		if(counter==step){
			//panel.game[x_][y_]=shape;
			panel.setAt(completionShape1, x_, y_);
			panel.addActions(() ->{
					panel.searchForDeletion(x_, y_);
					panel.unBlockActions();
				
			});
			return this;
		}
		counter++;
		xlocation+=xTrans;
		ylocation+=yTrans;
		return null;
	}
	public GameShape getShape(){
		return completionShape1;
	}
}