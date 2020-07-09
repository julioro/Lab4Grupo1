package com.pucp.lab4egb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pucp.lab4egb.entities.Publication;

import java.util.ArrayList;

public class ListPublicationsAdapter extends RecyclerView.Adapter<ListPublicationsAdapter.PublicationViewHolder> {

    private ArrayList<Publication> publicationsList;
    Context context;
    private OnItemClickListener mListener;


    //PARA STORAGE --------------------------------------------
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // -------------------------------------------- PARA STORAGE


    // Creamos una interfaz con un método para pasar la posición del item al que hemos hecho click
    public interface OnItemClickListener {
        // Método para gestionar un click sobre el botón de Ver más
        void onViewMoreClick(int position);
    }

    // Creamos un método al cual le pasaremos un listener desde el Activity
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // clase ViewHolder
    public static class PublicationViewHolder extends RecyclerView.ViewHolder{
        public TextView pubUserTextView;
        public TextView pubDateTextView;
        public TextView pubCommentsTextView;
        public TextView pubDescriptionTextView;
        public TextView pubViewMoreTextView;
        public ImageView pubImageView;

        public PublicationViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            pubUserTextView = itemView.findViewById(R.id.pubUserTextView);
            pubDateTextView = itemView.findViewById(R.id.pubDateTextView);
            pubCommentsTextView = itemView.findViewById(R.id.pubCommentsTextView);
            pubDescriptionTextView = itemView.findViewById(R.id.pubDescriptionTextView);
            pubViewMoreTextView = itemView.findViewById(R.id.pubViewMoreTextView);
            pubImageView = itemView.findViewById(R.id.pubImageView);

            // gestiona el evento de click sobre el botón de info
            pubViewMoreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onViewMoreClick(position);
                        }
                    }
                }
            });
        }
    }

    public ListPublicationsAdapter(ArrayList<Publication> list, Context c){
        this.publicationsList = list;
        this.context = c;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout del item que se repetirá (y guardarlo en item)
        View item = LayoutInflater.from(context).inflate(R.layout.item_rv_publication,parent,false);
        // Creamos el ViewHolder
        PublicationViewHolder publicationViewHolder = new PublicationViewHolder(item, mListener);
        // Retornamos el ViewHolder
        return publicationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {
        Publication p = publicationsList.get(position);
        // Obtenemos los parámetros de la publicación:
        String pubUser = p.getUserName();
        String pubDate = p.getDate();
        String pubComments = p.getCant_comments();
        String pubDescription = p.getDescription();


        /*ESTO SIRVE PARA PODER CARGAR LA IMAGEN DENTRO DEL IMAGEVIEW ------------------------------ */

        StorageReference imageRef = storageRef.child("publications/"+p.getPublicationId());
        Toast.makeText(context, p.getPublicationId(), Toast.LENGTH_SHORT);
        //ImageView imageView = null;

        Glide.with(context)
                .load(imageRef)
                .into(holder.pubImageView);
        /* --------------------------------------------------AQUI TERMINA LA CARGA EN EL IMAGEVIEW */

        // Le pasamos los parámetros de la publicación a la vista:
        holder.pubUserTextView.setText(pubUser);
        holder.pubDateTextView.setText(pubDate);
        holder.pubCommentsTextView.setText(pubComments + " Comentarios");
        holder.pubDescriptionTextView.setText(pubDescription);


    }

    @Override
    public int getItemCount() {
        return publicationsList.size();
    }
}
