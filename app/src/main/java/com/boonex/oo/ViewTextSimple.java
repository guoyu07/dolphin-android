package com.boonex.oo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewTextSimple extends LinearLayout {
	
	protected TextView m_viewText;
	protected LinearLayout m_layout;
		
    public ViewTextSimple(Context context, String sText) {
        super(context);
        
        LayoutInflater.from(context).inflate(R.layout.view_text_simple, this, true);
               
        m_layout = (LinearLayout) findViewById(R.id.layout);
        m_viewText = (TextView) findViewById(R.id.text_simple);
        
        m_viewText.setText(sText);
                             
    }

    
}
