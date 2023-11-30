package com.s8.core.bohr.neodymium.fields.arrays;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.flow.repository.objects.RepoS8Object;
import com.s8.core.bohr.neodymium.exceptions.NdIOException;
import com.s8.core.bohr.neodymium.fields.NdField;
import com.s8.core.bohr.neodymium.fields.NdFieldDelta;
import com.s8.core.bohr.neodymium.type.BuildScope;



/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class FloatArrayNdFieldDelta extends NdFieldDelta {

	
	public final FloatArrayNdField field;
	
	public final float[] value;

	public FloatArrayNdFieldDelta(FloatArrayNdField field, float[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	@Override
	public NdField getField() {
		return field;
	}

	@Override
	public void consume(RepoS8Object object, BuildScope scope) throws NdIOException {
		field.handler.set(object, value);
	}	

	@Override
	public void computeFootprint(MemoryFootprint weight) {
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.length*4);
		}
	}


}
