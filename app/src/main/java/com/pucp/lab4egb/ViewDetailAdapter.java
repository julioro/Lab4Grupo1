package com.pucp.lab4egb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.lab4egb.entities.Comment;
import com.pucp.lab4egb.entities.Publication;

import java.util.ArrayList;

public class ViewDetailAdapter extends RecyclerView.Adapter<ViewDetailAdapter.ViewDetailViewHolder> {


    private ArrayList<Comment> commentlist;
    Context context;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        // Método para gestionar un click sobre el botón de Ver más
        void onViewMoreClick(int position);
    }

    // Creamos un método al cual le pasaremos un listener desde el Activity
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // clase ViewHolder
    public static class ViewDetailViewHolder extends RecyclerView.ViewHolder{
        public TextView verComment;
        public TextView verUser;
        public TextView verDate;
        public TextView verHour;
        // public ImageView pubImageView;

        public ViewDetailViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            verComment = itemView.findViewById(R.id.verComment);
            verUser = itemView.findViewById(R.id.verUser);
            verDate = itemView.findViewById(R.id.verDate);
            verHour = itemView.findViewById(R.id.verHour);
            // pubImageView = itemView.findViewById(R.id.pubImageView);

            // gestiona el evento de click sobre el botón de info
           /* pubViewMoreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onViewMoreClick(position);
                        }
                    }
                }
            });*/
        }
    }


    public ViewDetailAdapter(ArrayList<Comment> list, Context c){
        this.commentlist = list;
        this.context = c;
    }

    @NonNull
    @Override
    public ViewDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout del item que se repetirá (y guardarlo en item)
        View item = LayoutInflater.from(context).inflate(R.layout.item_rv_comment,parent,false);
        // Creamos el ViewHolder
        ViewDetailViewHolder publicationViewHolder = new ViewDetailViewHolder(item, mListener);
        // Retornamos el ViewHolder
        return publicationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDetailViewHolder holder, int position) {
        Comment p = commentlist.get(position);
        // Obtenemos los parámetros de la publicación:
        String comUser = p.getUser();
        String comDate = p.getDate();
        String commentarios = p.getBody();
        String comHour = p.getHour();

        // Le pasamos los parámetros de la publicación a la vista:
        holder.verComment.setText(commentarios);
        holder.verUser.setText(comUser);
        holder.verDate.setText(comDate);
        holder.verHour.setText(comHour);
       // holder.pubDescriptionTextView.setText(pubDescription);
    }

    @Override
    public int getItemCount() {
        return commentlist.size();
    }


}
