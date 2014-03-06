import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class JTreeBuilder {

	public JTreeBuilder() {
		// TODO Auto-generated constructor stub
	}
	public OntologyJTree build(String pathToXml) throws Exception {
	     SAXReader reader = new SAXReader();
	     Document doc = reader.read(pathToXml);
	     Element root = doc.getRootElement();
	     OntologyItem rootItem = new OntologyItem(root);
	     return new OntologyJTree(build(rootItem));
	}

	public DefaultMutableTreeNode build(OntologyItem item) {
		Element e = item.getElement();
	   DefaultMutableTreeNode result = new DefaultMutableTreeNode(item);
	   
	   for(Object o : e.elements()) {
	      Element child = (Element) o;	    
	      result.add(build(new OntologyItem(child)));
	   }

	   return result;         
	}
}
