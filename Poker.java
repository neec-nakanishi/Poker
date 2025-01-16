import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

class Poker extends JFrame {
    private CardHolder stock; // ストックしているカード
    private CardHolder used;  // 使用したカード
    private CardHolder hand;  // 手持ちカード
    private JLabel winning;    // 役の表示
    private JButton change;  // カードの交換

    public static void main(String[] args) {
    	new Poker();
    }

    public Poker() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLayout(null);
		change = new JButton("Change");
		change.setSize(100, 50);
		change.setLocation(140, 250);
		change.addMouseListener(new actionListener());
		change.setVisible(false);
		getContentPane( ).add(change);
		winning = new JLabel();
		winning.setSize(200, 50);
		winning.setLocation(100, 20);
		getContentPane( ).add(winning);

		getContentPane( ).setBackground(new Color(0,196,0));

		// カードの作成
		hand = new CardHolder();
		used = new CardHolder();
		stock = new CardHolder();
		for (int i=0 ; i<4 ; i++) {
		    for (int j=1 ; j<=13 ; j++) {
			Card c = new Card(i*13+j);
			c.addMouseListener(new actionListener());
			getContentPane( ).add(c);
			c.setVisible(false);
			stock.add(c);
		    }
		}

		// 表示
		setVisible(true);

		// ゲームをスタート
		gameStart();
    }

    public void cardReset() {
		// カードをストックに戻す
		while(hand.size()!=0) {
			Card c = hand.remove(0);
			c.setVisible(false);
		    stock.add(c);
		}
		while(used.size()!=0) {
		    stock.add(used.remove(0));
		}
		// カードをシャッフル
		stock.shuffle();
    }

    public void gameStart() {
    	// 初期化
		cardReset();

		// 5枚カードを並べる
		for (int i=0 ; i<5 ; i++) {
		    Thread t = new Thread(new GetOneCard(i));
		    t.start();
		    join(t);
		}
		// 役をチェック
		checkWinning();
    }

    public class actionListener extends MouseAdapter{
		@Override
		public void mouseReleased(MouseEvent e){
		    if ("Card" == e.getSource().getClass().getName()) {
		    	if (change.getText()=="Change") {
					// カードをクリックした場合、ひっくり返す
					Card c = (Card)(e.getSource());
					c.reverse();
		    	}
		    } else if (e.getSource()==change) {
			    winning.setText("");
		    	change.setVisible(false);
		    	if (change.getText()=="Start") {
					// [Start]をクリックした場合、5枚カードを並べる
		    		change.setText("Change");
		    		// 手札をすべてひっくり返す
		    		for (Card c : hand) {
		    			c.setFace(false);
		    		}
		    		// 5枚カードを並べる
					Thread t = new Thread(new ChangeCard());
					t.start();
		    	} else {
					// [Change]をクリックした場合、裏向きのカードを交換する
		    		change.setText("Start");
					Thread t = new Thread(new ChangeCard());
					t.start();
		    	}
		    }
		}
    }

    public void checkWinning() {
		if (isRoyal() && isFlush() && isStraight()) {
			winning.setText("ロイヤルストレートフラッシュ！");
		} else if (isFlush() && isStraight()) {
			winning.setText("ストレートフラッシュ");
		} else if (isFourOfAKind()) {
			winning.setText("フォーカード");
		} else if (isFullHouse()) {
			winning.setText("フルハウス");
		} else if (isFlush()) {
			winning.setText("フラッシュ");
		} else if (isStraight() || isRoyal()) {
			winning.setText("ストレート");
		} else if (isThreeOfAKind()) {
			winning.setText("スリーカード");
		} else if (isTwoPair()) {
			winning.setText("ツーペア");
		} else if (isOnePair()) {
			winning.setText("ワンペア");
		} else {
			winning.setText("役なし");
		}
		change.setVisible(true);
    }

	private boolean isRoyal() {
		int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = hand.get(i).getNum();
        }
        Arrays.sort(values);
		if (values[0]==1 && values[1]==10 && values[2]==11 && values[3]==12 && values[4]==13) {
			return true;
		}
		return false;
	}
    private boolean isFlush() {
        int suit = hand.get(0).getSuit();
        for (int i=0 ; i<5 ; i++) {
            if (!(hand.get(i).getSuit()==suit)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStraight() {
        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = hand.get(i).getNum();
        }
        Arrays.sort(values);
        for (int i = 0; i < values.length - 1; i++) {
            if (values[i] + 1 != values[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private boolean isFourOfAKind() {
        return hasSameValue(4);
    }

    private boolean isFullHouse() {
        return hasSameValue(3) && hasSameValue(2);
    }

    private boolean isThreeOfAKind() {
        return hasSameValue(3);
    }

    private boolean isTwoPair() {
        Map<Integer, Integer> valueCounts = getValueCounts();
        int pairCount = 0;
        for (int count : valueCounts.values()) {
            if (count == 2) {
                pairCount++;
            }
        }
        return pairCount == 2;
    }

    private boolean isOnePair() {
        return hasSameValue(2);
    }

    private boolean hasSameValue(int count) {
        Map<Integer, Integer> valueCounts = getValueCounts();
        return valueCounts.containsValue(count);
    }

    private Map<Integer, Integer> getValueCounts() {
        Map<Integer, Integer> valueCounts = new HashMap<>();
        for (int i=0 ; i<5 ; i++) {
            int value = hand.get(i).getNum();
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
        }
        return valueCounts;
    }	

	public void sleep(int ms) {
		try {
		    Thread.sleep(ms);
		} catch(Exception e) {
		}
    }

    public void join(Thread t) {
		try {
		    t.join();
		} catch(Exception e) {
		}
    }

    public class ChangeCard implements Runnable {
		@Override
		public void run() {
			// 裏向きのカードを消す
		    for (int i=0; i<hand.size(); i++) {
				Card c = hand.get(i);
				if (c.getFace()==false) {
				    c.setVisible(false);
				}
		    }
		    //
		    for (int i=0; i<hand.size(); i++) {
				Card c = hand.get(i);
				if (c.getFace()==false) {
				    hand.remove(c);
				    used.add(c);
				    Thread t = new Thread(new GetOneCard(i));
				    t.start();
				    join(t);
				}
		    }
		    // 役のチェック
		    checkWinning();
		}
    }

    public class GetOneCard implements Runnable {
		private int index;
		public GetOneCard(int index) {
		    this.index = index;
		}
		@Override
		public void run() {
		    if(stock.size() <= 52/2) {
			// ストックのカードが半分になったら、使ったカードをストックに戻す
				while(used.size()!=0) {
				    stock.add(used.remove(0));
				}
				stock.shuffle();
		    }
		    sleep(500);
		    // ストックからカードを１枚引く
		    Card c = stock.remove(0);
		    hand.add(index, c);
		    c.setLocation(40+index*(c.getWidth()+10), 100);
		    c.setVisible(true);
		    sleep(500);
		    // カードをひっくり返す
		    c.reverse();
		}
    }
}
