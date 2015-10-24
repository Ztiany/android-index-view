package com.ztiany.IndexView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {


    private ListView listView;
    private IndexView indexView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listview);
        indexView = (IndexView) findViewById(R.id.indexView);
        ArrayList<String> mItems = new ArrayList<String>();
        mItems.add("ABS Diary of a Wimpy Kid 6: Cabin Fever");
        mItems.add("ABS Steve Jobs");
        mItems.add("ABS Inheritance (The Inheritance Cycle)");
        mItems.add("ABS 11/22/63: A Novel");
        mItems.add("BS The Hunger Games");
        mItems.add("BS The LEGO Ideas Book");
        mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
        mItems.add("Steve Jobs");
        mItems.add("Inheritance (The Inheritance Cycle)");
        mItems.add("11/22/63: A Novel");
        mItems.add("The Hunger Games");
        mItems.add("The LEGO Ideas Book");
        mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
        mItems.add("Catching Fire (The Second Book of the Hunger Games)");
        mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
        mItems.add("Death Comes to Pemberley");
        mItems.add("Diary of a Wimpy Kid 6: Cabin Fever");
        mItems.add("Steve Jobs");
        mItems.add("Inheritance (The Inheritance Cycle)");
        mItems.add("11/22/63: A Novel");
        mItems.add("The Hunger Games");
        mItems.add("The LEGO Ideas Book");
        mItems.add("Explosive Eighteen: A Stephanie Plum Novel");
        mItems.add("Catching Fire (The Second Book of the Hunger Games)");
        mItems.add("Elder Scrolls V: Skyrim: Prima Official Game Guide");
        mItems.add("XDeath Comes to Pemberley");
        mItems.add("ZThe Hunger Games");
        mItems.add("FThe LEGO Ideas Book");
        mItems.add("JExplosive Eighteen: A Stephanie Plum Novel");
        mItems.add("Catching Fire (The Second Book of the Hunger Games)");
        mItems.add("TElder Scrolls V: Skyrim: Prima Official Game Guide");
        mItems.add("YDeath Comes to Pemberley");
        Collections.sort(mItems);

        ListAdapter listAdapter = new ListAdapter(mItems);
        listView.setAdapter(listAdapter);
        indexView.setAdapter(listAdapter);
    }







    public class ListAdapter extends BaseAdapter implements SectionIndexer{

        private String mSections = "#ABCDEFGHIJHLMNOPQRSTUVWXYZ";

        private List<String> mDatas;

        ListAdapter(List<String> datas){
            this.mDatas = datas;
        }

        @Override
        public int getCount() {
            return mDatas==null?0:mDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                TextView textView = new TextView(getApplicationContext());
                textView.setPadding(20,20,20,20);
                textView.setTextColor(Color.BLUE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
                view = textView;
            }
            TextView textView = (TextView) view;
            textView.setText(mDatas.get(i));
            return view;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            char[] chars = mSections.toCharArray();
            int index = 0;
            for (char c : chars) {
                sections[index++] = String.valueOf(c);
            }
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < mDatas.size(); i++) {
               if(mSections.charAt(section) == mDatas.get(i).charAt(0)){
                   listView.setSelection(i);
               }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int i) {
            return 0;
        }
    }


}
