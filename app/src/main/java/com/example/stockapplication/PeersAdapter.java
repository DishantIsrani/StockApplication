package com.example.stockapplication;


import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    private List<String> peersList;

    public PeersAdapter(List<String> peersList) {
        this.peersList = peersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String peer = peersList.get(position);
        SpannableString spannable = new SpannableString(peer);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(widget.getContext(), SecondActivity.class); // Replace NewActivity with the name of the activity you want to open
                intent.putExtra("query", peer);
                widget.getContext().startActivity(intent);
            }
        };
        spannable.setSpan(clickableSpan, 0, peer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.peerTextView.setText(spannable);
        holder.peerTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public int getItemCount() {
        return peersList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView peerTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            peerTextView = itemView.findViewById(R.id.peersdata);
        }
    }
}
