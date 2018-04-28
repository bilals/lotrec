package cytoscape.util;

import java.net.Proxy;
import java.net.InetSocketAddress;
import java.beans.*;

import cytoscape.CytoscapeInit;
import cytoscape.Cytoscape;


public class ProxyHandler implements PropertyChangeListener {

        private static Proxy proxyServer = null;

	static {
		new ProxyHandler();
	}

	private ProxyHandler() { 
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( this );
	}


        public static Proxy getProxyServer() {
		if ( proxyServer == null )
			loadProxyServer();
		return proxyServer;				
	}

        private static void loadProxyServer() {

		String proxyName = CytoscapeInit.getProperties().getProperty("proxy.server");
		if ( proxyName == null || proxyName.equals("") ) {
               		proxyServer = null;
			return;
		}

		String proxyT = CytoscapeInit.getProperties().getProperty("proxy.server.type");
		if ( proxyT == null || proxyT.equals("") ) {
               		proxyServer = null;
			return;
		}
		Proxy.Type proxyType = Proxy.Type.valueOf(proxyT);


		String proxyP = CytoscapeInit.getProperties().getProperty("proxy.server.port");
		if ( proxyP == null || proxyP.equals("") ) {
               		proxyServer = null;
			return;
		}

		int proxyPort = Integer.parseInt(proxyP);

		proxyServer = new Proxy(proxyType, new InetSocketAddress(proxyName,proxyPort));
        }

	public void propertyChange(PropertyChangeEvent e) {
		if ( e.getPropertyName() == Cytoscape.PREFERENCES_UPDATED ) 
			loadProxyServer();
	}
}
