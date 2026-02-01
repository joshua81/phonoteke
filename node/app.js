'use strict';

const express = require('express');
const robots = require('express-robots-txt');
const cookieParser = require('cookie-parser');
const { MongoClient } = require('mongodb');
const axios = require('axios');

// Configuration
const config = {
  port: process.env.PORT || 8080,
  pageSize: 20,
  mongodb: {
    uri: process.env.MONGODB_URI || "mongodb+srv://mbeats:PwlVOgNqv36lvVXb@hbeats-31tc8.gcp.mongodb.net/test?retryWrites=true&w=majority",
    dbName: "mbeats"
  },
  spotify: {
    clientId: process.env.SPOTIFY_CLIENT_ID || 'a6c3686d32cb48d4854d88915d3925be',
    clientSecret: process.env.SPOTIFY_CLIENT_SECRET || '46004c8b1a2b4c778cb9761ace300b6c',
    redirectUri: process.env.SPOTIFY_REDIRECT_URI || 'https://humanbeats.appspot.com/api/login/spotify'
  },
  songkick: {
    apiKey: process.env.SONGKICK_API_KEY || '1hOiIfT9pFTkyVkg'
  }
};

// Database collections
let collections = {
  docs: null,
  authors: null,
  stats: null
};

// Initialize MongoDB connection
async function initializeDatabase() {
  try {
    const client = new MongoClient(config.mongodb.uri, { 
      useNewUrlParser: true, 
      useUnifiedTopology: true 
    });
    
    await client.connect();
    const db = client.db(config.mongodb.dbName);
    
    collections.docs = db.collection("docs");
    collections.authors = db.collection("authors");
    collections.stats = db.collection("stats");
    
    console.log("Successfully Connected to MongoDB");
    return client;
  } catch (error) {
    console.error("Failed to connect to MongoDB:", error);
    process.exit(1);
  }
}

// Initialize Express app
const app = express();

// Middleware setup
app.set('views', './template');
app.set('view engine', 'ejs');
app.use('/images', express.static('images'));
app.use('/css', express.static('css'));
app.use('/js', express.static('js'));
app.use('/robots', express.static('robots'));
app.use('/', express.static('web'));
app.use(cookieParser());
app.use(robots({
  UserAgent: '*',
  CrawlDelay: '5',
  Sitemap: 'https://humanbeats.appspot.com/robots/sitemap.xml',
}));

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

// Utility functions
function sanitizeDoc(doc) {
  if (!doc) return doc;
  
  const fieldsToSanitize = ['artistid', 'albumid', 'spartistid', 'spalbumid', 'dgalbumid'];
  fieldsToSanitize.forEach(field => {
    if (doc[field] === 'na') {
      doc[field] = null;
    }
  });

  if (doc.tracks) {
    doc.tracks.forEach(track => {
      if (track.spotify === 'na') {
        track.spotify = null;
        track.spartistid = null;
        track.spalbumid = null;
      }
      fieldsToSanitize.forEach(field => {
        if (track[field] === 'na') {
          track[field] = null;
        }
      });
      if (track.youtube === 'na') {
        track.youtube = null;
      }
    });
  }
  
  return doc;
}

function buildSearchQuery(type, query, source) {
  const conditions = [];
  
  if (type) {
    conditions.push({ type });
  }
  
  if (source) {
    conditions.push({ source });
  }
  
  if (query) {
    const searchRegex = '.*' + query.split(' ').join('.*') + '.*';
    conditions.push({
      $or: [
        { artist: { $regex: searchRegex, $options: 'i' } },
        { title: { $regex: searchRegex, $options: 'i' } },
        { 'tracks.title': { $regex: searchRegex, $options: 'i' } }
      ]
    });
  }
  
  return conditions.length > 0 ? { $and: conditions } : {};
}

// API Routes
app.get('/api/affinities', async (req, res) => {
  try {
    console.log('/api/affinities');
    
    if (!req.query.artists) {
      return res.status(400).json({ error: 'Artists parameter is required' });
    }
    
    const statistics = await collections.stats
      .find()
      .project({ source: 1, artists: 1 })
      .toArray();
    
    const queryArtists = new Set(req.query.artists.split(','));
    const affinities = statistics
      .filter(stat => stat.source != null)
      .map(stat => {
        const statArtists = new Set(stat.artists.map(artist => artist.spartistid));
        const intersection = new Set([...statArtists].filter(x => queryArtists.has(x)));
        return {
          source: stat.source,
          affinity: intersection.size / queryArtists.size
        };
      });
    
    res.json(affinities);
  } catch (error) {
    console.error('Error in /api/affinities:', error);
    res.status(500).json({ error: 'Failed to fetch affinities' });
  }
});

