package cn.yaman.adapter;


import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import cn.yaman.entity.SchemeDetailEntity;

public class NurseProcessFramentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<SchemeDetailEntity> listData;

    public NurseProcessFramentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> fragments, List<SchemeDetailEntity> datas) {
        fragmentList = fragments;
        listData = datas;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

}
