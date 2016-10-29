package com.platformhouse.lifepage.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.platformhouse.lifepage.R;
import com.platformhouse.lifepage.data.note.NoteColumnHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
 * This Class responsible on converting the array of items to ListView
 */
public class NoteAdapter extends BaseAdapter {
    Context context;
    ArrayList<NoteColumnHolder> noteList = new ArrayList<>();
    LayoutInflater inflater;
    Holder holder;
    int j = R.mipmap.alarm_clock;

    public NoteAdapter(ArrayList<Object> list, Context context) {
        this.context = context;
        if (list != null){
            for (int i = 0; i < list.size(); i++) {
                noteList.add((NoteColumnHolder) list.get(i));
            }
        }
    }

    @Override
    public int getCount() {
        return noteList.size() == 0 ? 0 : noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.note_item_list, parent, false);
            holder = new Holder();
            holder.noteImage = (ImageView) itemView.findViewById(R.id.note_image);
            holder.noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            holder.noteContent = (TextView) itemView.findViewById(R.id.note_content);
            holder.noteTime = (TextView) itemView.findViewById(R.id.time);
            holder.noteCalender = (TextView) itemView.findViewById(R.id.calendar);
            holder.noteClockHolder = (ImageView) itemView.findViewById(R.id.alarm_clock);
            holder.noteDateHolder = (ImageView) itemView.findViewById(R.id.alarm_calender);
            itemView.setTag(holder);
        } else {
            holder = (Holder) itemView.getTag();
        }
        Picasso.with(context)
                .load(Integer.parseInt(noteList.get(position).getAlarm_date_logo()))
                .into(holder.noteDateHolder);
        Picasso.with(context)
                .load(Integer.parseInt(noteList.get(position).getAlarm_clock_logo()))
                .into(holder.noteClockHolder);
        Picasso.with(context)
                .load(noteList.get(position).getImage_path())
                .resize(1380,850)
                .centerCrop()
                .into(holder.noteImage);

        holder.noteTitle.setText(noteList.get(position).getTitle());
        holder.noteContent.setText(noteList.get(position).getBody());
        holder.noteTime.setText(noteList.get(position).getTime());
        holder.noteCalender.setText(noteList.get(position).getDate());

        return itemView;
    }

    private class Holder {
        ImageView noteImage;
        TextView noteTitle;
        TextView noteContent;
        TextView noteTime;
        TextView noteCalender;
        ImageView noteClockHolder;
        ImageView noteDateHolder;

    }
}
