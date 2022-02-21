package etfbl.mdp.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.util.ArrayList;



public interface RMIReportInterface extends Remote{
	
	public void uploadReport(String user, String station, long fileLength, LocalTime time, String filename, byte[] data) throws RemoteException;
	
	public byte[] downloadReport(String filename) throws RemoteException;
	
	public ArrayList<String> getReports() throws RemoteException;
	
	public ArrayList<String> uploadedReports() throws RemoteException;
}
