package com.timaimee.vpdemo.activity;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.DividerItemDecoration;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.timaimee.vpdemo.adapter.WorldClockAdapter;
import com.timaimee.vpdemo.bean.TimeZoneBean;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IWorldClockOptListener;
import com.veepoo.protocol.model.datas.WorldClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tech.gujin.toast.ToastUtil;

/**
 * Description 世界时钟界面
 *
 * @author KYM.
 * @date 2024/4/10 16:05
 */
public class WorldClockActivity extends AppCompatActivity implements IWorldClockOptListener, OnRecycleViewClickCallback {

    private static final String TAG = "WorldClockActivity";

    private WorldClockAdapter mAdapter;

    private RecyclerView rvClock;
    private List<WorldClock> mListData = new ArrayList<>();

    private ImageView ivEmpty;


    private boolean isHasRead = false;
    private boolean isDeleteAll = false;
    private int delCount = 0;

    private final OperaterActivity.WriteResponse response = new OperaterActivity.WriteResponse();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_clock);
        initView();
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListData != null && mListData.size() >= 10) {
                    showMsg("联系人已超过十个。（已满）");
                    return;
                }
                Intent intent = new Intent(WorldClockActivity.this, AddClockActivity.class);
                startActivityForResult(intent, 15);
            }
        });


        findViewById(R.id.tvRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("刷新");
                VPOperateManager.getInstance().readWorldClock(-1, response, WorldClockActivity.this);
            }
        });
        findViewById(R.id.tvDeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListData.isEmpty()) {
                    ToastUtil.show("列表为空");
                    return;
                }
                isDeleteAll = true;
                ToastUtil.show("删除全部");
                VPOperateManager.getInstance().deleteWorldClocks(mListData, response, WorldClockActivity.this);
            }
        });
        initListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        ivEmpty = findViewById(R.id.ivEmpty);
        rvClock = findViewById(R.id.rvContact);
        rvClock = (RecyclerView) findViewById(R.id.rvClock);
        rvClock.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WorldClockAdapter(mListData, this);
        rvClock.setAdapter(mAdapter);
        DividerItemDecoration decor = new DividerItemDecoration(WorldClockActivity.this, DividerItemDecoration.VERTICAL_LIST, true);
        rvClock.addItemDecoration(decor);
        helper.attachToRecyclerView(rvClock);
    }

    private void initListData() {
        VPOperateManager.getInstance().readWorldClock(-1, response, this);
    }

    int mFirstAdapterPosition = -1, mLastAdapterPosition = -1;

    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //首先回调的方法 返回int表示是否监听该方向
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
            //int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //侧滑事件
            mListData.remove(viewHolder.getAdapterPosition());
            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //滑动事件
            int adapterPosition = viewHolder.getAdapterPosition();
            int targetAdapterPosition = target.getAdapterPosition();
            if (mFirstAdapterPosition == -1) {
                mFirstAdapterPosition = adapterPosition;
            }
            mLastAdapterPosition = targetAdapterPosition;
            Logger.t(TAG + "-[MOVE]").i("onMove:adapterPosition=" + adapterPosition + ",targetAdapterPosition=" + targetAdapterPosition);
            Collections.swap(mListData, adapterPosition, targetAdapterPosition);
            WorldClock from = mListData.get(adapterPosition);
            WorldClock to = mListData.get(targetAdapterPosition);
            Logger.t(TAG + "-[MOVE]").i("from => " + adapterPosition + "-" + from.toString());
            Logger.t(TAG + "-[MOVE]").i("to => " + targetAdapterPosition + "-" + to.toString());
            VPOperateManager.getInstance().moveWorldClockPosition(adapterPosition + 1, targetAdapterPosition + 1, response, WorldClockActivity.this);
            mAdapter.notifyItemMoved(adapterPosition, targetAdapterPosition);
            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ACTION_STATE_IDLE) {
//                ContactHandler.Companion.getInstance().moveContactList(mFirstAdapterPosition + 1, mLastAdapterPosition + 1);
//                saveContact();
//                int fromID = mFirstAdapterPosition + 1;
//                int toID = mLastAdapterPosition + 1;
//                WorldClock fromWorldClock = getWorldClockByID(fromID);
//                WorldClock toWorldClock = getWorldClockByID(toID);
                mFirstAdapterPosition = -1;
                mLastAdapterPosition = -1;
            }
        }
    });

    private WorldClock getWorldClockByID(int id) {
        if (mListData == null || mListData.isEmpty()) {
            return null;
        }
        for (WorldClock clock : mListData) {
            if (clock.getId() == id) {
                return clock;
            }
        }
        return mListData.get(0);
    }


    @Override
    public void OnRecycleViewClick(int position) {
        isDeleteAll = false;
        Logger.t(TAG).i("OnRecycleViewClick position delete=" + position);
        int realPosition = 0x0F & position;
        WorldClock worldClock = mListData.get(realPosition);
        mListData.remove(position);
        mAdapter.notifyDataSetChanged();
        if (mListData.isEmpty()) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
        ToastUtil.show("删除世界时钟建议删除的时候 弹出加载框，成功或失败的时候再关闭加载框。防止多次快速去点击删除 导致删除失败。 删除id = " + worldClock.getId());
        VPOperateManager.getInstance().deleteWorldClock(worldClock, response, this);
    }

    public void showMsg(String msg) {
        runOnUiThread(() -> Toast.makeText(WorldClockActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("-onActivityResult-  requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == 15 && resultCode == 1115) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                TimeZoneBean timeZoneBean = (TimeZoneBean) extras.getSerializable("TimeZoneBean");
                if (timeZoneBean != null) {
                    boolean isExit = false;
                    for (int i = 0; i < mListData.size(); i++) {
                        if (mListData.get(i).getTimeZoneName().equals(timeZoneBean.getCityName())) {
                            isExit = true;
                            break;
                        }
                    }
                    if (isExit) {
                        showMsg("已存在");
                        return;
                    }

                    WorldClock worldClock = new WorldClock(genNewId(mListData), timeZoneBean.getT15Min(), timeZoneBean.getCityName(), "");
                    mListData.add(worldClock);
                    ivEmpty.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    VPOperateManager.getInstance().addWorldClock(worldClock, response, this);
                }

            }
        }
    }

    /**
     * 生产世界时钟id
     */
    public int genNewId(List<WorldClock> wcList) {
        //1-10
        if (wcList.size() >= 10) {
            return -1;
        }
        for (int i = 1; i < 10; i++) {
            boolean isHave = false;
            for (WorldClock wc : wcList) {
                if (i == wc.getId()) {
                    isHave = true;
                }
            }
            if (!isHave) {
                return i;
            }
        }
        return wcList.size() + 1;
    }

    @Override
    public void onWorldClockReadSuccess(@NonNull List<WorldClock> worldClocks, int crc) {
        isHasRead = true;
        if (mListData == null) {
            mListData = new ArrayList<>();
        }
        for (WorldClock clock : worldClocks) {
            Logger.t(TAG).d("读取世界时钟：" + clock.toString());
        }
        mListData.clear();
        mListData.addAll(worldClocks);
        if (mListData.isEmpty()) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onWorldClockOptSuccess(@NonNull WorldClockOpt opt, int crc) {
        if (opt == WorldClockOpt.DELETE && isDeleteAll) {
            delCount++;
            if (delCount >= mListData.size()) {
                mListData.clear();
                mAdapter.notifyDataSetChanged();
                ivEmpty.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onWorldClockOptFailed(@NonNull WorldClockOpt opt) {

    }
}