/**
 * SOAPServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws;

public class SOAPServiceImplServiceLocator extends org.apache.axis.client.Service implements ws.SOAPServiceImplService {

    public SOAPServiceImplServiceLocator() {
    }


    public SOAPServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SOAPServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SOAPServiceImplPort
    private java.lang.String SOAPServiceImplPort_address = "http://localhost:9000/ws/SOAPService";

    public java.lang.String getSOAPServiceImplPortAddress() {
        return SOAPServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SOAPServiceImplPortWSDDServiceName = "SOAPServiceImplPort";

    public java.lang.String getSOAPServiceImplPortWSDDServiceName() {
        return SOAPServiceImplPortWSDDServiceName;
    }

    public void setSOAPServiceImplPortWSDDServiceName(java.lang.String name) {
        SOAPServiceImplPortWSDDServiceName = name;
    }

    public ws.SOAPService getSOAPServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SOAPServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSOAPServiceImplPort(endpoint);
    }

    public ws.SOAPService getSOAPServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ws.SOAPServiceImplPortBindingStub _stub = new ws.SOAPServiceImplPortBindingStub(portAddress, this);
            _stub.setPortName(getSOAPServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSOAPServiceImplPortEndpointAddress(java.lang.String address) {
        SOAPServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ws.SOAPService.class.isAssignableFrom(serviceEndpointInterface)) {
                ws.SOAPServiceImplPortBindingStub _stub = new ws.SOAPServiceImplPortBindingStub(new java.net.URL(SOAPServiceImplPort_address), this);
                _stub.setPortName(getSOAPServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SOAPServiceImplPort".equals(inputPortName)) {
            return getSOAPServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws/", "SOAPServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws/", "SOAPServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SOAPServiceImplPort".equals(portName)) {
            setSOAPServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
