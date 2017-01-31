package shiprocket.com.ashu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

/**
 * Created by apple on 31/01/17.
 */

public class ScoreAdapter extends  RecyclerView.Adapter<ScoreAdapter.ViewHolder>{

    private Context context;
    private List<Score> scores;
    private int lastPosition = -1;


    public ScoreAdapter(Context context,List<Score> scores){
        this.context = context;
        this.scores = scores;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;
        context = parent.getContext();
        // view = LayoutInflater.from(context).inflate(R.layout.alerts_item_grid, parent, false);
        view = LayoutInflater.from(context).inflate(R.layout.score_item, parent, false);

        viewHolder = new ViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.txtName.setText(scores.get(position).getName()+ " : ");
        holder.txtScore.setText(scores.get(position).getScore());

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private TextView txtScore;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.name);
            txtScore = (TextView) itemView.findViewById(R.id.score);
        }

    }
}
