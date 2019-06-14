package com.example.laboratorio09_10.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.laboratorio09_10.LogicaNegocio.Estudiante;
import com.example.laboratorio09_10.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.MyViewHolder> implements Filterable {

    private List<Estudiante> alumnoList;
    private List<Estudiante> alumnoListFiltered;
    private EstudianteAdapterListener listener;
    private Estudiante deletedItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo1, titulo2, description;
        //two layers
        public RelativeLayout viewForeground, viewBackgroundDelete, viewBackgroundEdit;


        public MyViewHolder(View view) {
            super(view);
            titulo1 = view.findViewById(R.id.titleFirstLbl);
            titulo2 = view.findViewById(R.id.titleSecLbl);
            description = view.findViewById(R.id.descriptionLbl);
            viewBackgroundDelete = view.findViewById(R.id.view_background_delete);
            viewBackgroundEdit = view.findViewById(R.id.view_background_edit);
            viewForeground = view.findViewById(R.id.view_foreground);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(alumnoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public EstudianteAdapter(List<Estudiante> alumnoList, EstudianteAdapterListener listener) {
        this.alumnoList = alumnoList;
        this.listener = listener;
        //init filter
        this.alumnoListFiltered = alumnoList;
    }

    @Override
    public EstudianteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EstudianteAdapter.MyViewHolder holder, int position) {
        // rendering view
        final Estudiante alumno = alumnoListFiltered.get(position);
        holder.titulo1.setText(alumno.getId());
        holder.titulo2.setText(alumno.getNombre());
        holder.description.setText(alumno.getCurso().toString());
    }

    @Override
    public int getItemCount() {
        return alumnoListFiltered.size();
    }

    public void removeItem(int position) {
        deletedItem = alumnoListFiltered.remove(position);
        Iterator<Estudiante> iter = alumnoList.iterator();
        while (iter.hasNext()) {
            Estudiante aux = iter.next();
            if (deletedItem.equals(aux))
                iter.remove();
        }
        // notify item removed
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) {

        if (alumnoListFiltered.size() == alumnoList.size()) {
            alumnoListFiltered.add(position, deletedItem);
        } else {
            alumnoListFiltered.add(position, deletedItem);
            alumnoList.add(deletedItem);
        }
        notifyDataSetChanged();
        // notify item added by position
        notifyItemInserted(position);
    }

    public Estudiante getSwipedItem(int index) {
        if (this.alumnoList.size() == this.alumnoListFiltered.size()) { //not filtered yet
            return alumnoList.get(index);
        } else {
            return alumnoListFiltered.get(index);
        }
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (alumnoList.size() == alumnoListFiltered.size()) { // without filter
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(alumnoList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(alumnoList, i, i - 1);
                }
            }
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(alumnoListFiltered, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(alumnoListFiltered, i, i - 1);
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    alumnoListFiltered = alumnoList;
                } else {
                    List<Estudiante> filteredList = new ArrayList<>();
                    for (Estudiante row : alumnoList) {
                        // filter use two parameters
                        if (row.getId().toLowerCase().contains(charString.toLowerCase()) || row.getNombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    alumnoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = alumnoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                alumnoListFiltered = (ArrayList<Estudiante>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface EstudianteAdapterListener {
        void onContactSelected(Estudiante estudiante);
    }

}
