package com.boonex.oo.media;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.boonex.oo.ActivityBase;
import com.boonex.oo.Connector;
import com.boonex.oo.LoaderImageView;
import com.boonex.oo.Main;
import com.boonex.oo.R;

public class ImagesGallery extends ActivityBase {

	private static final String TAG = "ImagesGallery";
	
	protected LoaderImageView m_viewImageLoader;
	protected Button m_btnNext, m_btnPrev;
	protected String m_sUsername;
	protected String m_sAlbumId;
	protected String m_sPhotoId;
	protected Integer m_iIndex;
	protected List<Map<String, Object>> m_listImages;
	protected ImagesGallery m_actImagesGallery;	
	protected Object m_aFiles[];
	protected ImagesFilesAdapter m_adapterFiles;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		
		this.setContentView(R.layout.media_images_gallery);
		setTitleCaption (R.string.title_image_gallery);
		
		m_actImagesGallery = this;
		
		m_viewImageLoader = (LoaderImageView)this.findViewById(R.id.media_images_image_view);
		m_btnNext = (Button)this.findViewById(R.id.media_images_gallery_next);
		m_btnPrev = (Button)this.findViewById(R.id.media_images_gallery_prev);
		
        Intent i = getIntent();                
        m_sUsername = i.getStringExtra("username");
        m_listImages = (List<Map<String, Object>>)i.getSerializableExtra("list");
        Log.d(TAG, "m_listImages: " + m_listImages);
        if (m_listImages != null) {
        	setImageIndex (i.getIntExtra("index", 0));        	
        } else {
        	m_sAlbumId = i.getStringExtra("album_id");
        	m_sPhotoId = i.getStringExtra("photo_id");
        	reloadRemoteData();
        }
		
		m_btnNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setImageIndex(m_iIndex + 1);
			}
		});
			
		m_btnPrev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setImageIndex(m_iIndex - 1);
			}
		});		
	}

    protected void reloadRemoteData() {
        
    	Connector oConnector = Main.getConnector();
    	
        Object[] aParams = {
        		oConnector.getUsername(), 
        		oConnector.getPassword(),
        		m_sUsername,
        		m_sAlbumId
        };                                    
                       
        
        oConnector.execAsyncMethod("dolphin.getImagesInAlbum", aParams, new Connector.Callback() {
			
			public void callFinished(Object result) {
				Log.d(TAG, "dolphin.getImagesInAlbum result: " + result.toString());
				
				m_aFiles = null;
				m_aFiles = (Object [])result;
							
				Log.d(TAG, "dolphin.getImagesInAlbum num: " + m_aFiles.length);
				
				if (m_aFiles.length < 1) {
					m_actThis.finish();
				} else {
					m_adapterFiles = null;
					m_adapterFiles = new ImagesFilesAdapter(m_actThis, m_aFiles, m_sUsername);
					m_listImages = m_adapterFiles.getListStorage();
					setImageIndex (m_adapterFiles.getPositionByFileId(m_sPhotoId));
				}
			}
        }, this);    	
    }
    
    protected MediaFilesAdapter getAdapterInstance (Context context, Object[] aFiles, String sUsername) {    	    	
		return (MediaFilesAdapter)new ImagesFilesAdapter(context, aFiles, sUsername);              	
    }
    
	protected void setImageIndex (Integer i) {
		int n = m_listImages.size();		
		if (i < 0)
			i = 0;
		if (i > n - 1)
			i = n - 1;

		Map<String, Object> map = m_listImages.get(i);
		m_viewImageLoader.setImageDrawable((String)map.get("file"));
				
		if (1 == n || 0 == i)
			m_btnPrev.setVisibility(Button.INVISIBLE);
		else
			m_btnPrev.setVisibility(Button.VISIBLE);
		
		if (1 == n || n - 1 == i)
			m_btnNext.setVisibility(Button.INVISIBLE);
		else
			m_btnNext.setVisibility(Button.VISIBLE);
		
		m_iIndex = i;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
        Connector oConnector = Main.getConnector();
                
        if (m_sUsername.equalsIgnoreCase(oConnector.getUsername())) {        		
        	MenuInflater inflater = getMenuInflater();
        	inflater.inflate(R.menu.media_images_gallery_menu, menu);
        	return true;
        }
        
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()) {
		
		case R.id.media_images_gallery_set_as_avatar:
			
			Connector oConnector = Main.getConnector();
			Map<String, Object> map = m_listImages.get(m_iIndex);
			
	        Object[] aParams = {
	        		oConnector.getUsername(), 
	        		oConnector.getPassword(),
	        		map.get("id")
	        };                                    
	                               
	        oConnector.execAsyncMethod("dolphin.makeThumbnail", aParams, new Connector.Callback() {
				
				public void callFinished(Object result) {
					Log.d(TAG, "dolphin.makeThumbnail result: " + result.toString());
					String sMsg;
					if (result.toString().equals("ok")) {
						sMsg = getString(R.string.media_images_gallery_set_as_avatar_ok);
					} else {
						sMsg = getString(R.string.media_images_gallery_set_as_avatar_fail);
					}
					Toast.makeText(m_actImagesGallery, sMsg, Toast.LENGTH_SHORT).show();
				}
	        }, this);
	        
			break;			
		}
		return true;	
	}	
}