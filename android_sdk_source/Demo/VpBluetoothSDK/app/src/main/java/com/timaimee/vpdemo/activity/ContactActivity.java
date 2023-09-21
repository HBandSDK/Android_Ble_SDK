package com.timaimee.vpdemo.activity;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.ContactAdapter;
import com.timaimee.vpdemo.adapter.DividerItemDecoration;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IContactOptListener;
import com.veepoo.protocol.model.datas.Contact;
import com.veepoo.protocol.model.enums.EContactOpt;
import com.veepoo.protocol.shareprence.VpSpGetUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tech.gujin.toast.ToastUtil;

public class ContactActivity extends AppCompatActivity implements IContactOptListener, OnRecycleViewClickCallback {

    private static final String TAG = "ContactActivity";

    private ContactAdapter mAdapter;

    private RecyclerView rvContact;
    private List<Contact> mListData = new ArrayList<>();

    private ImageView ivEmpty;

    private int contactType = 1;
    private boolean isSupportSOSContact = false;

    private boolean isHasRead = false;

    private final OperaterActivity.WriteResponse response = new OperaterActivity.WriteResponse();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactType = VpSpGetUtil.getVpSpVariInstance(this).getContactType();
        boolean isSupportSOSContact = VpSpGetUtil.getVpSpVariInstance(this).isSupportSOSContactFunction();
        setContentView(R.layout.activity_contact);
        initView();
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListData != null && mListData.size() >= 10) {
                    showMsg("联系人已超过十个。（已满）");
                    return;
                }
                Intent intent = new Intent(ContactActivity.this, AddContactActivity.class);
                intent.putExtra("_isAdd", true);
                startActivityForResult(intent, 15);
            }
        });
        findViewById(R.id.btnSOSSettings).setVisibility(contactType == 2 ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnSOSSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, AddContactActivity.class);
                intent.putExtra("_isAdd", false);
                startActivityForResult(intent, 16);
            }
        });

        findViewById(R.id.btnAddAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isHasRead) {
                    ToastUtil.show("请先读取更新联系人");
                    return;
                }
                int canAddSize = 10 - mListData.size();
                int idValue = mListData.size() + 1;
                if (canAddSize > 0) {
                    List<Contact> canAddList = new ArrayList<>();
                    for (int i = 0; i < canAddSize; i++) {
                        Contact contact = new Contact(idValue + i, "批量添加" + i, "110" + i, false, contactType == 2);
                        canAddList.add(contact);
                    }
                    VPOperateManager.getInstance().addContactList(canAddList, ContactActivity.this, response);
                } else {
                    ToastUtil.show("联系人已加满");
                }
            }
        });

        findViewById(R.id.tvRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("刷新");
                VPOperateManager.getInstance().readContact(-1, ContactActivity.this, response);
            }
        });
        findViewById(R.id.tvDeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListData.isEmpty()) {
                    ToastUtil.show("列表为空");
                    return;
                }
                ToastUtil.show("删除全部");
                VPOperateManager.getInstance().deleteContactList(mListData, ContactActivity.this, response);
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
        rvContact = findViewById(R.id.rvContact);
        rvContact = (RecyclerView) findViewById(R.id.rvContact);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactAdapter(mListData, this, contactType);
        rvContact.setAdapter(mAdapter);
        DividerItemDecoration decor = new DividerItemDecoration(ContactActivity.this, DividerItemDecoration.VERTICAL_LIST, true);
        rvContact.addItemDecoration(decor);
        helper.attachToRecyclerView(rvContact);
    }

    private void initListData() {
        VPOperateManager.getInstance().readContact(-1, this, response);
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
            Collections.swap(mListData, adapterPosition, targetAdapterPosition);
            for (int i = 0; i < mListData.size(); i++) {
                mListData.get(i).setContactID(i + 1);
            }
            mAdapter.notifyItemMoved(adapterPosition, targetAdapterPosition);
            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ACTION_STATE_IDLE) {
//                ContactHandler.Companion.getInstance().moveContactList(mFirstAdapterPosition + 1, mLastAdapterPosition + 1);
//                saveContact();
                int fromID = mFirstAdapterPosition + 1;
                int toID = mLastAdapterPosition + 1;
                Contact fromContact = getContactByID(fromID);
                Contact toContact = getContactByID(toID);
                if (fromContact != null && toContact != null) {
                    VPOperateManager.getInstance().moveContact(fromContact, toContact, ContactActivity.this, response);
                    mFirstAdapterPosition = -1;
                    mLastAdapterPosition = -1;
                }
            }
        }
    });

    @Override
    public void onContactOptSuccess(@NotNull EContactOpt opt, int crc) {
        Logger.t(TAG).e("联系人操作成功：" + opt + " crc = " + crc);
        showMsg("联系人操作成功：" + opt + " crc = " + crc);
//        VPOperateManager.getInstance().readContact(-1, this, response);
    }

    @Override
    public void onContactOptFailed(@NotNull EContactOpt opt) {
        Logger.t(TAG).e("联系人操作失败：" + opt);
//        VPOperateManager.getInstance().readContact(-1, this, response);
    }

    @Override
    public void onContactReadSuccess(@NotNull List<Contact> contactList) {
        Logger.t(TAG).e("读取联系人成功：" + contactList.size());
        isHasRead = true;
        if (mListData == null) {
            mListData = new ArrayList<>();
        }
        for (Contact contact : contactList) {
            Logger.t(TAG).d("读取联系人：" + contact.toString());
        }
        mListData.clear();
        mListData.addAll(contactList);
        if (mListData.isEmpty()) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onContactReadASSameCRC() {
        isHasRead = true;
        Logger.t(TAG).e("读取联系人，但CRC值一致，设备端和app端联系人列表一致(包括设备端没有数据)，无需重复读取");
        showMsg("读取联系人，但CRC值一致，设备端和app端联系人列表一致，无需重复读取");
    }

    @Override
    public void onContactReadFailed() {
        Logger.t(TAG).e("读取联系失败");
        showMsg("读取联系失败");
    }

    @Override
    public void OnRecycleViewClick(int position) {
        Logger.t(TAG).i("OnRecycleViewClick position delete=" + position);
        int realPosition = 0x0F & position;
        Contact contact = mListData.get(realPosition);
        if (position >= 0x10) {
            Logger.t(TAG).e("----" + contact);
            contact.setSettingSOS(!contact.isSettingSOS());
            contact.setSupportSOS(true);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "点击了第" + realPosition + "行SOS ->" + contact, Toast.LENGTH_SHORT).show();
            VPOperateManager.getInstance().setContactSOSState(contact.isSettingSOS(), contact, this, response);
            //saveContact();
        } else {
            mListData.remove(position);
            //saveContact();
            mAdapter.notifyDataSetChanged();
//            ContactHandler.Companion.getInstance().deleteContactList(position + 1);
            /**
             * 删除联系人建议删除的时候 弹出加载框，成功或失败的时候再关闭加载框。防止多次快速去点击删除 导致删除失败
             */
            ToastUtil.show("删除联系人建议删除的时候 弹出加载框，成功或失败的时候再关闭加载框。防止多次快速去点击删除 导致删除失败。 删除id = " + contact.getContactID());
            VPOperateManager.getInstance().deleteContact(contact, this, response);
        }
    }

    private Contact getContactByID(int id) {
        if (mListData == null || mListData.isEmpty()) return null;
        for (Contact contact : mListData) {
            if (contact.getContactID() == id) {
                return contact;
            }
        }
        return mListData.get(0);
    }

    public void showMsg(String msg) {
        runOnUiThread(() -> Toast.makeText(ContactActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("-onActivityResult-  requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == 15 && resultCode == 1115) {
            String name = data.getStringExtra("name");
            String phoneNumber = data.getStringExtra("phoneNumber");
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)) {
                showMsg("联系信息不能为空");
                return;
            }
            int idValue = mListData.size() + 1;
            Logger.t(TAG).d("-onActivityResult-  name = " + name + " phoneNumber = " + phoneNumber);
            Contact contact = new Contact(idValue, name, phoneNumber, false, contactType == 2);
            mListData.add(contact);
            mAdapter.notifyDataSetChanged();
            VPOperateManager.getInstance().addContact(contact, this, response);
        }
    }
}
