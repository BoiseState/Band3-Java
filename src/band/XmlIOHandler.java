/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package band;

import java.awt.Color;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.List;
import java.util.LinkedList;
import java.text.ParseException;
import java.io.FileNotFoundException;

/**
 *
 * @author Ryan Thompson
 */
public class XmlIOHandler {
    private DocumentBuilder builder;
    boolean validating;
    
    public XmlIOHandler() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // If this didn't work something is really screwed up
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private Material extract(Element e) {
        Material m;
        String type = e.getAttribute("type");
        if(type.equals("Metal")) {
            m = new Metal();
            String workFunction = e.getElementsByTagName("WorkFunction").item(0).getTextContent();
            String extraCharge  = e.getElementsByTagName("ExtraCharge").item(0).getTextContent();
            ((Metal)m).setWorkFunction(Double.parseDouble(workFunction));
            ((Metal)m).setExtraCharge(Double.parseDouble(extraCharge));
        }
        else if (type.equals("Dielectric")) {
            m = new Dielectric();
            String dConst   = e.getElementsByTagName("DielectricConstant").item(0).getTextContent();
            String bandG    = e.getElementsByTagName("BandGap").item(0).getTextContent();
            String eAff     = e.getElementsByTagName("ElectronAffinity").item(0).getTextContent();
            try {
                ((Dielectric)m).setDielectricConstant(Double.parseDouble(dConst));
            } catch (NumberFormatException nfe) {
                ((Dielectric)m).setDielectricConstantExpression(dConst);
            }
            ((Dielectric)m).setBandGap(Double.parseDouble(bandG));
            ((Dielectric)m).setElectronAffinity(Double.parseDouble(eAff));
        }
        else if (type.equals("Semiconductor")) {
            m = new Semiconductor();
            String bandG    = e.getElementsByTagName("BandGap").item(0).getTextContent();
            String eAff     = e.getElementsByTagName("ElectronAffinity").item(0).getTextContent();
            String dConst   = e.getElementsByTagName("DielectricConstant").item(0).getTextContent();
            String iCC      = e.getElementsByTagName("IntrinsicCarrierConcentration").item(0).getTextContent();
            String doCon    = e.getElementsByTagName("DopantConcentration").item(0).getTextContent();

            ((Semiconductor)m).setBandGapExpression(bandG);
            ((Semiconductor)m).setElectronAffinity(Double.parseDouble(eAff));
            ((Semiconductor)m).setDielectricConstant(Double.parseDouble(dConst));
            ((Semiconductor)m).setIntrinsicCarrierConcentrationExpression(iCC);
            ((Semiconductor)m).setDopantConcentration(Double.parseDouble(doCon));
        }
        else {
            return null;
        }

        String name     = e.getElementsByTagName("Name").item(0).getTextContent();
        String notes    = e.getElementsByTagName("Notes").item(0).getTextContent();
        String fColor   = e.getElementsByTagName("FillColor").item(0).getTextContent();
        
        m.setName(name);
        m.setNotes(notes);
        m.setFillColor(Color.decode(fColor));
        
        return m;
    }
    
    private Element pack(Material m, Document doc) {
        Element e = doc.createElement("Material");
        if(m instanceof Metal) {
            Metal metal = (Metal) m;

            e.setAttribute("type", "Metal");

            Element wFun = doc.createElement("WorkFunction");
            Element exChg = doc.createElement("ExtraCharge");

            wFun.setTextContent(String.valueOf(metal.getWorkFunction()));
            exChg.setTextContent(String.valueOf(metal.getExtraCharge()));

            e.appendChild(wFun);
            e.appendChild(exChg);
        }
        else if (m instanceof Dielectric) {
            Dielectric d = (Dielectric) m;

            e.setAttribute("type", "Dielectric");

            Element dConst  = doc.createElement("DielectricConstant");
            Element bGap    = doc.createElement("BandGap");
            Element eAff    = doc.createElement("ElectronAffinity");

            dConst.setTextContent(d.getDielectricConstantExpression());
            bGap.setTextContent(String.valueOf(d.getBandGap()));
            eAff.setTextContent(String.valueOf(d.getElectronAffinity()));

            e.appendChild(dConst);
            e.appendChild(bGap);
            e.appendChild(eAff);
        }
        else { //(m instanceof Semiconductor)
            Semiconductor sem = (Semiconductor) m;

            e.setAttribute("type", "Semiconductor");

            Element dConst  = doc.createElement("DielectricConstant");
            Element bGap    = doc.createElement("BandGap");
            Element eAff    = doc.createElement("ElectronAffinity");
            Element iCC     = doc.createElement("IntrinsicCarrierConcentration");
            Element dCon    = doc.createElement("DopantConcentration");

            dConst.setTextContent(String.valueOf(sem.getDielectricConstant()));
            bGap.setTextContent(sem.getBandGapExpression());
            eAff.setTextContent(String.valueOf(sem.getElectronAffinity()));
            iCC.setTextContent(sem.getIntrinsicCarrierConcentrationExpression());
            dCon.setTextContent(String.valueOf(sem.getDopantConcentration()));

            e.appendChild(dConst);
            e.appendChild(bGap);
            e.appendChild(eAff);
            e.appendChild(iCC);
            e.appendChild(dCon);
        }

        Element name    = doc.createElement("Name");
        Element notes   = doc.createElement("Notes");
        Element fColor  = doc.createElement("FillColor");

        //Grab the hex value of color. Take substring because it returns alpha also
        String rgb = Integer.toHexString(m.fillColor.getRGB());
        rgb = "#" + rgb.substring(2, rgb.length());

        name.setTextContent(m.getName());
        notes.setTextContent(m.getNotes());
        fColor.setTextContent(rgb);
                
        e.appendChild(name);
        e.appendChild(notes);
        e.appendChild(fColor);
        return e;
    }
    
