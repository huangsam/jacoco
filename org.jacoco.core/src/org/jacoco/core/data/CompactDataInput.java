/*******************************************************************************
 * Copyright (c) 2009 Mountainminds GmbH & Co. KG and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 * $Id: $
 *******************************************************************************/
package org.jacoco.core.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Additional data input methods for compact storage of data structures.
 * 
 * @see CompactDataOutput
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class CompactDataInput extends DataInputStream {

	/**
	 * Creates a new {@link CompactDataInput} that uses the specified underlying
	 * input stream.
	 * 
	 * @param in
	 *            underlying input stream
	 */
	public CompactDataInput(final InputStream in) {
		super(in);
	}

	/**
	 * Reads a variable length representation of an integer value.
	 * 
	 * @return read value
	 * @throws IOException
	 *             might be thrown by the underlying stream
	 */
	public int readVarInt() throws IOException {
		final int value = 0xFF & readByte();
		if ((value & 0x80) == 0) {
			return value;
		}
		return (value & 0x7F) | (readVarInt() << 7);
	}

	private int booleanBuffer = 0;

	private int booleanBufferSize = 0;

	/**
	 * Reads a boolean value. Internally a sequence of boolean values is packed
	 * into single bits. After the last boolean value has been read
	 * {@link #finishPackedBoolean()} has to be called.
	 * 
	 * @return boolean value
	 * @throws IOException
	 */
	public boolean readPackedBoolean() throws IOException {
		if (booleanBufferSize == 0) {
			booleanBuffer = readByte();
			booleanBufferSize = 8;
		}
		final boolean value = (booleanBuffer & 0x01) != 0;
		booleanBuffer >>>= 1;
		booleanBufferSize--;
		return value;
	}

	/**
	 * Finalizes the input of a sequence of packed boolean values.
	 * 
	 * @throws IOException
	 */
	public void finishPackedBoolean() throws IOException {
		booleanBufferSize = 0;
	}

}