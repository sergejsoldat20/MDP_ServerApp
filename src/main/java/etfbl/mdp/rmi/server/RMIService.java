package etfbl.mdp.rmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class RMIService {
	
	public static byte[] downloadReport(String filename) {
		System.setProperty("java.security.policy", "client_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			RMIReportInterface report = (RMIReportInterface) registry.lookup("RMIReport");
			return report.downloadReport(filename);			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getUploadedReports(){
		System.setProperty("java.security.policy", "client_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			RMIReportInterface report = (RMIReportInterface) registry.lookup("RMIReport");
			return report.uploadedReports();	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getReportsInfo() {
		System.setProperty("java.security.policy", "client_policyfile.txt");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			RMIReportInterface report = (RMIReportInterface) registry.lookup("RMIReport");
			return report.getReports();			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
