package de.devland.masterpassword.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.ui.DummyCard;
import de.devland.masterpassword.ui.SiteCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by David Kunzler on 03/09/14.
 */
public class SiteCardArrayAdapter extends CardArrayAdapter {

    private Filter filter;
    private List<Card> allCards;

    public SiteCardArrayAdapter(Context context, List<Card> cards) {
        super(context, cards);
        allCards = new ArrayList<>();
        allCards.addAll(cards);

        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Card> tempList = new ArrayList<>();
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                if (constraint != null) {
                    // Live Site
                    int length = allCards.size();
                    int i = 0;
                    while (i < length) {
                        Card card = allCards.get(i);
                        if (card instanceof SiteCard) {
                            SiteCard siteCard = (SiteCard) card;
                            Site site = siteCard.getSite();
                            if (site.getSiteName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                                tempList.add(siteCard);
                            }
                        } else if (card instanceof DummyCard) {
                            tempList.add(card);
                        }

                        i++;
                    }
                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                SiteCardArrayAdapter.super.clear();
                SiteCardArrayAdapter.super.addAll((List<Card>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        Card card = getItem(position);
        if (card instanceof SiteCard) {
            return ((SiteCard) card).getSite().getId();
        } else if (card instanceof DummyCard) {
            return -1l;
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public void add(Card object) {
        super.add(object);
        allCards.add(object);
    }

    @Override
    public void insert(Card card, int index) {
        super.clear();
        allCards.add(index, card);
        super.addAll(allCards);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends Card> collection) {
        super.addAll(collection);
        allCards.addAll(collection);
    }

    @Override
    public void addAll(Card... items) {
        super.addAll(items);
        allCards.addAll(Arrays.asList(items));
    }

    @Override
    public void clear() {
        super.clear();
        allCards.clear();
    }

    @Override
    public void remove(Card object) {
        super.remove(object);
        allCards.remove(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Object tag = null;
        if (convertView != null) {
            tag = convertView.getTag();
        }
        if (convertView != null && tag != null && (Boolean) tag) {
            view = super.getView(position, null, parent);
        } else {
           view = super.getView(position, convertView, parent);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
