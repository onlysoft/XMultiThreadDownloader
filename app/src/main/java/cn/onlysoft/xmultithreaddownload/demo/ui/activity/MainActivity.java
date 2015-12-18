package cn.onlysoft.xmultithreaddownload.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.onlysoft.xmultithreaddownload.demo.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnListViewWithDownloadInfos).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AppListActivity.class);
        switch (v.getId()) {
            case R.id.btnListViewWithDownloadInfos:
                intent.putExtra("EXTRA_TYPE", AppListActivity.TYPE.TYPE_LISTVIEW_DOWNLOADINFO);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

}
