package br.com.jaker.custom.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class JakerViewPager extends ViewPager {

	public JakerViewPager(Context context) {
		super(context);
	}
	
	public JakerViewPager(Context context, AttributeSet attributs){
		super(context, attributs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return false;
	}
}