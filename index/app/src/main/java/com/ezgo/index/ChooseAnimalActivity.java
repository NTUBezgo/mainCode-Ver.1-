package com.ezgo.index;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.ezgo.index.Adapter.MyPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChooseAnimalActivity extends AppCompatActivity {

    static ViewPager viewPager;
    MyPageAdapter myPageAdapter;
    static Context context;
    RadioGroup mRadio;

    private static final int[] pictures = {R.drawable.elephant,
            R.drawable.formosablackbear, R.drawable.giraffe,
            R.drawable.panda, R.drawable.penguin}; //動物圖庫

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_animal);

        context = this;

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //---------
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mRadio = (RadioGroup) findViewById(R.id.page_group) ;

        List<Integer> list = new ArrayList<Integer>();      //滑動頁面清單的陣列
        for (int i : pictures) {            //將pitctures的內容加到頁面上
            list.add(i);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mRadio.check(R.id.page1);
                        break;
                    case 1:
                        mRadio.check(R.id.page2);
                        break;
                    case 2:
                        mRadio.check(R.id.page3);
                        break;
                    case 3:
                        mRadio.check(R.id.page4);
                        break;
                    case 4:
                        mRadio.check(R.id.page5);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        myPageAdapter = new MyPageAdapter(this, list); //將資料傳入Adapter
        viewPager.setAdapter(myPageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void arStart(View view){
        Intent intent=new Intent();
        intent.setClass(context,ArActivity.class);
        startActivity(intent);
        ChooseAnimalActivity.this.finish();
    }
}
