import java.awt.Color;
import java.util.Random;

public class PaletteCreator {
	
    public static Color[] erzeugeFarben(int count, boolean grayscale) {
    	int farbe=0;
    	Random myRandom = new Random();
    	int r=myRandom.nextInt(155);
    	int g=myRandom.nextInt(155);
    	int b=myRandom.nextInt(155);
    	boolean rUp=true;
    	boolean gUp=false;
    	boolean bUp=true;

    	Color[] meineFarben = new Color[count];
        for (int i = 0; i < count; i++) {
//        	if(i>100 && (i%7==0)) {
//        		meineFarben[i]=new Color(r,g,b);
//        		continue;
//        	}
//        	if(i>500 && (i%5==0)) {
//        		meineFarben[i]=new Color(r,g,b);
//        		continue;
//        	}
//        	if(i>1000 && (i%3==0)) {
//        		meineFarben[i]=new Color(r,g,b);
//        		continue;
//        	}

//            	if(i<10) {
//            		farbe += 5;
//            	} else if(i<100) {
//            		farbe += 2;
//            	} else {
//            		if(i%4==0) {
//            			farbe++;
//            		}
//            	}
                farbe++;
                if (farbe > 255)
                {
                    farbe=0;
                }
                if(rUp) {
                	r += myRandom.nextInt(5);
                } else {
                	r -= myRandom.nextInt(2);
                }
                if(r<0) {
                	r=0;
                	rUp=true;
                }
                if(r>255) {
                	r=255;
                	rUp=false;
                }
                
                
                if(gUp) {
                	g += myRandom.nextInt(2);
                } else {
                	g -= myRandom.nextInt(5);
                }
                if(g<0) {
                	g=0;
                	gUp=true;
                }
                if(g>255) {
                	g=255;
                	gUp=false;
                }
                
                
                if(bUp) {
                	b += myRandom.nextInt(5);
                } else {
                	b -= myRandom.nextInt(5);
                }
                if(b<0) {
                	b=0;
                	bUp=true;
                }
                if(b>255) {
                	b=255;
                	bUp=false;
                }
                if(grayscale) {
                	// meineFarben[i]=(i%2)==0?Color.BLACK:Color.white; // black/white intermittend
                	meineFarben[i]= new Color(farbe,farbe,farbe);
                } else {
                	meineFarben[i]=new Color(r,g,b);
                	//meineFarben[i]=new Color(Math.abs((150-farbe*1) % 255),Math.abs((128 - farbe*2) % 255),Math.abs((200-farbe*3) % 255));
                }
        }
        meineFarben[count-1] = new Color(80,0,0);
        return meineFarben;
    }
}
