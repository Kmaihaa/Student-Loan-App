package com.example.student_loan_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {
    private List<Resource> resourceList;
    private Context context;

    public ResourceAdapter(List<Resource> resourceList, Context context) {
        this.resourceList = resourceList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button visitButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.resourceTitle);
            description = itemView.findViewById(R.id.resourceDescription);
            visitButton = itemView.findViewById(R.id.resourceVisitButton);
        }
    }

    @Override
    public ResourceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resource_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResourceAdapter.ViewHolder holder, int position) {
        Resource resource = resourceList.get(position);
        holder.title.setText(resource.getTitle());
        holder.description.setText(resource.getDescription());

        holder.visitButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resource.getUrl()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }
}

