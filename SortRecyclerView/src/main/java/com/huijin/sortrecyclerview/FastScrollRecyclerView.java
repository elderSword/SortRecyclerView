package com.huijin.sortrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by flaviusmester on 23/02/15.
 */
public class FastScrollRecyclerView extends RecyclerView {
    private Context ctx;

    private boolean setupThings = false;
    public static int indWidth = 25;
    public static int indHeight= 18;
    public float scaledWidth;
    public float scaledHeight;
    public String[] sections;
    public float sx;
    public float sy;
    public String section;
    public boolean showLetter = false;
    private Handler listHandler;
    private TextView letterDialog;
    private boolean isEnable;


    public FastScrollRecyclerView(Context context) {
        super(context);
        ctx = context;
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

    /**
     * 为滑动显示的中间textview
     * @param letter
     */
    public void setTextView(TextView letter){
        this.letterDialog = letter;
    }

    /**
     * 启用side的开关
     * @param isEnable
     */
    public void setEnableSide(boolean isEnable){
        this.isEnable = isEnable;
    }
    @Override
    public void onDraw(Canvas c) {
        if(!setupThings)
            setupThings();
        super.onDraw(c);
    }

    private void setupThings() {
        //create az text data
        Set<String> sectionSet = ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().keySet();
        ArrayList<String> listSection = new ArrayList<>(sectionSet);
//        Collections.sort(listSection);
        sections = new String[listSection.size()];
        int i=0;
        for(String s:listSection) {
            sections[i++] = s;
        }

        scaledWidth = indWidth * ctx.getResources().getDisplayMetrics().density;
        scaledHeight= indHeight* ctx.getResources().getDisplayMetrics().density;
        sx = this.getWidth() - this.getPaddingRight() - (float)(0.8*scaledWidth);
        sy = (float)((this.getHeight() - (scaledHeight * sections.length) )/2.0);
        setupThings = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnable){
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (x < sx - scaledWidth || y < sy || y > (sy + scaledHeight*sections.length))
                    return super.onTouchEvent(event);
                else {
                    // We touched the index bar
                    float yy = y - this.getPaddingTop() - getPaddingBottom() - sy;
                    int currentPosition = (int) Math.floor(yy / scaledHeight);
                    if(currentPosition<0)currentPosition=0;
                    if(currentPosition>=sections.length)currentPosition=sections.length-1;
                    section = sections[currentPosition];
                    if (letterDialog != null) {
                        letterDialog.setText(section);
                        letterDialog.setVisibility(View.VISIBLE);
                    }
                    showLetter = true;
                    int positionInData = 0;
                    if( ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().containsKey(section.toUpperCase()) )
                        positionInData = ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().get(section.toUpperCase());
                    this.scrollToPosition(positionInData);
                    FastScrollRecyclerView.this.invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                if (!showLetter && (x < sx  - scaledWidth || y < sy || y > (sy + scaledHeight*sections.length)))
                    return super.onTouchEvent(event);
                else {
                    float yy = y - sy;
                    int currentPosition = (int) Math.floor(yy / scaledHeight);
                    if(currentPosition<0)currentPosition=0;
                    if(currentPosition>=sections.length)currentPosition=sections.length-1;
                    section = sections[currentPosition];
                    if (letterDialog != null) {
                        letterDialog.setText(section);
                        letterDialog.setVisibility(View.VISIBLE);
                    }
                    showLetter = true;
                    int positionInData = 0;
                    if(((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().containsKey(section.toUpperCase()) )
                        positionInData = ((FastScrollRecyclerViewInterface)getAdapter()).getMapIndex().get(section.toUpperCase());
                    this.scrollToPosition(positionInData);
                    FastScrollRecyclerView.this.invalidate();

                }
                break;

            }
            case MotionEvent.ACTION_UP: {
                listHandler = new ListHandler();
                listHandler.sendEmptyMessageDelayed(0, 100);
                if (letterDialog != null) {
                    letterDialog.setVisibility(View.INVISIBLE);
                }
                if (x < sx - scaledWidth || y < sy || y > (sy + scaledHeight*sections.length))
                    return super.onTouchEvent(event);
                else
                    return true;
//                break;
            }
        }
        return true;
    }

    private class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLetter = false;
            FastScrollRecyclerView.this.invalidate();
        }


    }
}
