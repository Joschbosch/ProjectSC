/*
 * Copyright (C) 2016 
 */
 
package de.projectsc.core.data.utils.colladaloader;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

public class XMLLoader {
    
    
    public static void main(String[] args){
        Document doc=null;
        try {
            XMLReader xerces = XMLReaderFactory.createXMLReader(); 
            Builder parser = new Builder(xerces, false);
            doc = parser.build(XMLLoader.class.getResourceAsStream("/models/animated/dragon.dae"));
            Element root = doc.getRootElement();
            listChildren(root, 0);      
          }
          catch (ParsingException | IOException | SAXException  ex) {
            ex.printStackTrace();
          }
        
    }
    
    public static void listChildren(Node current, int depth) {
        
        printSpaces(depth);
        String data = "";
        if (current instanceof Element) {
            Element temp = (Element) current;
            data = ": " + temp.getQualifiedName();   
        }
        else if (current instanceof ProcessingInstruction) {
            ProcessingInstruction temp = (ProcessingInstruction) current;
            data = ": " + temp.getTarget();   
        }
        else if (current instanceof DocType) {
            DocType temp = (DocType) current;
            data = ": " + temp.getRootElementName();   
        }
        else if (current instanceof Text || current instanceof Comment) {
            String value = current.getValue();
            value = value.replace('\n', ' ').trim();
            if (value.length() <= 20) data = ": " + value;
            else data = ": " + current.getValue().substring(0, 17) + "...";   
        }
        // Attributes are never returned by getChild()
        System.out.println(data);
        for (int i = 0; i < current.getChildCount(); i++) {
          listChildren(current.getChild(i), depth+1);
        }
        
      }
      
      private static void printSpaces(int n) {
        
        for (int i = 0; i < n; i++) {
          System.out.print(' '); 
        }
        
      }
}