app.get('/api/stats/:source?', async (req, res) => {
  try {
    const source = req.params.source || null;
    console.log(`/api/stats${source ? '/' + source : ''}`);
    
    const result = await collections.stats
      .find({ source })
      .toArray();
    
    res.json(result[0] || {});
  } catch (error) {
    console.error('Error in /api/stats:', error);
    res.status(500).json({ error: 'Failed to fetch stats' });
  }
});

// Generic endpoint for different document types
const documentTypes = ['albums', 'artists', 'concerts', 'interviews'];
documentTypes.forEach(type => {
  app.get(`/api/${type}`, async (req, res) => {
    try {
      console.log(`/api/${type}`);
      const docType = type.slice(0, -1); // Remove 's' from plural
      const result = await findDocs(docType, req.query.p, req.query.q, req.query.s);
      res.json(result);
    } catch (error) {
      console.error(`Error in /api/${type}:`, error);
      res.status(500).json({ error: `Failed to fetch ${type}` });
    }
  });
});

app.get('/api/podcasts/:source?', async (req, res) => {
  try {
    const source = req.params.source;
    console.log(`/api/podcasts${source ? '/' + source : ''}`);
    
    const query = source ? { source } : {};
    const result = await collections.authors
      .find(query)
      .project({ source: 1, name: 1, cover: 1, lastEpisodeDate: 1 })
      .sort({ lastEpisodeDate: -1, name: 1 })
      .toArray();
    
    res.json(result);
  } catch (error) {
    console.error('Error in /api/podcasts:', error);
    res.status(500).json({ error: 'Failed to fetch podcasts' });
  }
});

app.get('/api/podcasts/:source/episodes', async (req, res) => {
  try {
    console.log(`/api/podcasts/${req.params.source}/episodes`);
    const result = await findDocs('podcast', req.query.p, req.query.q, req.params.source);
    res.json(result);
  } catch (error) {
    console.error('Error in /api/podcasts/episodes:', error);
    res.status(500).json({ error: 'Failed to fetch episodes' });
  }
});

app.get('/api/events/:id', async (req, res) => {
  try {
    console.log(`/api/events/${req.params.id}`);
    const events = await findEvents(req.params.id);
    res.json(events?.resultsPage?.results?.event || []);
  } catch (error) {
    console.error('Error in /api/events:', error);
    res.status(500).json({ error: 'Failed to fetch events' });
  }
});

// Spotify authentication routes
app.get('/api/login', (req, res) => {
  console.log('/api/login (login to Spotify...)');
  const scopes = [
    'user-library-read',
    'user-library-modify',
    'user-read-private',
    'user-read-playback-state',
    'user-modify-playback-state'
  ].join('%20');
  
  const authUrl = `https://accounts.spotify.com/authorize?` +
    `response_type=code&` +
    `scope=${scopes}&` +
    `client_id=${config.spotify.clientId}&` +
    `redirect_uri=${config.spotify.redirectUri}`;
  
  res.redirect(authUrl);
});

