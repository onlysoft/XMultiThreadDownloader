package cn.onlysoft.xmultithreaddownload.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import cn.onlysoft.xmultithreaddownload.demo.R;
import cn.onlysoft.xmultithreaddownload.demo.ui.fragment.ListViewFragment;

public class AppListActivity extends AppCompatActivity {

    public static final class TYPE {
        public static final int TYPE_LISTVIEW_DOWNLOADINFO=2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        Intent intent = getIntent();
        int type = intent.getIntExtra("EXTRA_TYPE", TYPE.TYPE_LISTVIEW_DOWNLOADINFO);
        if (savedInstanceState == null) {
            Fragment fragment=new ListViewFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        getSupportActionBar().setTitle("ListView With Records");
    }
}
