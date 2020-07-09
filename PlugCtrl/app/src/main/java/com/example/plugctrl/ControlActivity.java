package com.example.plugctrl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class ControlActivity extends AppCompatActivity {


    HTTPActivity httpObj;
    Intent j;
    String IP = null;
    String httpHeader = "http://";
    String request = null;
    String receivedMessage = null;
    boolean button_state = false;
    ThreadReceive threadReceive = null;
    ArrayList<String> plug_list = new ArrayList<String>();
    private RecyclerView recview,recview2;
    private RecyclerView.Adapter mAdapter,mAdapter2;
    private RecyclerView.LayoutManager layoutManager , layoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_list);


        j = getIntent();
        IP = j.getStringExtra("IP");
        System.out.println("IP domestic:"+IP);
        httpObj = new HTTPActivity();
        threadReceive = new ThreadReceive(httpObj);
        recview = findViewById(R.id.recview);
        recview.setHasFixedSize(true);

        recview2 = findViewById(R.id.recview2);
        recview2.setHasFixedSize(true);

        plug_list.add("MyPlug1");
        plug_list.add("MyPlug2");
        plug_list.add("MyPlug3");
        mAdapter = new MyAdapter(plug_list , IP);
        recview.setAdapter(mAdapter);

        mAdapter2 = new MyOnOffAdapter(plug_list,IP);
        recview2.setAdapter(mAdapter2);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recview.setLayoutManager(llm);
        recview.addItemDecoration(new VerticalSpacingDecoration(35));

        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recview2.setLayoutManager(llm2);
        recview2.addItemDecoration(new VerticalSpacingDecoration(35));


    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public DividerItemDecoration(Drawable divider) {
            this.mDivider = divider;
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
    }

    public class VerticalSpacingDecoration extends RecyclerView.ItemDecoration {

        private int spacing;

        public VerticalSpacingDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = spacing;
        }
    }


}
