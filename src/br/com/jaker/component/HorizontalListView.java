package br.com.jaker.component;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class HorizontalListView extends HorizontalScrollView implements OnTouchListener, OnGestureListener {
	
	public HorizontalListView(Context context, int maxItem,	int itemWidth) {
		this(context);
		this.maxItem = maxItem;
		this.itemWidth = itemWidth;
		this.gestureDetector = new GestureDetector(this);
		this.setOnTouchListener(this);
	}

	private HorizontalListView(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	private static final int SWIPE_THRESHOLD_VELOCITY = 300;
	private static final int SWIPE_MIN_DISTANCE = 240;
	private GestureDetector gestureDetector;
	private boolean flingDisable = true;
	private float prevScrollX = 0;
	private boolean start = true;
	private float currentScrollX;
	private int activeItem = 0;
	private int itemWidth = 0;
	private int scrollTo = 0;
	private int maxItem = 0;		

	@Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (gestureDetector.onTouchEvent(event)) return true;
		Boolean returnValue = gestureDetector.onTouchEvent(event);
		int x = (int) event.getRawX();
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (start) {
					this.prevScrollX = x;
					start = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				start = true;
				this.currentScrollX = x;
				int minFactor = itemWidth / 10;
				if ((this.prevScrollX - this.currentScrollX) > minFactor) {
					if (activeItem < maxItem - 1)activeItem++;
				} else if ((this.currentScrollX - this.prevScrollX) > minFactor) {
					if (activeItem > 0)	activeItem--;
				}
				scrollTo = activeItem * itemWidth;
				this.smoothScrollTo(scrollTo, 0);
				returnValue = true;
				break;
		}
		return returnValue;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (flingDisable) return false;
			
		boolean returnValue = false;
		float ptx1 = 0, ptx2 = 0;
		
		if (e1 == null || e2 == null) return false;
		
		ptx1 = e1.getX();
		ptx2 = e2.getX();

		if (ptx1 - ptx2 > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			if (activeItem < maxItem - 1) activeItem++;				
			returnValue = true;
		} else if (ptx2 - ptx1 > SWIPE_MIN_DISTANCE	&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			if (activeItem > 0) activeItem--;
			returnValue = true;
		}
		scrollTo = activeItem * itemWidth;
		this.smoothScrollTo(0, scrollTo);
		return returnValue;
	}

	
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) { return false; }
	
	@Override
	public boolean onSingleTapUp(MotionEvent event) { return false; }
	
	@Override
	public boolean onDown(MotionEvent e) { return false; }
	
	@Override
	public void onLongPress(MotionEvent event) { }

	@Override
	public void onShowPress(MotionEvent event) { }
}