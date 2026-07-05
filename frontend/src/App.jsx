import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = 'http://localhost:8080'

const emptyForm = {
  originalUrl: '',
  category: '',
  password: '',
  expiryDate: '',
}

function App() {
  const [form, setForm] = useState(emptyForm)
  const [urls, setUrls] = useState([])
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('')
  const [editingId, setEditingId] = useState(null)
  const [shortUrl, setShortUrl] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const stats = useMemo(() => {
    return {
      total: urls.length,
      clicks: urls.reduce((sum, item) => sum + item.clickCount, 0),
      active: urls.filter((item) => item.active).length,
      favourites: urls.filter((item) => item.favourite).length,
    }
  }, [urls])

  async function request(path, options = {}) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    })

    if (!response.ok) {
      throw new Error(`Request failed with status ${response.status}`)
    }

    const text = await response.text()
    return text ? JSON.parse(text) : null
  }

  async function loadUrls() {
    setLoading(true)
    try {
      const data = await request('/api/url/all')
      setUrls(data)
      setMessage('')
    } catch (error) {
      setMessage('Backend connect nahi ho raha. Spring Boot server run hai kya?')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUrls()
  }, [])

  function updateField(event) {
    setForm((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }))
  }

  function buildPayload() {
    return {
      originalUrl: form.originalUrl.trim(),
      category: form.category.trim() || null,
      password: form.password.trim() || null,
      expiryDate: form.expiryDate ? form.expiryDate : null,
    }
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!form.originalUrl.trim()) {
      setMessage('Long URL required hai.')
      return
    }

    try {
      const payload = buildPayload()
      if (editingId) {
        await request(`/api/url/${editingId}`, {
          method: 'PUT',
          body: JSON.stringify(payload),
        })
        setMessage('URL update ho gaya.')
      } else {
        const data = await request('/api/url/shorten', {
          method: 'POST',
          body: JSON.stringify(payload),
        })
        setShortUrl(data.shortUrl)
        setMessage('Short link generate ho gaya.')
      }

      setForm(emptyForm)
      setEditingId(null)
      await loadUrls()
    } catch (error) {
      setMessage('Request fail ho gayi. URL format aur backend check karo.')
    }
  }

  async function handleSearch(event) {
    event.preventDefault()

    try {
      if (!search.trim()) {
        await loadUrls()
        return
      }

      const data = await request(`/api/url/search?keyword=${encodeURIComponent(search.trim())}`)
      setUrls(data)
      setMessage(`${data.length} result mile.`)
    } catch (error) {
      setMessage('Search fail ho gayi.')
    }
  }

  async function handleCategoryFilter(value) {
    setCategoryFilter(value)

    try {
      if (!value) {
        await loadUrls()
        return
      }

      const data = await request(`/api/url/category?name=${encodeURIComponent(value)}`)
      setUrls(data)
    } catch (error) {
      setMessage('Category filter fail ho gaya.')
    }
  }

  async function copyText(value) {
    await navigator.clipboard.writeText(value)
    setMessage('Copied.')
  }

  function startEdit(url) {
    setEditingId(url.id)
    setForm({
      originalUrl: url.originalUrl,
      category: url.category || '',
      password: '',
      expiryDate: url.expiryDate ? url.expiryDate.slice(0, 16) : '',
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function performAction(path, successMessage, method = 'PATCH') {
    try {
      await request(path, { method })
      setMessage(successMessage)
      await loadUrls()
    } catch (error) {
      setMessage('Action fail ho gaya.')
    }
  }

  const categories = Array.from(
    new Set(urls.map((item) => item.category).filter(Boolean)),
  )

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Smart URL Shortener</p>
          <h1>Short links, clean dashboard.</h1>
        </div>
        <button className="ghost-button" type="button" onClick={loadUrls}>
          Refresh
        </button>
      </header>

      <section className="panel hero-panel">
        <form className="shorten-form" onSubmit={handleSubmit}>
          <label>
            Long URL
            <input
              name="originalUrl"
              placeholder="https://www.example.com/very/long/link"
              value={form.originalUrl}
              onChange={updateField}
            />
          </label>

          <div className="form-grid">
            <label>
              Category
              <input
                name="category"
                placeholder="Shopping"
                value={form.category}
                onChange={updateField}
              />
            </label>
            <label>
              Password
              <input
                name="password"
                placeholder="Optional"
                value={form.password}
                onChange={updateField}
              />
            </label>
            <label>
              Expiry
              <input
                name="expiryDate"
                type="datetime-local"
                value={form.expiryDate}
                onChange={updateField}
              />
            </label>
          </div>

          <div className="button-row">
            <button className="primary-button" type="submit">
              {editingId ? 'Update Link' : 'Generate Short Link'}
            </button>
            {editingId && (
              <button
                className="ghost-button"
                type="button"
                onClick={() => {
                  setEditingId(null)
                  setForm(emptyForm)
                }}
              >
                Cancel
              </button>
            )}
          </div>
        </form>

        <aside className="result-box">
          <span>Latest short URL</span>
          <strong>{shortUrl || 'Generate a link to see it here'}</strong>
          {shortUrl && (
            <button type="button" onClick={() => copyText(shortUrl)}>
              Copy
            </button>
          )}
        </aside>
      </section>

      {message && <div className="notice">{message}</div>}

      <section className="stats-grid">
        <Stat label="Total links" value={stats.total} />
        <Stat label="Total clicks" value={stats.clicks} />
        <Stat label="Active" value={stats.active} />
        <Stat label="Favourites" value={stats.favourites} />
      </section>

      <section className="panel">
        <div className="dashboard-head">
          <div>
            <h2>My URLs</h2>
            <p>{loading ? 'Loading...' : `${urls.length} links visible`}</p>
          </div>
          <form className="filters" onSubmit={handleSearch}>
            <input
              placeholder="Search URL"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
            <select
              value={categoryFilter}
              onChange={(event) => handleCategoryFilter(event.target.value)}
            >
              <option value="">All categories</option>
              {categories.map((category) => (
                <option key={category} value={category}>
                  {category}
                </option>
              ))}
            </select>
            <button type="submit">Search</button>
          </form>
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Original URL</th>
                <th>Short URL</th>
                <th>Meta</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {urls.map((url) => (
                <tr key={url.id}>
                  <td>
                    <div className="url-cell">
                      <strong>{url.originalUrl}</strong>
                      <span>{url.category || 'No category'}</span>
                    </div>
                  </td>
                  <td>
                    <button
                      className="link-button"
                      type="button"
                      onClick={() => copyText(url.shortUrl)}
                    >
                      {url.shortUrl}
                    </button>
                  </td>
                  <td>
                    <div className="chips">
                      <span>{url.clickCount} clicks</span>
                      <span>{url.active ? 'Active' : 'Disabled'}</span>
                      {url.favourite && <span>Favourite</span>}
                      {url.passwordProtected && <span>Locked</span>}
                    </div>
                  </td>
                  <td>
                    <div className="action-grid">
                      <button type="button" onClick={() => startEdit(url)}>
                        Edit
                      </button>
                      <button
                        type="button"
                        onClick={() =>
                          performAction(`/api/url/${url.id}/toggle-status`, 'Status update ho gaya.')
                        }
                      >
                        {url.active ? 'Disable' : 'Enable'}
                      </button>
                      <button
                        type="button"
                        onClick={() =>
                          performAction(`/api/url/${url.id}/favourite`, 'Favourite update ho gaya.')
                        }
                      >
                        {url.favourite ? 'Unstar' : 'Star'}
                      </button>
                      <button
                        className="danger"
                        type="button"
                        onClick={() =>
                          performAction(`/api/url/${url.id}`, 'URL delete ho gaya.', 'DELETE')
                        }
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {!urls.length && (
                <tr>
                  <td colSpan="4" className="empty-state">
                    No links found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  )
}

function Stat({ label, value }) {
  return (
    <div className="stat-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  )
}

export default App
