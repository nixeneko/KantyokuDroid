/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kantyokudroid.softkeyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

public class KantyokuKeyboard extends Keyboard {
	private KeyTable currentTable;
    private KeyTable table;
    private Key mEnterKey;
    private Key mSpaceKey;
    private Key mKanaKey;
    private Key[] mAlphabetKeys = new Key[31];
    private boolean katakanaState = false;
    
    // 30 (or 40) matrix keys, hiragana-katakana toggle key, space key, enter key 
    // are needed.
    
    public KantyokuKeyboard(Context context, int xmlLayoutResId) {
    	super(context, xmlLayoutResId);
        

    	List<Key> keys = this.getKeys();
    	Key key;
    	for (int i = 0; i<keys.size(); i++){
    	  key = keys.get(i);
    	  if (key.codes[0] == 10) {
    		  mEnterKey = key;
    	  } else if (key.codes[0] == ' ') {
    		  mSpaceKey = key;
    	  } else if (key.codes[0] >= 1010 && key.codes[0] <= 1040){
    		  //System.out.println(String.valueOf(key.codes[0]-1010));
    		  mAlphabetKeys[key.codes[0]-1010] = key;
//    		  key.label = null;
    	  } else if (key.codes[0] == 1041) { // kana key
    		  mKanaKey = key;
    	  }
    	}
    	
    	this.table = parseTable(context);
    	this.currentTable = this.table;
    	updateKeytops();
//		System.out.println(parseTable(context).table[0]);
	}
    
