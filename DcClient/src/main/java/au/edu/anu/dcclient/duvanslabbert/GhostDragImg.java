/*
 * Source: http://www.java-forums.org/blogs/duvanslabbert/92-java-file-explorer.html
 */

package au.edu.anu.dcclient.duvanslabbert;
import java.awt.MouseInfo;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class GhostDragImg extends JWindow implements Runnable{
	public boolean stop;
	private JLabel lbl=new JLabel();
	private ImageIcon img;
	private String str;
	public GhostDragImg(){
		add(lbl);
		try {		
			Class<?>awtUtilitiesClass=Class.forName("com.sun.awt.AWTUtilities");
			Method method=awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);		
			method.invoke(null,this,0.6f);
			method=awtUtilitiesClass.getMethod("setWindowOpaque", Window.class, boolean.class);
			method.invoke(null,this,false);
		}catch(Exception exc){exc.printStackTrace();}
		setAlwaysOnTop(true);
	}
	public void start(DefaultMutableTreeNode node){
		str=node.toString();  
		str=str.substring(str.lastIndexOf("\\")+1,str.length());		
		img=(ImageIcon)FileSystemView.getFileSystemView().getSystemIcon((File)node.getUserObject());		
		stop=false;
		lbl.setIcon(img);
		lbl.setText(str);
		setSize(img.getIconWidth()+lbl.getText().length()*10,img.getIconHeight());
		new Thread(this).start();
		setVisible(true);
	}
	public void stop(){
		stop=true;
	}
	public void run() {
		while(!stop)
			setLocation((int)MouseInfo.getPointerInfo().getLocation().getX()+4,(int)MouseInfo.getPointerInfo().getLocation().getY()-16);
		setVisible(false);
	}
}
