package app.robo.fhv.roboapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.domain.Score;

/**
 * Created by Kevin on 15.01.2016.
 */
public class ScoreArrayAdapter extends ArrayAdapter<Score> {

    private final Context context;
    private final Score[] scores;

    public ScoreArrayAdapter(Context context, Score[] scores) {
        super(context, R.layout.highscores_row_layout, scores);

        this.context = context;
        this.scores = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.highscores_row_layout, parent, false);

        TextView rankView = (TextView) rowView.findViewById(R.id.highScores_rank);
        TextView nameView = (TextView) rowView.findViewById(R.id.highScores_name);
        TextView durationView = (TextView) rowView.findViewById(R.id.highScores_duration);

        rankView.setText((position + 1) + ".");
        nameView.setText(scores[position].getName());
        durationView.setText(scores[position].getDuration());

        return rowView;
    }
}
