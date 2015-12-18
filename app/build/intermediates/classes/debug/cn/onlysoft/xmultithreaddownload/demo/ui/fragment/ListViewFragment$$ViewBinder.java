// Generated code from Butter Knife. Do not modify!
package cn.onlysoft.xmultithreaddownload.demo.ui.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ListViewFragment$$ViewBinder<T extends cn.onlysoft.xmultithreaddownload.demo.ui.fragment.ListViewFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492950, "field 'listView'");
    target.listView = finder.castView(view, 2131492950, "field 'listView'");
    view = finder.findRequiredView(source, 2131492949, "field 'btn_add'");
    target.btn_add = finder.castView(view, 2131492949, "field 'btn_add'");
  }

  @Override public void unbind(T target) {
    target.listView = null;
    target.btn_add = null;
  }
}
