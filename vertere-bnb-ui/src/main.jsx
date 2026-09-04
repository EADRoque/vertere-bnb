// main.jsx
// This is the entry point that boots up the React app and mounts it
// into the page.

import { StrictMode } from 'react' //dev-only helper that highlights potential issues by intentionally double-running some code
import { createRoot } from 'react-dom/client' //hooks React up to an actual DOM element
import './index.css'
import App from './App.jsx' //the app's routes/pages live here
import { AuthProvider } from './context/AuthContext.jsx' //makes the logged-in user available everywhere in the app

createRoot(document.getElementById('root')).render( //renders into the <div id="root"> in index.html
  <StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </StrictMode>,
)
