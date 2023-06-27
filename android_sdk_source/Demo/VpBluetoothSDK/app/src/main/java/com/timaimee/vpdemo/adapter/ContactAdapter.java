package com.timaimee.vpdemo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.model.datas.Contact;

import java.util.List;

/**
 * Created by Administrator on 2018/12/22.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private final List<Contact> list;
    private final OnRecycleViewClickCallback onDragItemClick;
    private final int contactType;

    public ContactAdapter(List<Contact> list, OnRecycleViewClickCallback onDragItemClick, int contactType) {
        this.onDragItemClick = onDragItemClick;
        this.list = list;
        this.contactType = contactType;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactName;
        private final TextView contactPhone;
        private final TextView contactSOS;

        public MyViewHolder(View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.iv_contact_name);
            contactPhone = (TextView) itemView.findViewById(R.id.iv_contact_phone);
            ImageView contactDelete = (ImageView) itemView.findViewById(R.id.iv_contact_delte);
            contactSOS = (TextView) itemView.findViewById(R.id.tvSOS);
            contactSOS.setVisibility(contactType == 2 ? View.VISIBLE : View.GONE);

            contactDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(0x00 | position);
                }
            });

            contactSOS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(0x10 | position);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = list.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhoneNumber());
        holder.contactSOS.setTextColor(holder.itemView.getResources().getColor(contact.isSettingSOS() ? R.color.real_red : R.color.black_30));
        if(contactType == 2) {
            if (!contact.isSettingSOS()) {
                holder.contactSOS.setVisibility(isNeedGoneGreySOSTag() ? View.GONE : View.VISIBLE);
            } else {
                holder.contactSOS.setVisibility(View.VISIBLE);
            }
        } else {
            holder.contactSOS.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private boolean isNeedGoneGreySOSTag() {
        int sosCount = 0;
        for (Contact contact : list) {
            if (contact.isSettingSOS()) {
                sosCount++;
            }
        }
        return contactType == 2 && sosCount >= 5;
    }
}

