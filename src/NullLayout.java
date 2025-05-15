/**
 *
 * @author Student
 */
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;
public class NullLayout {
   JFrame frame;
   JLabel label;
   JTextField textfield ;
   
   public NullLayout(){
   }
   public void UI(){
   System.out.println("XYZ");
   frame = new JFrame("First Application");
   frame.setPreferredSize(new Dimension(400,200));
   frame.setSize(400,200);
   frame.setLocationRelativeTo(null);
   frame.setLayout(null);
   addUIElements ();
   frame.setVisible(true);
   }
   
   public static void main(String[] args){
      NullLayout nl = new NullLayout();
      nl.UI();
       
   }
   public void addUIElements(){
       
       label = new JLabel("Name");
       JLabel label2 = new JLabel("Email");
       label.setSize(80,20);
       label2.setSize(100,30);
       label.setLocation(0,0);
       label2.setLocation(60,80);
       
       textfield = new JTextField();
       textfield.setSize(100,20);
       textfield.setLocation(50,80);
   
      
        frame.add(label);   
        frame.add(label2);
   }
  
}
