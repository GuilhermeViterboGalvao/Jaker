package br.com.jaker.component;

import br.com.jaker.custom.component.JakerFragment;
import br.com.jaker.custom.component.SlidePageAdapter;
import br.com.jaker.view.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MenuTop extends RelativeLayout implements OnClickListener, OnTouchListener {
	
	private Button btnMenu;
	
	private OnMenuButtonClick onMenuButtonClick;
	
	private Button btnSlider;
	
	private OnSlide onSlide;
	
	private ViewPager viewPager;
	
	private int currentX = 0;
	
	private boolean isSmoothingScrool = false;
	
	public MenuTop(Context context) {
		super(context);
	}
	
	public MenuTop(Context context, AttributeSet attributs){
		super(context, attributs);
	}
	
	public MenuTop(Context context, AttributeSet attributs, int defStyle){
		super(context, attributs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		btnMenu = new Button(getContext());
		btnMenu.setOnClickListener(this);
		btnMenu.setWidth(42);
		btnMenu.setHeight(42);
		btnMenu.setBackgroundResource(R.drawable.menu);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(42, 42);		
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);		
		addView(btnMenu, params);
		
		btnSlider = new Button(getContext());
		btnSlider.setOnClickListener(this);
		btnSlider.setHeight(42);
		btnSlider.setText("Deslise seu dedo aqui");
		btnSlider.setBackgroundColor(0xffffff);
		btnSlider.setOnTouchListener(this);
						
		params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 42);
		params.addRule(RelativeLayout.RIGHT_OF, btnMenu.getId());
		addView(btnSlider, params);
		
		viewPager = (ViewPager)findViewById(R.sliderPaginator.pager);
		viewPager.setOnTouchListener(this);
		viewPager.setClickable(false);
		params = (LayoutParams)viewPager.getLayoutParams();
		params.width = RelativeLayout.LayoutParams.FILL_PARENT;
		params.height = RelativeLayout.LayoutParams.FILL_PARENT;
		params.topMargin = 42;
		params.addRule(RelativeLayout.ALIGN_BOTTOM, btnMenu.getId());		
	}
	
	@Override
	public void onClick(View v) {
		if (v.equals(btnMenu) && onMenuButtonClick != null) {
			onMenuButtonClick.click(btnMenu);
		}
	}	

	public interface OnMenuButtonClick {
		void click(View view);
	}
	
	public void setOnMenuButtonClick(OnMenuButtonClick onMenuButtonClick) {
		this.onMenuButtonClick = onMenuButtonClick;
	}

	public interface OnSlide {
		void slide(View view);
	}
	
	public void setOnSlide(OnSlide onSlide) {
		this.onSlide = onSlide;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (v.equals(btnSlider)) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:				
					if (currentX == 0) {
						currentX = (int)event.getRawX();
					} else if (currentX > (int)event.getRawX() 
								&& !isSmoothingScrool 
									&& viewPager.getCurrentItem() < viewPager.getAdapter().getCount()) {
						//Moving to left
						viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
						isSmoothingScrool = true;
						if (onSlide != null) {
							onSlide.slide(v);
						}
					} else if (currentX < (int)event.getRawX() 
								&& !isSmoothingScrool 
									&& viewPager.getCurrentItem() != 0) {
						//Moving to right
						viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
						isSmoothingScrool = true;
						if (onSlide != null) {
							onSlide.slide(v);
						}
					}		
					break;
					
				case MotionEvent.ACTION_UP:
					currentX = 0;
					isSmoothingScrool = false;
					break;
			}	
		} else if (v.equals(viewPager)) {
			SlidePageAdapter adapter = (SlidePageAdapter)viewPager.getAdapter();
			JakerFragment fragment = (JakerFragment)adapter.getItem(viewPager.getCurrentItem());
			WebView webView = fragment.getWebView();
			webView.onTouchEvent(event);
		}

		return true;
	}
}