    private void writeToFile(Document doc, File f) throws IOException {
        try {
            FileOutputStream outStream = new FileOutputStream(f);
            
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outStream);
            
            transformer.transform(source, result);
            
            outStream.close();
        } catch (TransformerConfigurationException tce) {
              throw new IOException("* Transformer Factory error"
                    + "  " + tce.getMessage() );
        } catch (TransformerException te) {
              throw new IOException("* Transformation error"
                    + "  " + te.getMessage() );
        }
    }
    
    public Structure readStructure(File f) throws ParseException, FileNotFoundException {
        Structure s = new Structure();
        try {
            Document doc = builder.parse(f);
            Material m;
            NodeList stackList = doc.getElementsByTagName("Material");
            for(int i=0; i<stackList.getLength(); i++) {
                Element e = (Element)stackList.item(i);
                
                m = extract(e);
                String tNess    = e.getElementsByTagName("Thickness").item(0).getTextContent();
                m.setThicknessNm(Double.parseDouble(tNess));

                s.addLayer(m);
            }
        } catch (SAXParseException spe) {
            throw new ParseException(spe.getMessage(), spe.getLineNumber());
        } catch (IOException e) {
            throw new FileNotFoundException();
        } catch (SAXException se) {
            throw new ParseException(se.getMessage(), 0);
        }
        
        return s;
    }
    
    public List<Material> importMaterials(File f) throws ParseException, FileNotFoundException {
        LinkedList<Material> matList = new LinkedList<Material>();
        try {
            Document doc = builder.parse(f);
            NodeList matNodes = doc.getElementsByTagName("Material");
            for(int i=0; i<matNodes.getLength(); i++) {
                // Grab the next element

                Element e = (Element)matNodes.item(i);
                
                // Extract material out of element
                matList.add(extract(e));
            }
        } catch (SAXParseException spe) {
            throw new ParseException(spe.getMessage(), spe.getLineNumber());
        } catch (IOException e) {
            throw new FileNotFoundException();
        } catch (SAXException se) {
            throw new ParseException(se.getMessage(), 0);
        }
        
        return matList;
    }
    
    public void exportMaterials(List<Material> matList, File f) throws IOException {
        Document doc = builder.newDocument();
        Element lElement = doc.createElement("Library");
        
        for(Material m: matList) {
            // Pack material in element
            Element mat = pack(m, doc);
            
            // Append to library node
            lElement.appendChild(mat);
        }
        
        // Append library to document
        doc.appendChild(lElement);
        
        // Write it out
        writeToFile(doc, f);
    }
    
    public void saveStructure(Structure s, File f) throws IOException {
        Document doc = builder.newDocument();
        Element sElement = doc.createElement("Stack");
        
        for(int i=0; i<s.numLayers; i++) {
            Material m = s.getLayer(i);
            
            // Pack material into an element
            Element layer = pack(m, doc);
            
            // Tack on thickness since this is a structure
            Element tNess   = doc.createElement("Thickness");
            tNess.setTextContent(String.valueOf(m.getThicknessNm()));
            layer.appendChild(tNess);
            
            // Append to the stack list
            sElement.appendChild(layer);
        }
        
        // Append the structure node to the document
        doc.appendChild(sElement);
        
        // Write document to file
        writeToFile(doc, f);
    }
}