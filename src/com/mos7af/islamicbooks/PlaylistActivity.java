package com.mos7af.islamicbooks;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class PlaylistActivity extends Activity 
{
	private final int CONTEXT_MENU_ID = 1;
	private IconContextMenu iconContextMenu = null;
	
	private final int MENU_ITEM_1_ACTION = 1;
	private final int MENU_ITEM_2_ACTION = 2;
	private final int MENU_ITEM_3_ACTION = 3;
	
	private int selectedIndex = 0;
	
	private PlaylistItemAdapter playlistItemAdapter ;
	private ListView listView ;
	private DatabaseHandler db ;
	private ArrayList<HashMap<String, String>> surasList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.ly_playlist);
        
        playlistItemAdapter = new PlaylistItemAdapter(this );
        listView = (ListView)findViewById(R.id.list);
        db = new DatabaseHandler(getApplicationContext());
        Resources res = getResources();
       
		iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);
		iconContextMenu.addItem(res, getString(R.string.fav_show_books), R.drawable.ic_1, MENU_ITEM_1_ACTION);
		iconContextMenu.addItem(res, getString(R.string.fav_delete), R.drawable.ic_delete, MENU_ITEM_2_ACTION);
		iconContextMenu.addItem(res, getString(R.string.fav_rename), R.drawable.ic_rename, MENU_ITEM_3_ACTION);
          
         
          iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
     			@Override
     			public void onClick(int menuId) {
     				
     				switch(menuId) {
     				case MENU_ITEM_1_ACTION:
     					
     					 int index = selectedIndex;
     	        	    ArrayList<HashMap<String, String>> _surasList =  db.getPlaylistSuras(surasList.get(index).get("playlistId"));
     	          	   if(_surasList.size()==0)
     	          	   {
     	          		 Toast.makeText(getApplicationContext(), getString(R.string.fav_empty),
     								Toast.LENGTH_SHORT).show(); 
     	          	   }else
     	          	   {
     	          		   BookslistManager s= BookslistManager.getInstance();
     	        	    	s.SetBooksList(_surasList);
     	          	    	Intent i = new Intent(getApplicationContext(), PlayListSurasActivity.class);
     	          	    	startActivityForResult(i, 100);	
     	          	   }
     					break;
     				case MENU_ITEM_2_ACTION:
     					boolean result =  db.deletePlaylist(surasList.get(selectedIndex).get("playlistId"));
     					if(result)
     						Toast.makeText(getApplicationContext(), getString(R.string.fav_delete_success),
         							Toast.LENGTH_SHORT).show();
     					 loadCurrentPlaylists();
     					break;
     				case MENU_ITEM_3_ACTION:
     					ShowRenameDialog();
     					break;
     				}
     			}});
        
        loadCurrentPlaylists();
        RelativeLayout relativeclic1 =(RelativeLayout)findViewById(R.id.footer);
        relativeclic1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	insertPlaylist();
            	Toast.makeText(getApplicationContext(), getString(R.string.fav_add_success),
							Toast.LENGTH_SHORT).show();
            }
        });
        
    }
    private void loadCurrentPlaylists() {
		

		// Spinner Drop down elements
    	surasList = db.getAllPlaylists();
		playlistItemAdapter.SetData(surasList);
		listView.setAdapter(playlistItemAdapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				selectedIndex = pos;
				showDialog(CONTEXT_MENU_ID);
				return false;
			}});
    	
    	listView.setOnItemClickListener(new OnItemClickListener() {
        
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	    
        	    int index = position;
        	    ArrayList<HashMap<String, String>> _surasList =  db.getPlaylistSuras(surasList.get(index).get("playlistId"));
          	   if(_surasList.size()==0)
          	   {
          		 Toast.makeText(getApplicationContext(),  getString(R.string.fav_empty),
							Toast.LENGTH_SHORT).show(); 
          	   }else
          	   {
          		   BookslistManager s= BookslistManager.getInstance();
        	    	s.SetBooksList(_surasList);
          	    	Intent i = new Intent(getApplicationContext(), PlayListSurasActivity.class);
          	    	startActivityForResult(i, 100);	
          	   }
        	  }
        	});
	}
    @Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID) {
			return iconContextMenu.createMenu( getString(R.string.fav));
		}
		
		return super.onCreateDialog(id);
	}
    @Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	
        	ArrayList<HashMap<String, String>> _surasList = db.getPlaylistSuras(surasList.get(selectedIndex).get("playlistId"));
			  if(_surasList.size()==0)
         	   {
         		 Toast.makeText(getApplicationContext(),getString(R.string.fav_empty),
							Toast.LENGTH_SHORT).show(); 
         	   }else
         	   {
					BookslistManager songsManager=BookslistManager.getInstance();
					songsManager.deletAllBooks();
					songsManager.SetSongs(_surasList);
					IslamicBooks.tabIndex =1;
					
         	   }
        }
      
 
    }
    public void insertPlaylist() 
    {
		String label = getString(R.string.fav_new);
		if (label.trim().length() > 0)
		{
			db.insertPlaylist(label);
			loadCurrentPlaylists();
		} 

	}
    private void  ShowRenameDialog() {
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.fav_rename_to)+surasList.get(selectedIndex).get("playlistName")+" ل ");
         final EditText input = new EditText(this);
         input.setMaxLines(1);
         input.append(surasList.get(selectedIndex).get("playlistName"));
         input.setLines(1);
         input.setSingleLine();
         InputFilter[] FilterArray = new InputFilter[1];
         FilterArray[0] = new InputFilter.LengthFilter(15);
         input.setFilters(FilterArray);
         input.setSelection(0,input.getText().length());
         input.setId(1);
         InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
         builder.setView(input);
         builder.setPositiveButton(getString(R.string.fav_save), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				 String value = input.getText().toString();
				 db.UpdatePlaylist(surasList.get(selectedIndex).get("playlistId"), value);
				 Toast.makeText(getApplicationContext(), getString(R.string.fav_rename_success),
							Toast.LENGTH_SHORT).show();
				 loadCurrentPlaylists();
				 dialog.cancel();
				
			}
		});
        builder.setNegativeButton(getString(R.string.fav_cancel), new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        
		// show it
		alertDialog.show();
      
    }
}