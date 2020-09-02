package com.example.a16046512.qrhunt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionAdapter extends ArrayAdapter<Question> {
    private ArrayList<Question> viewquestion;
    private Context context;

    private TextView q,a,b,c,d,h,ans;

    public ViewQuestionAdapter(Context context, int resource,  ArrayList<Question> objects) {
        super(context, resource, objects);
        viewquestion = objects;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.acc_view_game_row, parent, false);

        q = (TextView) rowView.findViewById(R.id.tvViewQuestion);
        a = (TextView) rowView.findViewById(R.id.tvViewA);
        b = (TextView) rowView.findViewById(R.id.tvViewB);
        c = (TextView) rowView.findViewById(R.id.tvViewC);
        d = (TextView) rowView.findViewById(R.id.tvViewD);
        h = (TextView) rowView.findViewById(R.id.tvViewHint);
        ans = (TextView) rowView.findViewById(R.id.tvViewCorrectAnswer);

        Question currentquestion = viewquestion.get(position);


        q.setText("Question: "+currentquestion.getQuestion());
        a.setText("Answer A: "+currentquestion.getAnsa());
        b.setText("Answer B: "+currentquestion.getAnsb());
        c.setText("Answer C: "+currentquestion.getAnsc());
        d.setText("Answer D: "+currentquestion.getAnsd());
        h.setText("Hint: "+currentquestion.getHint());
        ans.setText("Correct Answer: "+currentquestion.getCorrectans());

        return rowView;
    }

}
