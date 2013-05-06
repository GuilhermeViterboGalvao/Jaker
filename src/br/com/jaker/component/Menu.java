package br.com.jaker.component;

import br.com.jaker.custom.component.JakerFragment;
import br.com.jaker.custom.component.SlidePageAdapter;
import br.com.jaker.view.R;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Menu extends RelativeLayout implements OnTouchListener {

	public Menu(Context context) {
		super(context);
	}
	
	public Menu(Context context, AttributeSet attributs){
		super(context, attributs);
	}
	
	public Menu(Context context, AttributeSet attributs, int defStyle){
		super(context, attributs, defStyle);
	}
	
	private FrameLayout frameLayout;
	
	private ViewPager viewPager;
	
	private Button btnMenu;
	
	private Slider slider;
	
	private int firstX = 0;
	
	private int currentX = 0;
	
	@Override
	protected void onFinishInflate() {
		btnMenu = new Button(getContext());
		btnMenu.setWidth(42);
		btnMenu.setHeight(42);
		btnMenu.setBackgroundResource(R.drawable.menu);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(42, 42);		
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);		
		addView(btnMenu, params);
		
		frameLayout = new FrameLayout(getContext());
		frameLayout.setBackgroundColor(Color.BLACK);
		frameLayout.setOnTouchListener(this);
		
		params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 42);
		params.addRule(RelativeLayout.RIGHT_OF, btnMenu.getId());
		params.leftMargin = 42;
		addView(frameLayout, params);
		
		slider = new Slider(getContext());
		slider.setOnTouchListener(this);
		frameLayout.addView(slider);
		
		viewPager = (ViewPager)findViewById(R.sliderPaginator.pager);
		viewPager.setOnTouchListener(this);
		params = (LayoutParams)viewPager.getLayoutParams();
		params.width = RelativeLayout.LayoutParams.FILL_PARENT;
		params.height = RelativeLayout.LayoutParams.FILL_PARENT;
		params.topMargin = 42;
		params.addRule(RelativeLayout.ALIGN_BOTTOM, btnMenu.getId());
		
		
		
		super.onFinishInflate();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {		
		if (view instanceof Slider || view instanceof FrameLayout) {
			switch (event.getAction()) {
			
				case MotionEvent.ACTION_MOVE:
					slider.setVisibility(VISIBLE);
					currentX = resize((int)event.getRawX());
					if (firstX == 0) {
						firstX = currentX;
					}
					break;
					
				case MotionEvent.ACTION_UP:
					if (firstX < currentX && viewPager.getCurrentItem() > 0) {
						// <------						
						viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
					} else if (firstX > currentX && (viewPager.getCurrentItem() + 1) < viewPager.getAdapter().getCount()) {
						// ------>						
						viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
					}
					slider.setVisibility(GONE);
					firstX = 0;
					break;
					
			}
			slider.onTouchEvent(event);
		} else if (view instanceof ViewPager) {
			SlidePageAdapter adapter = (SlidePageAdapter)viewPager.getAdapter();
			JakerFragment fragment = (JakerFragment)adapter.getItem(viewPager.getCurrentItem());
			WebView webView = fragment.getWebView();
			webView.onTouchEvent(event);
		}
		
		return true;
	}
	
	public int resize(int x) {
		return (x * viewPager.getWidth() * (viewPager.getCurrentItem() + 1)) / frameLayout.getWidth();
	}
}