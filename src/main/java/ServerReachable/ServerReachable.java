package ServerReachable;

import java.net.Socket;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerReachable {

    public static void main(String[] args) {
        readConfig("configuration/config.xml");
    }
    public static boolean isServerReachable(String host, int port) {
        readConfig("configuration/config.xml");
        try (Socket socket = new Socket(host, port)) {
            System.out.println(socket);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // reading configuration folder
    public static void readConfig(String configPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(configPath);
            document.getDocumentElement().normalize();

            NodeList serviceList = document.getElementsByTagName("service");

            for (int i = 0; i < serviceList.getLength(); i++) {
                Node serviceNode = serviceList.item(i);

                if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element serviceElement = (Element) serviceNode;

                    String host = serviceElement.getElementsByTagName("serviceHost").item(0).getTextContent();
                    int port = Integer.parseInt(serviceElement.getElementsByTagName("servicePort").item(0).getTextContent());
                    String serviceName = serviceElement.getElementsByTagName("serviceName").item(0).getTextContent();
                    System.out.println("Service:\n" +
                            "Service name:  " + serviceName + "\n" +
                            "Host:          " + host + "\n" +
                            "Port:          " + port);

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
