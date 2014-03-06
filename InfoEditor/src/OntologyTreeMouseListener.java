import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


public class OntologyTreeMouseListener implements MouseListener {
	JTree tree;
	TextEditor main;
	public OntologyTreeMouseListener(JTree _tree, TextEditor _main) {
		// TODO Auto-generated constructor stub
		tree=_tree;
		main=_main;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		int selRow = tree.getRowForLocation(x, y);
		TreePath selPath = tree.getPathForLocation(x, y);
		DefaultMutableTreeNode obj = (DefaultMutableTreeNode)selPath.getLastPathComponent();
		final OntologyItem selectedItem =(OntologyItem) obj.getUserObject();
		if (SwingUtilities.isRightMouseButton(e)) {
			final JPopupMenu popup = new JPopupMenu();
			JButton apply =new JButton("Apply Annotation");
			apply.addActionListener(new ActionListener() {          
				public void actionPerformed(ActionEvent e) {
					Component c = (Component) e.getSource();
					main.applyAnnotation(selectedItem.getValue(),selectedItem.getColor());
					popup.setVisible(false);
				}
			}); 
			popup.add(apply);
			popup.add(new JLabel("Name: "+selectedItem.getName()));
			popup.add(new JLabel("Color: "+selectedItem.getColorName()));
			popup.add(new JLabel("Value: "+selectedItem.getValue()));
			popup.add(new JLabel("Annotation: "+selectedItem.isAnnotation()));
			popup.show(tree, x, y);
		}
		else{

			if(selRow != -1) {
				if(e.getClickCount() == 1) {
					// System.out.println("Single Click");
					//  mySingleClick(selRow, selPath);
				}
				else if(e.getClickCount() == 2) {
					// System.out.println("Double Click");
					main.applyAnnotation(selectedItem.getValue(),selectedItem.getColor());

				}
			}	
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
