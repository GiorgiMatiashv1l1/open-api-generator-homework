package ge.tbc.testautomation.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import org.w3c.dom.Document;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Properties;

public final class SoapUtils {

    private SoapUtils() {
    }

    public static <T> String marshal(T requestBody) {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            JAXBContext context = JAXBContext.newInstance(requestBody.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(requestBody, new DOMResult(soapPart.getEnvelope().getBody()));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Properties properties = new Properties();
            properties.setProperty("indent", "yes");
            properties.setProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperties(properties);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            transformer.transform(soapMessage.getSOAPPart().getContent(), new StreamResult(output));

            return output.toString(StandardCharsets.UTF_8);
        } catch (JAXBException | SOAPException | TransformerException e) {
            throw new IllegalStateException("Failed to marshal SOAP request body", e);
        }
    }

    public static <T> T unmarshal(String soapResponseBody, Class<T> responseType) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(soapResponseBody.getBytes(StandardCharsets.UTF_8));
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, input);
            Document document = message.getSOAPBody().extractContentAsDocument();

            Unmarshaller unmarshaller = JAXBContext.newInstance(responseType).createUnmarshaller();
            return responseType.cast(unmarshaller.unmarshal(document));
        } catch (SOAPException | JAXBException | IOException e) {
            throw new IllegalStateException("Failed to unmarshal SOAP response body", e);
        }
    }

    public static XMLGregorianCalendar toXmlDate(LocalDate date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth(), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Failed to build XMLGregorianCalendar", e);
        }
    }

    public static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
