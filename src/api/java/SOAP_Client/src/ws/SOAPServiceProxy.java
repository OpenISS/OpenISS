package ws;

public class SOAPServiceProxy implements ws.SOAPService {
  private String _endpoint = null;
  private ws.SOAPService sOAPService = null;
  
  public SOAPServiceProxy() {
    _initSOAPServiceProxy();
  }
  
  public SOAPServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOAPServiceProxy();
  }
  
  private void _initSOAPServiceProxy() {
    try {
      sOAPService = (new ws.SOAPServiceImplServiceLocator()).getSOAPServiceImplPort();
      if (sOAPService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOAPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOAPService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOAPService != null)
      ((javax.xml.rpc.Stub)sOAPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ws.SOAPService getSOAPService() {
    if (sOAPService == null)
      _initSOAPServiceProxy();
    return sOAPService;
  }
  
  public byte[] getBytes(java.lang.String arg0) throws java.rmi.RemoteException{
    if (sOAPService == null)
      _initSOAPServiceProxy();
    return sOAPService.getBytes(arg0);
  }
  
  public void fromByteToJpg(byte[] arg0) throws java.rmi.RemoteException{
    if (sOAPService == null)
      _initSOAPServiceProxy();
    sOAPService.fromByteToJpg(arg0);
  }
  
  public byte[] getFrame() throws java.rmi.RemoteException{
    if (sOAPService == null)
      _initSOAPServiceProxy();
    return sOAPService.getFrame();
  }
  
  
}