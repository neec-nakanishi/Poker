import javax.swing.*;

class Card extends JLabel {

    private int num;  // 数字
    private int suit; // 絵柄
    private boolean face; // true: 表　false: 裏
    private ImageIcon img;  // 表面の画像
    private ImageIcon back; // 裏面の画像

    public Card(int num) {
	this.num = num % 13; this.num = (this.num==0)?13:this.num;
	this.suit = (num-1) / 13;

	img = new ImageIcon("./img/"+num+".png");
	back = new ImageIcon("./img/back.png");
	setSize(img.getIconWidth( ), img.getIconHeight( ));
	setFace(false);
    }

    public int getNum() { return num; }
    public int getSuit() { return suit; }
    public boolean getFace() { return face; }
    public void setFace(boolean face) {
	this.face = face;
    	if (face) {
	    setIcon(img);
	} else {
	    setIcon(back);
	}
    }
    public void reverse() {
	// ひっくり返す
	setFace(!face);
    }
}