app.get('/api/login/spotify', async (req, res) => {
  try {
    if (req.query.error) {
      console.log('Login to Spotify failed:', req.query.error);
      return res.status(400).json({ error: req.query.error });
    }
    
    if (!req.query.code) {
      return res.status(400).json({ error: 'Authorization code not provided' });
    }
    
    console.log('Login to Spotify succeeded:', req.query.code);
    
    const tokenResponse = await axios.post('https://accounts.spotify.com/api/token', 
      new URLSearchParams({
        code: req.query.code,
        redirect_uri: config.spotify.redirectUri,
        grant_type: 'authorization_code'
      }), {
        headers: {
          'Authorization': `Basic ${Buffer.from(`${config.spotify.clientId}:${config.spotify.clientSecret}`).toString('base64')}`,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );
    
    const { access_token, refresh_token } = tokenResponse.data;
    res.cookie('spotify-token', access_token);
    res.cookie('spotify-refresh-token', refresh_token);
    res.redirect('/');
  } catch (error) {
    console.error('Error in Spotify login:', error);
    res.status(500).json({ error: 'Failed to authenticate with Spotify' });
  }
});

app.get('/api/login/refresh', async (req, res) => {
  try {
    if (!req.cookies['spotify-refresh-token']) {
      return res.status(400).json({ error: 'Refresh token not found' });
    }
    
    const tokenResponse = await axios.post('https://accounts.spotify.com/api/token',
      new URLSearchParams({
        refresh_token: req.cookies['spotify-refresh-token'],
        grant_type: 'refresh_token'
      }), {
        headers: {
          'Authorization': `Basic ${Buffer.from(`${config.spotify.clientId}:${config.spotify.clientSecret}`).toString('base64')}`,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );
    
    console.log('Refresh successful');
    const { access_token } = tokenResponse.data;
    res.cookie('spotify-token', access_token);
    res.json({ success: true });
  } catch (error) {
    console.error('Error refreshing token:', error);
    res.status(error.response?.status || 500).json({ 
      error: error.response?.data || 'Failed to refresh token' 
    });
  }
});

app.get('/api/:id', async (req, res) => {
  try {
    const result = await findDoc(req.params.id);
    res.json(result);
  } catch (error) {
    console.error('Error fetching document:', error);
    res.status(500).json({ error: 'Failed to fetch document' });
  }
});

app.get('/api/:id/links', async (req, res) => {
  try {
    const result = await findLinks(req.params.id);
    res.json(result);
  } catch (error) {
    console.error('Error fetching links:', error);
    res.status(500).json({ error: 'Failed to fetch links' });
  }
});

// Page rendering routes
app.get('/albums/:id', async (req, res) => {
  try {
    console.log(`Loading /albums/${req.params.id}`);
    const docs = await collections.docs
      .find({ 
        $and: [
          { type: 'album' }, 
          { id: req.params.id }
        ] 
      })
      .project({ artist: 1, title: 1, type: 1, cover: 1, coverM: 1, description: 1 })
      .toArray();
    
    if (docs && docs[0]) {
      const doc = docs[0];
      res.render('index', {
        title: `${doc.artist} - ${doc.title}`,
        type: `music:${doc.type}`,
        url: `https://humanbeats.appspot.com/albums/${req.params.id}`,
        cover: doc.coverM || doc.cover,
        description: doc.description
      });
    } else {
      res.status(404).render('index', {
        title: 'Album Not Found',
        type: 'music',
        url: 'https://humanbeats.appspot.com/',
        cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
        description: 'Album not found'
      });
    }
  } catch (error) {
    console.error('Error loading album page:', error);
    res.status(500).render('index', {
      title: 'Error',
      type: 'music',
      url: 'https://humanbeats.appspot.com/',
      cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
      description: 'Error loading album'
    });
  }
});

app.get('/shows/:source', async (req, res) => {
  try {
    console.log(`Loading /${req.params.source}`);
    const docs = await collections.authors
      .find({ source: req.params.source })
      .project({ source: 1, name: 1, cover: 1 })
      .toArray();
    
    if (docs && docs[0]) {
      const doc = docs[0];
      res.render('index', {
        title: `Human Beats - ${doc.name}`,
        type: 'music:podcast',
        url: `https://humanbeats.appspot.com/${req.params.source}`,
        cover: doc.cover,
        description: `${doc.name} podcasts`
      });
    } else {
      res.status(404).render('index', {
        title: 'Show Not Found',
        type: 'music',
        url: 'https://humanbeats.appspot.com/',
        cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
        description: 'Show not found'
      });
    }
  } catch (error) {
    console.error('Error loading show page:', error);
    res.status(500).render('index', {
      title: 'Error',
      type: 'music',
      url: 'https://humanbeats.appspot.com/',
      cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
      description: 'Error loading show'
    });
  }
});

app.get('/shows/:source/episodes/:id', async (req, res) => {
  try {
    const docs = await collections.docs
      .find({ 
        $and: [
          { type: 'podcast' }, 
          { id: req.params.id }
        ] 
      })
      .project({ artist: 1, title: 1, type: 1, cover: 1, coverM: 1, description: 1 })
      .toArray();
    
    if (docs && docs[0]) {
      const doc = docs[0];
      res.render('index', {
        title: `${doc.artist} - ${doc.title}`,
        type: `music:${doc.type}`,
        url: `https://humanbeats.appspot.com/episodes/${req.params.id}`,
        cover: doc.coverM || doc.cover,
        description: doc.description
      });
    } else {
      res.status(404).render('index', {
        title: 'Episode Not Found',
        type: 'music',
        url: 'https://humanbeats.appspot.com/',
        cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
        description: 'Episode not found'
      });
    }
  } catch (error) {
    console.error('Error loading episode page:', error);
    res.status(500).render('index', {
      title: 'Error',
      type: 'music',
      url: 'https://humanbeats.appspot.com/',
      cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
      description: 'Error loading episode'
    });
  }
});

// Default route
app.get('/*', (req, res) => {
  res.render('index', {
    title: 'Human Beats',
    type: 'music',
    url: 'https://humanbeats.appspot.com/',
    cover: 'https://storage.googleapis.com/humanbeats/humanbeats-wp.jpg',
    description: 'Music designed by humans, assembled by robots'
  });
});
// Database helper functions
async function findDoc(id) {
  try {
    const result = await collections.docs.find({ id }).toArray();
    return result.length > 0 ? [sanitizeDoc(result[0])] : [];
  } catch (error) {
    console.error('Error in findDoc:', error);
    throw error;
  }
}

async function findDocs(type, page, query, source) {
  try {
    const pageNum = Math.max(0, Number(page) || 0);
    const searchQuery = buildSearchQuery(type, query, source);
    
    const result = await collections.docs
      .find(searchQuery)
      .project({
        id: 1,
        type: 1,
        artist: 1,
        title: 1,
        cover: 1,
        coverL: 1,
        coverM: 1,
        coverS: 1,
        description: 1,
        date: 1
      })
      .skip(pageNum * config.pageSize)
      .limit(config.pageSize)
      .sort({ date: -1 })
      .toArray();
    
    return result;
  } catch (error) {
    console.error('Error in findDocs:', error);
    throw error;
  }
}

async function findEvents(id) {
  if (!id || id === 'na') {
    return null;
  }
  
  try {
    const response = await axios.get(
      `https://api.songkick.com/api/3.0/artists/mbid:${id}/calendar.json?apikey=${config.songkick.apiKey}`
    );
    return response.data;
  } catch (error) {
    console.error('Error while executing findEvents():', error);
    return null;
  }
}

async function findLinks(id) {
  try {
    const docs = await collections.docs.find({ id }).toArray();
    
    if (!docs || docs.length === 0) {
      return { albums: [], podcasts: [] };
    }
    
    const doc = docs[0];
    const artists = [];
    
    // Collect artist IDs
    if (doc.spartistid && doc.spartistid !== 'na') {
      artists.push(doc.spartistid);
    }
    
    if (doc.type === 'podcast' && doc.tracks) {
      doc.tracks.forEach(track => {
        if (track.spartistid && track.spartistid !== 'na') {
          artists.push(track.spartistid);
        }
      });
    }
    
    if (artists.length === 0) {
      return { albums: [], podcasts: [] };
    }
    
    // Find related albums and podcasts
    const [albums, podcasts] = await Promise.all([
      collections.docs
        .find({
          $and: [
            { type: 'album' },
            { spartistid: { $in: artists } }
          ]
        })
        .project({
          id: 1,
          type: 1,
          artist: 1,
          title: 1,
          cover: 1,
          coverL: 1,
          coverM: 1,
          coverS: 1,
          date: 1,
          year: 1,
          source: 1
        })
        .sort({ year: -1, artist: 1 })
        .toArray(),
      
      collections.docs
        .find({
          $and: [
            { type: 'podcast' },
            { 'tracks.spartistid': { $in: artists } }
          ]
        })
        .project({
          id: 1,
          type: 1,
          artist: 1,
          title: 1,
          cover: 1,
          coverL: 1,
          coverM: 1,
          coverS: 1,
          date: 1,
          year: 1,
          source: 1
        })
        .sort({ date: -1, artist: 1 })
        .limit(config.pageSize)
        .toArray()
    ]);
    
    // Filter out the current document
    const filteredAlbums = albums.filter(album => album.id !== doc.id);
    const filteredPodcasts = podcasts.filter(podcast => podcast.id !== doc.id);
    
    return {
      albums: filteredAlbums,
      podcasts: filteredPodcasts
    };
  } catch (error) {
    console.error('Error in findLinks:', error);
    return { albums: [], podcasts: [] };
  }
}

// Start server
async function startServer() {
  try {
    await initializeDatabase();
    
    app.listen(config.port, () => {
      console.log(`App listening on port ${config.port}`);
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
}

// Only start server if this file is run directly
if (require.main === module) {
  startServer();
}

module.exports = app;
