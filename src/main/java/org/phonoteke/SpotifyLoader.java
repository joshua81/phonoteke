package org.phonoteke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;

public class SpotifyLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("3294564c84e54285adeee3e05caf4b29")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();

	private static final ClientCredentialsRequest LOGIN = SPOTIFY_API.clientCredentials().build();

	private ClientCredentials credentials;

	private void login()
	{
		try 
		{
			if(credentials == null || credentials.getExpiresIn() < 5)
			{
				credentials = LOGIN.execute();
				SPOTIFY_API.setAccessToken(credentials.getAccessToken());
				LOGGER.info("Expires in: " + credentials.getExpiresIn() + " secs");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error connecting to Spotify: " + e.getMessage());
		}
	}

	public String getAlbumId(String band, String album)
	{
		try
		{
			login();
			
			Thread.currentThread().sleep(2000);
			SearchAlbumsRequest request = SPOTIFY_API.searchAlbums(album).build();
			Paging<AlbumSimplified> albums = request.execute();
			for(int j = 0; j < albums.getItems().length; j++)
			{
				AlbumSimplified a = albums.getItems()[j];
				ArtistSimplified[] artists = a.getArtists();
				for(int k = 0; k < artists.length; k++)
				{
					ArtistSimplified artist = artists[k];
					if(artist.getName().toLowerCase().replace(" ", "").trim().equals(band.toLowerCase().replace(" ", "").trim()))
					{
						return a.getId();
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error getting " + band + " - " + album + "  id: " + e.getMessage());
		}
		return null;
	}
}
