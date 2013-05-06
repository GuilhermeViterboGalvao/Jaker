package br.com.jaker.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Slider extends View {

	public Slider(Context context) {
		super(context);
		init();
	}
	
	public Slider(Context context, AttributeSet attributs){
		super(context, attributs);
		init();
	}
	
	public Slider(Context context, AttributeSet attributs, int defStyle){
		super(context, attributs, defStyle);
		init();
	}
	
	private void init() {
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3);
	}
	
	private Paint paint;
	
	private float cx;
	
	private float cy;
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(cx, cy, 10, paint);		
		canvas.restore();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cx = event.getX();
		cy = event.getY();
		invalidate();		
		return true;
	}
}