/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */


package org.openepics.discs.hourlog.ologmig;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A Raw HTML request/response logging filter. 
 * added level check to the handle.
 * 
 * @author Paul.Sandoz@Sun.Com, shroffk
 */
public class RawLoggingFilter extends ClientFilter {

	private static final Logger LOGGER = Logger
			.getLogger(RawLoggingFilter.class.getName());

	private static final String NOTIFICATION_PREFIX = "* ";

	private static final String REQUEST_PREFIX = "> ";

	private static final String RESPONSE_PREFIX = "< ";

	private final class Adapter extends AbstractClientRequestAdapter {
		private final StringBuilder b;

		Adapter(ClientRequestAdapter cra, StringBuilder b) {
			super(cra);
			this.b = b;
		}

		public OutputStream adapt(ClientRequest request, OutputStream out)
				throws IOException {
			return new LoggingOutputStream(getAdapter().adapt(request, out), b);
		}

	}

	private final class LoggingOutputStream extends OutputStream {
		private final OutputStream out;

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private final StringBuilder b;

		LoggingOutputStream(OutputStream out, StringBuilder b) {
			this.out = out;
			this.b = b;
		}

		@Override
		public void write(byte[] b) throws IOException {
			baos.write(b);
			out.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			baos.write(b, off, len);
			out.write(b, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			baos.write(b);
			out.write(b);
		}

		@Override
		public void close() throws IOException {
			printEntity(b, baos.toByteArray());
			log(b);
			out.close();
		}
	}

	private final PrintStream loggingStream;

	private final Logger logger;

	private long _id = 0;

	/**
	 * Create a logging filter logging the request and response to a default JDK
	 * logger, named as the fully qualified class name of this class.
	 */
	public RawLoggingFilter() {
		this(LOGGER);
	}

	/**
	 * Create a logging filter logging the request and response to a JDK logger.
	 * 
	 * @param logger
	 *            the logger to log requests and responses.
	 */
	public RawLoggingFilter(Logger logger) {
		this.loggingStream = null;
		this.logger = logger;
	}

	/**
	 * Create a logging filter logging the request and response to print stream.
	 * 
	 * @param loggingStream
	 *            the print stream to log requests and responses.
	 */
	public RawLoggingFilter(PrintStream loggingStream) {
		this.loggingStream = loggingStream;
		this.logger = null;
	}

	private void log(StringBuilder b) {
		if (logger != null) {
			logger.fine(b.toString());
		} else {
			loggingStream.print(b);
		}
	}

	private StringBuilder prefixId(StringBuilder b, long id) {
		b.append(Long.toString(id)).append(" ");
		return b;
	}

	@Override
	public ClientResponse handle(ClientRequest request)
			throws ClientHandlerException {
		if (this.logger.isLoggable(Level.FINE)) {
			long id = ++this._id;
			logRequest(id, request);
			
			ClientResponse response = getNext().handle(request);

			logResponse(id, response);

			return response;
		} else{
			return getNext().handle(request);			
		}

	}

	private void logRequest(long id, ClientRequest request) {
		StringBuilder b = new StringBuilder();

		printRequestLine(b, id, request);
		printRequestHeaders(b, id, request.getHeaders());

		if (request.getEntity() != null) {
			request.setAdapter(new Adapter(request.getAdapter(), b));
		} else {
			log(b);
		}
	}

	private void printRequestLine(StringBuilder b, long id,
			ClientRequest request) {
		prefixId(b, id).append(NOTIFICATION_PREFIX)
				.append("Client out-bound request").append("\n");
		prefixId(b, id).append(REQUEST_PREFIX).append(request.getMethod())
				.append(" ").append(request.getURI().toASCIIString())
				.append("\n");
	}

	private void printRequestHeaders(StringBuilder b, long id,
			MultivaluedMap<String, Object> headers) {
		for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
			String header = e.getKey();
			for (Object value : e.getValue()) {
				prefixId(b, id).append(REQUEST_PREFIX).append(header)
						.append(": ")
						.append(ClientRequest.getHeaderValue(value))
						.append("\n");
			}
		}
		prefixId(b, id).append(REQUEST_PREFIX).append("\n");
	}

	private void logResponse(long id, ClientResponse response) {
		StringBuilder b = new StringBuilder();

		printResponseLine(b, id, response);
		printResponseHeaders(b, id, response.getHeaders());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = response.getEntityInputStream();
		try {
			ReaderWriter.writeTo(in, out);

			byte[] requestEntity = out.toByteArray();
			printEntity(b, requestEntity);
			response.setEntityInputStream(new ByteArrayInputStream(
					requestEntity));
		} catch (IOException ex) {
			throw new ClientHandlerException(ex);
		}
		log(b);
	}

	private void printResponseLine(StringBuilder b, long id,
			ClientResponse response) {
		prefixId(b, id).append(NOTIFICATION_PREFIX)
				.append("Client in-bound response").append("\n");
		prefixId(b, id).append(RESPONSE_PREFIX)
				.append(Integer.toString(response.getStatus())).append("\n");
	}

	private void printResponseHeaders(StringBuilder b, long id,
			MultivaluedMap<String, String> headers) {
		for (Map.Entry<String, List<String>> e : headers.entrySet()) {
			String header = e.getKey();
			for (String value : e.getValue()) {
				prefixId(b, id).append(RESPONSE_PREFIX).append(header)
						.append(": ").append(value).append("\n");
			}
		}
		prefixId(b, id).append(RESPONSE_PREFIX).append("\n");
	}

	private void printEntity(StringBuilder b, byte[] entity) throws IOException {
		if (entity.length == 0)
			return;
		b.append(new String(entity)).append("\n");
	}
}