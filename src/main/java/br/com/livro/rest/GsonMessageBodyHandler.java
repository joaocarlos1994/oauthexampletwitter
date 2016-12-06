package br.com.livro.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static String UTF_8 = "UTF-8";
	private Gson gson;

	private Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		}
		return gson;
	}

	@Override
	public long getSize(final Object arg0, Class<?> arg1, final Type arg2, final Annotation[] arg3,
			final MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(final Class<?> arg0, final Type arg1, final Annotation[] arg2, final MediaType arg3) {
		return true;
	}

	@Override
	public void writeTo(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
		
		final OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		
		try {
			Type jsonType;
			
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			
			getGson().toJson(object, jsonType, writer);
		} finally {
			writer.close();
		}
		
	}

	@Override
	public boolean isReadable(final Class<?> paramClass, final Type paramType, final Annotation[] paramArrayOfAnnotation,
			final MediaType paramMediaType) {
		return true;
	}

	@Override
	public Object readFrom(final Class<Object> type, final Type geneticType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
			throws IOException, WebApplicationException {
		
		final InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8);
		
		try {
			Type jsonType;
			if (type.equals(geneticType)) {
				jsonType = type;
			} else {
				jsonType = geneticType;
			}
			return getGson().fromJson(streamReader, jsonType);
		} finally {
			streamReader.close();
		}
	}

}
