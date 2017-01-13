package name.gudong.translate.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.Acknowledgement;
import name.gudong.translate.widget.CharacterDrawable;

public class AcknowledgementAdapter extends ListAdapter<Acknowledgement, AcknowledgementAdapter.AcknowledgementView> {

    public AcknowledgementAdapter(Context context, List<Acknowledgement> data) {
        super(context, data);
    }

    @Override
    protected AcknowledgementView createView(Context context) {
        return new AcknowledgementView(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        if (holder.itemView instanceof AcknowledgementView) {
            AcknowledgementView itemView = (AcknowledgementView) holder.itemView;
            if (getItemClickListener() instanceof AcknowledgementItemClickListener) {
                itemView.textViewLicense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AcknowledgementItemClickListener listener =
                                (AcknowledgementItemClickListener) getItemClickListener();
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.openLicense(position);
                        }
                    }
                });
            }
        }
        return holder;
    }

    class AcknowledgementView extends RelativeLayout implements IAdapterView<Acknowledgement> {

        @BindView(R.id.image_view_icon)
        ImageView imageViewIcon;
        @BindView(R.id.text_view_name)
        TextView textViewName;
        @BindView(R.id.text_view_description)
        TextView textViewDescription;
        @BindView(R.id.text_view_license)
        TextView textViewLicense;

        public AcknowledgementView(Context context) {
            super(context);
            View.inflate(context, R.layout.item_acknowledgement, this);
            ButterKnife.bind(this);
        }

        @Override
        public void bind(Acknowledgement item, int position) {
            Glide.with(getContext())
                    .load(item.icon)
                    .placeholder(
                            new CharacterDrawable.Builder()
                                    .setCharacter(item.name.charAt(0))
                                    .setCharacterPadding(getResources().getDimension(R.dimen.ff_padding_large))
                                    .build()
                    )
                    .into(imageViewIcon);
            textViewName.setText(item.name);
            textViewLicense.setText(item.licenseName);
            textViewDescription.setText(item.description);
        }
    }

    public interface AcknowledgementItemClickListener extends OnItemClickListener {
        void openLicense(int position);
    }
}
