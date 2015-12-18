// Generated code from Butter Knife. Do not modify!
package cn.onlysoft.xmultithreaddownload.demo.ui.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ListViewRecordsAdapter$ViewHolder$$ViewBinder<T extends cn.onlysoft.xmultithreaddownload.demo.ui.adapter.ListViewRecordsAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492944, "field 'ivIcon'");
    target.ivIcon = finder.castView(view, 2131492944, "field 'ivIcon'");
    view = finder.findRequiredView(source, 2131492945, "field 'tvName'");
    target.tvName = finder.castView(view, 2131492945, "field 'tvName'");
    view = finder.findRequiredView(source, 2131492946, "field 'btnDownload'");
    target.btnDownload = finder.castView(view, 2131492946, "field 'btnDownload'");
    view = finder.findRequiredView(source, 2131492954, "field 'tvDownloadPerSize'");
    target.tvDownloadPerSize = finder.castView(view, 2131492954, "field 'tvDownloadPerSize'");
    view = finder.findRequiredView(source, 2131492955, "field 'tvStatus'");
    target.tvStatus = finder.castView(view, 2131492955, "field 'tvStatus'");
    view = finder.findRequiredView(source, 2131492947, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131492947, "field 'progressBar'");
  }

  @Override public void unbind(T target) {
    target.ivIcon = null;
    target.tvName = null;
    target.btnDownload = null;
    target.tvDownloadPerSize = null;
    target.tvStatus = null;
    target.progressBar = null;
  }
}
