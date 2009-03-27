package org.encog.util.xml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.encog.EncogError;
import org.encog.neural.NeuralNetworkError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLWrite {
	
	private final OutputStream output;
	private final TransformerHandler outputXML;
	private final AttributesImpl attributes;
	private final Stack<String> tagStack;
	
	public XMLWrite(final OutputStream os)
	{
		try {
			this.output = os;
			final StreamResult streamResult = new StreamResult(os);
			final SAXTransformerFactory tf = 
				(SAXTransformerFactory) TransformerFactory
					.newInstance();
			// SAX2.0 ContentHandler.
			this.outputXML = tf.newTransformerHandler();
			final Transformer serializer = this.outputXML.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			this.outputXML.setResult(streamResult);
			
			this.attributes = new AttributesImpl();
			this.tagStack = new Stack<String>();

		} catch (final TransformerConfigurationException e) {
			throw new EncogError(e);
		} 
	}
	
	public void close()
	{
		try {
			this.output.close();
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}
	
	public void addAttribute(String name, String value)
	{
		this.attributes.addAttribute("", "", name, "CDATA", value);
	}
	
	public void beginTag(String name)
	{
		try {
			this.outputXML.startElement("", "", name, this.attributes);
			this.tagStack.push(name);
		} catch (SAXException e) {
			throw new EncogError(e);
		}		
	}
	
	public void endTag()
	{
		try {
			if( this.tagStack.isEmpty() )
			{
				throw new NeuralNetworkError("Can't create end tag, no beginning tag.");
			}
			String tag = this.tagStack.pop();
			this.outputXML.endElement("", "", tag);
			this.attributes.clear();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void beginDocument()
	{
		try {
			this.outputXML.startDocument();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	public void endDocument()
	{
		try {
			this.outputXML.endDocument();
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void addText(String text)
	{
		try {
			this.outputXML.characters(text.toCharArray(), 0, text.length());
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}
	
	public void addCDATA(String text)
	{
		try {
			this.outputXML.startCDATA();
			this.addText(text);
			this.outputXML.endCDATA();
		} catch (SAXException e) {
			throw(new EncogError(e));
		}
		
	}
	
	public void addProperty(String name, String value)
	{
		beginTag(name);
		addText(value);
		endTag();
	}
}