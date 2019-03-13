package cn.yaman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author timpkins
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private String[] title;
    private List<Fragment> list;
    private Context context;
    public FragmentAdapter(Context context,@NonNull FragmentManager fm, String[] title,List<Fragment> list) {
        super(fm);
        this.title = title;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_text, null);
        TextView tv= (TextView) view.findViewById(R.id.tv_tab_item);
        tv.setText(title[position]);
        return view;
    }
}
