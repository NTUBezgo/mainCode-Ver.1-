package com.ezgo.index;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MilestoneFragment extends Fragment {

    private View view;
    private SimpleAdapter adapter;
    private ListView listView;

    public MilestoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_milestone, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        // 清單面版
        adapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.milestone_content,
                new String[]{"title", "info","img"},
                new int[]{R.id.title, R.id.info,R.id.img});
        listView.setAdapter(adapter);

        return view;
    }

    private List getData() {
        ArrayList list = new ArrayList();
        Map
        map = new HashMap();map.put("title", "獅子的拜訪");map.put("info", "看過10種動物"); map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "猴王愛散步");map.put("info", "走了3公里"); map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "象爺爺的故事");map.put("info", "看過大象的介紹");map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "貓頭鷹的回憶");map.put("info", "答對3題關於貓頭鷹的問題");map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "馬來貘出遠門");map.put("info", "走了5公里");map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "兔兔感冒了");map.put("info", "幫兔兔找感冒藥");map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "小熊找媽媽");map.put("info", "走到台灣黑熊動物區");map.put("img", R.drawable.lock); list.add(map);
        map = new HashMap();map.put("title", "企鵝住哪兒");map.put("info", "走到企鵝館"); map.put("img", R.drawable.lock); list.add(map);

        return list;
    }

}
