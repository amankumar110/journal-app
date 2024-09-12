package in.amankumar110.journalapp.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.amankumar110.journalapp.databinding.JournalRowBinding;
import in.amankumar110.journalapp.models.Journal;

public class JournalsAdapter extends RecyclerView.Adapter<JournalsAdapter.JournalViewHolder> {

    private final List<Journal> journalList;
    private final Context context;

    public JournalsAdapter(List<Journal> journalList, Context context) {
        this.journalList = journalList;
        this.context = context;
    }


    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JournalRowBinding binding = JournalRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {

        Journal journal = journalList.get(position);
        holder.binding.journalRowUsername.setText(journal.getUserName());
        holder.binding.journalThoughtList.setText(journal.getThoughts());
        holder.binding.journalTitleList.setText(journal.getTitle());

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(
                journal.getTimeAdded().getSeconds()*1000
        );
        holder.binding.journalTimestampList.setText(timeAgo);

        String imageUrl = journal.getImageUrl();

        if(imageUrl!=null) {
            Glide.with(context)
                    .load(journal.getImageUrl())
                    .into(holder.binding.journalImageList);
        } else {
            holder.binding.journalImageList.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {

        private final JournalRowBinding binding;
        public JournalViewHolder(@NonNull JournalRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
