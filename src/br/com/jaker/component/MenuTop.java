package br.com.jaker.component;

import br.com.jaker.view.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MenuTop extends RelativeLayout implements OnClickListener {
	
	private Button btnMenu;
	
	private OnMenuButtonClick onMenuButtonClick;
	
	private Button btnSlider;
	
	private OnSlide onSlide;
	
	private Context context;
	
	public MenuTop(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public MenuTop(Context context, AttributeSet attributs){
		super(context, attributs);
		this.context = context;
		init();
	}
	
	public MenuTop(Context context, AttributeSet attributs, int defStyle){
		super(context, attributs, defStyle);
		this.context = context;
		init();
	}
	
	private void init() {
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
		
		params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 42);
		params.addRule(RelativeLayout.RIGHT_OF, btnMenu.getId());
		addView(btnSlider, params);
	}
	
	@Override
	public void onClick(View v) {
		if (v.equals(btnMenu) && onMenuButtonClick != null) {
			onMenuButtonClick.click(btnMenu);
		} else if (v.equals(btnSlider) && onSlide != null) {
			onSlide.slide(btnSlider);
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
}