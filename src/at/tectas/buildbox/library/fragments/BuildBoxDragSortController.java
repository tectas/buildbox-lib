package at.tectas.buildbox.library.fragments;

import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;

import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.helpers.ViewHelper;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class BuildBoxDragSortController extends DragSortController {
    private DragSortListView dragSortListView = null;
    private BaseAdapter adapter = null;

    public BuildBoxDragSortController(DragSortListView dslv, BaseAdapter adapter) {
        super(dslv);
        setDragHandleId(R.id.download_item_layout);
        dragSortListView = dslv;
        this.adapter = adapter;
    }

    @Override
    public View onCreateFloatView(int position) {
        View v = adapter.getView(position, null, dragSortListView);
        ViewHelper.setAlphaOfView(v.getContext().getResources().getInteger(R.integer.download_float_view_alpha), v);
        return v;
    }

    @Override
    public void onDestroyFloatView(View floatView) {
    	
    }

    @Override
    public int startDragPosition(MotionEvent ev) {
        int res = super.dragHandleHitPosition(ev);
        int width = dragSortListView.getWidth();

        if ((int) ev.getX() < width / 5) {
            return res;
        } else {
            return DragSortController.MISS;
        }
    }
}
