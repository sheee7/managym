package gym.managym;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NoticeListView {
    String title;
    String name;
    String date;
    String content;

    public NoticeListView(String title, String name, String date, String content) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class NoticeListAdapter extends BaseAdapter {
    private Context context;
    private List<NoticeListView> noticeList;

    public NoticeListAdapter(Context context, List<NoticeListView> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    @Override
    public Object getItem(int i) {
        return noticeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_notice_listview, null);
        TextView titleText = v.findViewById(R.id.noticeTitleText);
        TextView nameText = v.findViewById(R.id.noticeNameText);
        TextView dateText = v.findViewById(R.id.noticeDateText);

        titleText.setText(noticeList.get(i).getTitle());
        nameText.setText(noticeList.get(i).getName());
        dateText.setText(noticeList.get(i).getDate());

        v.setTag(noticeList.get(i).getTitle());
        return v;
    }
}