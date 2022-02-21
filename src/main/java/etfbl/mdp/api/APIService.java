package etfbl.mdp.api;

import java.util.ArrayList;

import javax.print.PrintServiceLookup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import etfbl.gui.LinesController;
import etfbl.mdp.rest.Line;
import etfbl.mdp.rest.Timetable;

@Path("/lines")

public class APIService {
	
	TimetableService tService;	
	
	public APIService() {
		super();
		tService = new TimetableService();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{station}")
	public ArrayList<Line> getLinesForStation(@PathParam("station") String station) {
		return tService.getAllLinesForStation(station);
	}
	
	@PUT
	@Path("/{id}/{station}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateArrivalTime(@PathParam("id") int id, @PathParam("station") String station, String time) {
		if(tService.updateArrivalTime(id, station)) {
			return Response.status(200).build();
		}
		return Response.status(404).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteLine(@PathParam("id") int id) {
		if(tService.deleteLine(id)) {
			return Response.status(200).build();
		}
		return Response.status(404).build();
	}
}
