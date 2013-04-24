package br.com.jaker.component;

import br.com.jaker.view.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MenuTop extends RelativeLayout implements OnClickListener, OnTouchListener {
	
	private Button btnMenu;
	
	private OnMenuButtonClick onMenuButtonClick;
	
	private Button btnSlider;
	
	private OnSlide onSlide;
	
	private ViewPager viewPager;
	
	private Context context;
	
	private int currentX = 0;
	
	private boolean isSmoothingScrool = false;
	
	public MenuTop(Context context) {
		super(context);
		this.context = context;
		init(0);
	}
	
	public MenuTop(Context context, AttributeSet attributs){
		super(context, attributs);
		this.context = context;
		init(attributs.getAttributeIntValue("viewPagerId", "viewPagerId", 0));
	}
	
	public MenuTop(Context context, AttributeSet attributs, int defStyle){
		super(context, attributs, defStyle);
		this.context = context;
		init(attributs.getAttributeIntValue("viewPagerId", "viewPagerId", 0));
	}
	
	private void init(int idViewPager) {
		btnMenu = new Button(context);
		btnMenu.setOnClickListener(this);
		btnMenu.setWidth(42);
		btnMenu.setHeight(42);
		btnMenu.setBackgroundResource(R.drawable.menu);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(42, 42);		
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);		
		addView(btnMenu, params);
		
		btnSlider = new Button(context);
		btnSlider.setOnClickListener(this);
		btnSlider.setHeight(42);
		btnSlider.setText("Deslise seu dedo aqui");
		btnSlider.setBackgroundColor(0xffffff);
		btnSlider.setOnTouchListener(this);
		
		
				
		params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 42);
		params.addRule(RelativeLayout.RIGHT_OF, btnMenu.getId());
		addView(btnSlider, params);
		
		//Log.i("ID", "" + getRootView().getResources().getIdentifier("pager", "sliderPaginator", null));
		
		viewPager = (ViewPager)findViewById(R.sliderPaginator.pager);
		
	}
	
	@Override
	public void onClick(View v) {
		if (v.equals(btnMenu) && onMenuButtonClick != null) {
			onMenuButtonClick.click(btnMenu);
		}
	}	
		
	public void setOnMenuButtonClick(OnMenuButtonClick onMenuButtonClick) {
		this.onMenuButtonClick = onMenuButtonClick;
	}

	public void setOnSlide(OnSlide onSlide) {
		this.onSlide = onSlide;
	}

	public interface OnMenuButtonClick {
		void click(View view);
	}
	
	public interface OnSlide {
		void slide(View view);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (currentX == 0) {
					currentX = (int)event.getRawX();
				} else if (currentX < (int)event.getRawX() && !isSmoothingScrool) {
					//Moving to right
					viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
					isSmoothingScrool = true;
					onSlide.slide(v);
				} else if (currentX > (int)event.getRawX() && !isSmoothingScrool) {
					//Moving to left
					viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
					isSmoothingScrool = true;
					onSlide.slide(v);
				}				
				break;
				
			case MotionEvent.ACTION_UP:
				currentX = 0;
				isSmoothingScrool = false;
				break;
		}

		return true;
	}
}