    private KeyTable parseTable(Context context){
    	int eventType;
    	String tagname = null;
    
    	XmlResourceParser xrp = context.getResources().getXml(R.xml.tut_table);

    	try {
			eventType = xrp.getEventType(); 
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if(eventType == XmlResourceParser.START_TAG) {
					tagname = xrp.getName();
//					System.out.println("Start tag "+tagname+", "+xrp.getDepth()+", "+String.valueOf(tagname=="table"));
					if(tagname.equals("table")){
						return parseTableKeys(xrp, xrp.getDepth());
					}
					
				} else if(eventType == XmlResourceParser.END_TAG) {
//            		System.out.println("End tag "+xrp.getName()+", "+xrp.getDepth());
            	}
				eventType = xrp.next();
        	}
//        	System.out.println("End document");
        } catch (XmlPullParserException e) {
        	System.out.println("XmlPullParserExeption");
        } catch (IOException e) {
			System.out.println("IOException");
		};
		return null;
    }
    
    private KeyTable parseTableKeys(XmlResourceParser xrp, int currentDepth){
//    	System.out.println("just called: parseTableKeys with depth of " + currentDepth);
    	int depth = currentDepth+1;
    	int eventType;
    	String tagname = null;
    	String chars = null;
    	String keytop = null;
    	KeyTable key;
    	ArrayList<KeyTable> keys = new ArrayList<KeyTable>();
    	
		try {
			eventType = xrp.getEventType();
			
			if(eventType == XmlResourceParser.START_TAG) {
//				System.out.println("Start tag "+xrp.getName()+", "+xrp.getDepth()+",  "+xrp.getAttributeCount());
				tagname = xrp.getName();
				if (tagname.equals("table")){
					eventType = xrp.next();
					while (xrp.getDepth() >= depth){
						if (eventType == XmlResourceParser.START_TAG){
							key = parseTableKeys(xrp, xrp.getDepth());
							if (key != null){
								keys.add(key);
							}
						} else {
							eventType = xrp.next();
						}
					}
					return new KeyTable(keys, "");
				}else if (tagname.equals("keys")){
					eventType = xrp.next();
					while (xrp.getDepth() >= depth){
						if (eventType == XmlResourceParser.START_TAG){
							key = parseTableKeys(xrp, xrp.getDepth());
							if (key != null){
								keys.add(key);
							}
						} else {
							eventType = xrp.next();
						}
					}
					return new KeyTable(keys, "");
				}else if (tagname.equals("key")){
					eventType = xrp.getEventType();
					for(int i=0; i<xrp.getAttributeCount(); i++){
//						System.out.println(xrp.getAttributeValue(i));
						String attrname = xrp.getAttributeName(i);
						if (attrname.equals("str")){
							chars = xrp.getAttributeValue(i);
						}
					}
					eventType = xrp.next();
					while (xrp.getDepth() >= currentDepth 
							&& eventType != XmlResourceParser.START_TAG){
						eventType = xrp.next();
					}
					if (chars != null){
						return new KeyTable(chars);
					}
					return new KeyTable();
				}
//				for(int i=0; i<xrp.getAttributeCount(); i++){
//					System.out.println(xrp.getAttributeName(i)+"="+xrp.getAttributeValue(i));
//					String attrname = xrp.getAttributeName(i);
//				}
			} else if(eventType == XmlResourceParser.END_TAG) {
//        		System.out.println("End tag "+xrp.getName()+", "+xrp.getDepth());
        	}
			eventType = xrp.next();
        	
        } catch (XmlPullParserException e) {
        	System.out.println("XmlPullParserExeption");
        } catch (IOException e) {
			System.out.println("IOException");
		}
		return null;	// this should not happen
    }
 
    public KantyokuKeyboard(Context context, int layoutTemplateResId, 
            CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);

    }
    
    public boolean handleDelete(){
    	if (this.currentTable == this.table){
    		return true;
    	}else{
    		this.currentTable = this.table;
    		updateKeytops();
    		return false;
    	}
    }
    
    // returns necessity to update keytops
    public void handleKanaKey(){
    	if (this.katakanaState){ // katakana mode
    		this.katakanaState = false;
    		this.mKanaKey.label = "かな";
    	} else {	// hiragana mode
    		this.katakanaState = true;
    		this.mKanaKey.label = "カナ";
    	}
//		this.currentTable = this.table;
		updateKeytops();
    }
    
    public void updateKeytops(){
    	if (this.currentTable == null || this.currentTable.lastFlag || this.currentTable.table == null){
    		return;
    	}
    	for (int i=0; i<31; i++){
//    		System.out.println(this.currentTable.table[i]);
    		if(this.currentTable.table[i].lastFlag){
    			if (this.currentTable.table[i].charachter != null){
    				if (this.katakanaState){
    					this.mAlphabetKeys[i].label = katakanize(this.currentTable.table[i].charachter); 
    				} else {
    					this.mAlphabetKeys[i].label = this.currentTable.table[i].charachter;
    				}
    			}else{
    				this.mAlphabetKeys[i].label = "";
    			}
    		}else if(this.currentTable.table[i] == null){
    			this.mAlphabetKeys[i].label = "";
    		}
    		else if(this.currentTable.table[i].keyTop != null){
    			if (this.katakanaState){
    				this.mAlphabetKeys[i].label = katakanize(this.currentTable.table[i].keyTop); 
    			} else {
    				this.mAlphabetKeys[i].label = this.currentTable.table[i].keyTop;
    			}
    		}
    	}
    }
    
    // to convert hiragana to katakana
    public String katakanize(String str){
    	if (str == null) return null;
    	StringBuffer sb = new StringBuffer(str);
        for (int i = 0; i < sb.length(); i++) {
        	char c = sb.charAt(i);
        	if ((c >= 'ぁ' && c <= 'ゖ') || c == 'ゝ' || c == 'ゞ') {
        		sb.setCharAt(i, (char)(c - 'ぁ' + 'ァ'));
        	}
        }
        return sb.toString();
    }
    
    public String handleKey(int index){
    	if (this.currentTable == null){
    		this.currentTable = this.table;
    		updateKeytops();
    		return null;
    	}
    	this.currentTable = this.currentTable.table[index];
    	
//    	System.out.println(this.currentTable);
    	if (this.currentTable == null){
    		System.out.println("null");
	    	this.currentTable = this.table;
	    	updateKeytops();
    		return null;
    	} else if (this.currentTable.lastFlag){
    		String retStr = this.currentTable.charachter;
	    	this.currentTable = this.table;
	    	updateKeytops();
	    	if (this.katakanaState){
	    		return katakanize(retStr);
	    	} else {
	    		return retStr;
	    	}
    	} else {
    		updateKeytops();
    		return null;
    	}
    }
    
    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }
        
        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                mEnterKey.label = null;
                break;
        }
        
    }

    void setSpaceIcon(final Drawable icon) {
        if (mSpaceKey != null) {
            mSpaceKey.icon = icon;
        }
    }

}
