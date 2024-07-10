package com.restaurant.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * Utility class for testing servlets.
 */
public class TestUtils {
	/**
     * A custom ServletOutputStream for testing purposes.
     */
	public static class TestServletOutputStream extends ServletOutputStream{
		private final ByteArrayOutputStream byteArrayOutputStream;
		
		/**
         * Constructs a new TestServletOutputStream.
         */
		public TestServletOutputStream() {
			byteArrayOutputStream = new ByteArrayOutputStream();
		}
		
		 /**
         * Indicates whether this output stream is ready to be written to.
         *
         * @return true, indicating that the stream is always ready.
         */
		@Override
		public boolean isReady() {
			return true;
		}
		
		/**
         * Sets a WriteListener for this output stream.
         *
         * @param writeListener the WriteListener to set.
         */
		@Override
		public void setWriteListener(WriteListener writeListener) {
			
		}
		
		/**
         * Writes a byte to the output stream.
         *
         * @param b the byte to write.
         * @throws IOException if an I/O error occurs.
         */
		@Override
		public void write(int b) throws IOException {
			byteArrayOutputStream.write(b);			
		}
		
		/**
         * Retrieves the content written to the output stream as a string.
         *
         * @return the content of the output stream.
         */
		public String getResponseContent() {
			return byteArrayOutputStream.toString();
		}		
	}
	
	/**
	 * A custom implementation of {@link jakarta.servlet.ServletInputStream} for testing purposes.
	 * This class simulates a servlet input stream containing JSON data.
	 */	
	public static class TestServletInputStream extends ServletInputStream {

	    /** 
	     * A field that stores the string representation of a JSON object.
	     */
	    private final String data;

	    /** 
	     * The index of the current reading position in {@code data}.
	     */
	    private int position;

	    /**
	     * Constructs a {@code TestServletInputStream} with the given object.
	     * The object is serialized into a JSON string and stored in {@code data}.
	     *
	     * @param object the object to be serialized into JSON.
	     * @throws IOException if an I/O error occurs.
	     */
	    public TestServletInputStream(Object object) throws IOException {
	        this.data = new ObjectMapper().writeValueAsString(object);
	        this.position = 0;
	    }

	    /**
	     * Returns {@code true} if the current position is greater than or equal to the length of {@code data},
	     * indicating that all data has been read.
	     *
	     * @return {@code true} if all data has been read; {@code false} otherwise.
	     */
	    @Override
	    public boolean isFinished() {
	        return position >= data.length();
	    }

	    /**
	     * Always returns {@code true}, indicating that the stream is ready for reading.
	     *
	     * @return {@code true} indicating the stream is ready for reading.
	     */
	    @Override
	    public boolean isReady() {
	        return true;
	    }

	    /**
	     * An empty implementation of the method that sets a read listener. This is not used in this context.
	     *
	     * @param readListener the {@code ReadListener} to be set.
	     */
	    @Override
	    public void setReadListener(jakarta.servlet.ReadListener readListener) {
	        // Not implemented
	    }

	    /**
	     * Reads the next character from {@code data}. If the end of the string is reached,
	     * returns {@code -1}, indicating the end of the stream. Otherwise, returns the next character
	     * and increments {@code position}.
	     *
	     * @return the next character as an integer, or {@code -1} if the end of the stream is reached.
	     * @throws IOException if an I/O error occurs.
	     */
	    @Override
	    public int read() throws IOException {
	        if (position >= data.length()) {
	            return -1;
	        }
	        return data.charAt(position++);
	    }
	}
}