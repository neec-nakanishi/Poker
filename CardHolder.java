import java.util.*;

public class CardHolder extends ArrayList<Card> {
    public CardHolder() {
	super();
    }

    public CardHolder(CardHolder ch) {
	super(ch);
    }

    public void shuffle() {
	for (int i=1 ; i<=1000 ; i++) {
	    int r = (int)(Math.random()*(size()));
	    add(remove(r));
	}
    }

    public void sort() {
	Collections.sort(this, new CardComparator());
    }

    class CardComparator implements Comparator {
	public int compare(Object s, Object t) {
	    return ((Card)s).getNum() - ((Card)t).getNum();
	}
    }
}
