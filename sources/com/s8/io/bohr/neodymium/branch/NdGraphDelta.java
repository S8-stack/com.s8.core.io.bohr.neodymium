package com.s8.io.bohr.neodymium.branch;

import static com.s8.io.bohr.atom.BOHR_Keywords.CLOSE_JUMP;
import static com.s8.io.bohr.atom.BOHR_Keywords.DEFINE_JUMP_COMMENT;
import static com.s8.io.bohr.atom.BOHR_Keywords.DEFINE_JUMP_TIMESTAMP;
import static com.s8.io.bohr.atom.BOHR_Keywords.OPEN_JUMP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.s8.io.bohr.neodymium.branch.endpoint.NdOutbound;
import com.s8.io.bohr.neodymium.exceptions.NdIOException;
import com.s8.io.bohr.neodymium.object.NdObjectDelta;
import com.s8.io.bohr.neodymium.type.BuildScope;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class NdGraphDelta {




	/**
	 * Mandatriy version
	 */
	public final long targetVersion;


	/**
	 * the revision generated by applying this delta to previous rev
	 */
	private boolean hasTimestamp;

	/**
	 * 
	 */
	private long timestamp;


	/**
	 * 
	 */
	private boolean hasComment;

	/**
	 * 
	 */
	private String comment;


	/**
	 * 
	 */
	public List<NdObjectDelta> objectDeltas = new ArrayList<>();


	/**
	 * 
	 */
	public long lastAssignedIndex = -1;


	/**
	 * 
	 */
	public NdGraphDelta(long version) {
		super();
		this.targetVersion = version;
	}

	
	
	public void setComment(String comment) {
		this.hasComment = true;
		this.comment = comment;
	}
	
	
	public void setTimestamp(long timestamp) {
		this.hasTimestamp = true;
		this.setTimestamp(timestamp);
	}

	/**
	 * 
	 * @param delta
	 */
	public void appendObjectDelta(NdObjectDelta delta) {
		objectDeltas.add(delta);
	}



	/**
	 * 
	 * @param graph
	 * @throws NdIOException
	 */
	public void operate(NdGraph graph) throws NdIOException {
		/* check version */
		if(targetVersion != (graph.version + 1)) { 
			throw new NdIOException("Mismatch in versions");
		}
		
		BuildScope scope = graph.createBuildContext();
		for(NdObjectDelta objectDelta : objectDeltas) { 
			objectDelta.consume(graph, scope); 
		}
		scope.resolve();
		
		/* increment version of graph */
		graph.version++;
	}




	public void serialize(NdOutbound outbound, ByteOutflow outflow) throws IOException {

		outflow.putUInt8(OPEN_JUMP);
		
		outflow.putUInt64(targetVersion);
		
		
		if(hasTimestamp) {
			outflow.putUInt8(DEFINE_JUMP_TIMESTAMP);
			outflow.putUInt64(timestamp);
		}
		
		if(hasComment) {
			outflow.putUInt8(DEFINE_JUMP_COMMENT);
			outflow.putStringUTF8(comment);
		}

		// compose common database
		//codebaseIO.compose(outflow, false);
		for(NdObjectDelta objectDelta : objectDeltas) { 
		
			
			objectDelta.serialize(outbound, outflow); 
		}


		outflow.putUInt8(CLOSE_JUMP);
	}


	public void computeFootprint(MemoryFootprint weight) {
		weight.reportInstance();
		objectDeltas.forEach(delta -> delta.computeFootprint(weight));
	}

}