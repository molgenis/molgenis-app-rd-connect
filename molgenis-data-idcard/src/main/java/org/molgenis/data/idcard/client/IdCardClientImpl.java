package org.molgenis.data.idcard.client;

import com.google.common.primitives.Ints;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.idcard.mapper.IdCardEntityMapper;
import org.molgenis.data.idcard.model.IdCardBiobank;
import org.molgenis.data.idcard.model.IdCardOrganization;
import org.molgenis.data.idcard.model.IdCardRegistry;
import org.molgenis.data.idcard.settings.IdCardIndexerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;

@Service
public class IdCardClientImpl implements IdCardClient
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardClientImpl.class);

	private final HttpClient httpClient;
	private final IdCardIndexerSettings idCardIndexerSettings;
	private final IdCardEntityMapper idCardEntityMapper;

	@Autowired
	public IdCardClientImpl(HttpClient httpClient, IdCardIndexerSettings idCardIndexerSettings,
			IdCardEntityMapper idCardEntityMapper)
	{
		this.httpClient = requireNonNull(httpClient);
		this.idCardIndexerSettings = requireNonNull(idCardIndexerSettings);
		this.idCardEntityMapper = requireNonNull(idCardEntityMapper);
	}

	@Override
	public IdCardBiobank getIdCardBiobank(String id)
	{
		return getIdCardBiobank(id, idCardIndexerSettings.getApiTimeout());
	}

	private IdCardBiobank getIdCardBiobank(String id, long timeout)
	{
		// Construct uri
		String uriBuilder =
				idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings.getOrganisationResource() + '/' + id;

		return getIdCardResource(uriBuilder, new JsonResponseHandler<IdCardBiobank>()
		{
			@Override
			public IdCardBiobank deserialize(JsonReader jsonReader) throws IOException
			{
				return idCardEntityMapper.toIdCardBiobank(jsonReader);
			}
		}, timeout);
	}

	@Override
	public IdCardRegistry getIdCardRegistry(String id)
	{
		return getIdCardRegistry(id, idCardIndexerSettings.getApiTimeout());
	}

	private IdCardRegistry getIdCardRegistry(String id, long timeout)
	{
		// Construct uri
		String uriBuilder =
				idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings.getOrganisationResource() + '/' + id;

		return getIdCardResource(uriBuilder, new JsonResponseHandler<IdCardRegistry>()
		{
			@Override
			public IdCardRegistry deserialize(JsonReader jsonReader) throws IOException
			{
				return idCardEntityMapper.toIdCardRegistry(jsonReader);
			}
		}, timeout);
	}

	private <T> T getIdCardResource(String url, ResponseHandler<T> responseHandler, long timeout)
	{

		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		if (timeout != -1)
		{
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Ints.checkedCast(timeout))
					.setConnectionRequestTimeout(Ints.checkedCast(timeout)).setSocketTimeout(Ints.checkedCast(timeout))
					.build();
			request.setConfig(requestConfig);
		}
		try
		{
			LOG.info("Retrieving [" + url + "]");
			return httpClient.execute(request, responseHandler);
		}
		catch (IOException e)
		{
			throw new MolgenisDataException(e);
		}
	}

	@Override
	public Iterable<IdCardBiobank> getIdCardBiobanks()
	{
		return getIdCardBiobanks(idCardIndexerSettings.getApiTimeout());
	}

	@Override
	public Iterable<IdCardBiobank> getIdCardBiobanks(long timeout)
	{
		// Construct uri
		String uriBuilder =
				idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings.getBiobankCollectionResource();

		// Retrieve biobank ids
		Iterable<IdCardOrganization> idCardOrganizations = getIdCardResource(uriBuilder,
				new JsonResponseHandler<Iterable<IdCardOrganization>>()
				{
					@Override
					public Iterable<IdCardOrganization> deserialize(JsonReader jsonReader) throws IOException
					{
						return idCardEntityMapper.toIdCardOrganizations(jsonReader);
					}
				}, timeout);

		// Retrieve biobanks
		return this.getIdCardBiobanks(
				() -> stream(idCardOrganizations.spliterator(), false).map(IdCardOrganization::getOrganizationId)
						.iterator(), timeout);
	}

	private Iterable<IdCardBiobank> getIdCardBiobanks(Iterable<String> ids, long timeout)
	{
		String value = stream(ids.spliterator(), false).collect(Collectors.joining(",", "[", "]"));
		try
		{
			value = URLEncoder.encode(value, UTF_8.name());
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new RuntimeException(e1);
		}
		String uriBuilder = idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings
				.getBiobankCollectionSelectionResource() + '/' + value;

		return getIdCardResource(uriBuilder, new JsonResponseHandler<Iterable<IdCardBiobank>>()
		{
			@Override
			public Iterable<IdCardBiobank> deserialize(JsonReader jsonReader) throws IOException
			{
				return idCardEntityMapper.toIdCardBiobanks(jsonReader);
			}
		}, timeout);
	}

	@Override
	public Iterable<IdCardRegistry> getIdCardRegistries()
	{
		return getIdCardRegistries(idCardIndexerSettings.getApiTimeout());
	}

	@Override
	public Iterable<IdCardRegistry> getIdCardRegistries(long timeout)
	{
		// Construct uri
		String uriBuilder =
				idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings.getRegistryCollectionResource();

		// Retrieve biobank ids
		Iterable<IdCardOrganization> idCardOrganizations = getIdCardResource(uriBuilder,
				new JsonResponseHandler<Iterable<IdCardOrganization>>()
				{
					@Override
					public Iterable<IdCardOrganization> deserialize(JsonReader jsonReader) throws IOException
					{
						return idCardEntityMapper.toIdCardOrganizations(jsonReader);
					}
				}, timeout);

		// Retrieve biobanks
		return this.getIdCardRegistries(
				() -> stream(idCardOrganizations.spliterator(), false).map(IdCardOrganization::getOrganizationId)
						.iterator(), timeout);
	}

	private Iterable<IdCardRegistry> getIdCardRegistries(Iterable<String> ids, long timeout)
	{
		String value = stream(ids.spliterator(), false).collect(Collectors.joining(",", "[", "]"));
		try
		{
			value = URLEncoder.encode(value, UTF_8.name());
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new RuntimeException(e1);
		}
		String uriBuilder = idCardIndexerSettings.getApiBaseUri() + '/' + idCardIndexerSettings
				.getRegistryCollectionSelectionResource() + '/' + value;

		return getIdCardResource(uriBuilder, new JsonResponseHandler<Iterable<IdCardRegistry>>()
		{
			@Override
			public Iterable<IdCardRegistry> deserialize(JsonReader jsonReader) throws IOException
			{
				return idCardEntityMapper.toIdCardRegistries(jsonReader);
			}
		}, timeout);
	}

	private static abstract class JsonResponseHandler<T> implements ResponseHandler<T>
	{
		@Override
		public T handleResponse(final HttpResponse response) throws IOException
		{
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() < 100 || statusLine.getStatusCode() >= 300)
			{
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			}

			HttpEntity entity = response.getEntity();
			if (entity == null)
			{
				throw new ClientProtocolException("Response contains no content");
			}

			try (JsonReader jsonReader = new JsonReader(new InputStreamReader(entity.getContent(), UTF_8)))
			{
				return deserialize(jsonReader);
			}
		}

		public abstract T deserialize(JsonReader jsonReader) throws IOException;
	}
}